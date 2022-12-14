/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.assessment;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.eclipse.common.api.controller.IAssessmentController;
import edu.kit.kastel.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;
import edu.kit.kastel.eclipse.grading.view.activator.Activator;

public class AutograderUtil {
	private static final ILog LOG = Platform.getLog(AutograderUtil.class);

	public static void runAutograder(IAssessmentController assessmentController, Path path, Consumer<Boolean> onCompletion) {
		if (!assessmentController.getAnnotations().isEmpty()) {
			return; // Don't run the autograder if there are already annotations
		}

		Job.create("Autograder", monitor -> {
			try {
				monitor.beginTask("Autograder", 7); // Compile, PMD, CPD, SpotBugs, Spoon, integrated, parsing

				ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", getResource("resources/autograder-cmd.jar").getAbsolutePath(),
						getResource("resources/autograder_config.yaml").getAbsolutePath(), path.toString(), "-s", "--output-json");
				var process = processBuilder.start();
				Scanner autograderOutput = new Scanner(process.getInputStream());

				LOG.info("Autograder started");

				String problems = "[]";
				while (autograderOutput.hasNext() && process.isAlive()) {
					String line = autograderOutput.nextLine();
					if (line.equals(">> Problems <<")) {
						problems = autograderOutput.nextLine();
					} else {
						monitor.worked(1);
					}
				}

				monitor.setTaskName("Parsing annotations");
				String errorOutput = new String(process.getErrorStream().readAllBytes());
				if (!errorOutput.isBlank()) {
					LOG.warn("Autograder failed: " + errorOutput);
					onCompletion.accept(false);
					Display.getDefault().asyncExec(() -> MessageDialog.openWarning(AssessmentUtilities.getWindowsShell(), "Autograder failed",
							"Autograder failed. Please assess the submission normally. Additional information can be found in the Eclipse log"));
				} else {
					LOG.info("Autograder completed successfully");

					List<AutograderAnnotation> annotations = Arrays.asList(new ObjectMapper().readValue(problems, AutograderAnnotation[].class));

					for (AutograderAnnotation annotation : annotations) {
						var type = mapAnnotation(assessmentController, annotation);
						if (type.isPresent()) {
							String id = IAnnotation.createID();
							assessmentController.addAnnotation(id, type.get(), annotation.startLine() - 1, annotation.endLine() - 1,
									annotation.file().replace("/", "."), annotation.message(), type.get().isCustomPenalty() ? 0.0 : null);
							AssessmentUtilities.createMarkerByAnnotation(assessmentController.getAnnotationById(id).get(),
									Activator.getDefault().getSystemwideController().getCurrentProjectName(), "assignment/src/");
						} else {
							LOG.warn("No mistake type found for autograder annotation type " + annotation.type());
						}
					}
					onCompletion.accept(true);

					monitor.done();
					Display.getDefault().asyncExec(() -> MessageDialog.openInformation(AssessmentUtilities.getWindowsShell(), "Autograder succeeded",
							String.format("Autograder found %d issues. Please check that there are no false-positives.", annotations.size())));
				}
			} catch (Exception ex) {
				LOG.warn(ex.getMessage());
			}
		}).schedule();
	}

	private static File getResource(String name) throws IOException {
		Bundle bundle = Platform.getBundle("edu.kit.kastel.sdq.eclipse.grading.view");
		URL url = FileLocator.toFileURL(FileLocator.find(bundle, new org.eclipse.core.runtime.Path(name)));
		try {
			return URIUtil.toFile(URIUtil.toURI(url));
		} catch (URISyntaxException ex) {
			throw new IOException(ex);
		}
	}

	private static Optional<IMistakeType> mapAnnotation(IAssessmentController assessmentController, AutograderAnnotation annotation) {
		String id = switch (annotation.type()) {
		case "DEPRECATED_COLLECTION_USED" -> "customComment";
		case "COLLECTION_IS_EMPTY_REIMPLEMENTED" -> "unnecessaryComplex";
		case "STRING_IS_EMPTY_REIMPLEMENTED" -> "unnecessaryComplex";
		case "INVALID_AUTHOR_TAG" -> "customComment";
		case "COMMENTED_OUT_CODE" -> "todo";
		case "INCONSISTENT_COMMENT_LANGUAGE" -> "wrongLanguage";
		case "INVALID_COMMENT_LANGUAGE" -> "wrongLanguage";
		case "JAVADOC_STUB_DESCRIPTION" -> "jdTrivial";
		case "JAVADOC_STUB_PARAMETER_TAG" -> "jdTrivial";
		case "JAVADOC_STUB_RETURN_TAG" -> "jdTrivial";
		case "JAVADOC_STUB_THROWS_TAG" -> "jdTrivial";
		case "JAVADOC_MISSING_PARAMETER_TAG" -> "jdTrivial";
		case "JAVADOC_UNKNOWN_PARAMETER_TAG" -> "jdTrivial";
		case "JAVADOC_INCOMPLETE_RETURN_TAG" -> "jdTrivial";
		case "UNUSED_DIAMOND_OPERATOR" -> "customComment";
		case "EXPLICITLY_EXTENDS_OBJECT" -> "unnecessaryComplex";
		case "FOR_WITH_MULTIPLE_VARIABLES" -> "complexCode";
		case "REDUNDANT_DEFAULT_CONSTRUCTOR" -> "emptyConstructor";
		case "REDUNDANT_IF_FOR_BOOLEAN" -> "unnecessaryComplex";
		case "REDUNDANT_MODIFIER" -> "unnecessaryComplex";
		case "REDUNDANT_VOID_RETURN" -> "unnecessaryComplex";
		case "REDUNDANT_SELF_ASSIGNMENT" -> "unnecessaryComplex";
		case "REDUNDANT_LOCAL_BEFORE_RETURN" -> "unnecessaryComplex";
		case "UNUSED_IMPORT" -> "unnecessaryComplex";
		case "PRIMITIVE_WRAPPER_INSTANTIATION" -> "customComment";
		case "ASSERT" -> "assertIF";
		case "EXCEPTION_PRINT_STACK_TRACE" -> "customComment";
		case "CUSTOM_EXCEPTION_INHERITS_RUNTIME_EXCEPTION" -> "customComment";
		case "CUSTOM_EXCEPTION_INHERITS_ERROR" -> "customComment";
		case "EMPTY_CATCH" -> "emptyBlock";
		case "EXCEPTION_CAUGHT_IN_SURROUNDING_BLOCK" -> "exceptionControlFlow";
		case "RUNTIME_EXCEPTION_OR_ERROR_CAUGHT" -> "customComment";
		case "OBJECTS_COMPARED_VIA_TO_STRING" -> "javaAPI";
		case "CONSTANT_NOT_STATIC_OR_NOT_UPPER_CAMEL_CASE" -> "identifierNaming";
		case "CONSTANT_IN_INTERFACE" -> "customComment";
		case "DUPLICATE_CODE" -> "codeCopyHelper";
		case "REASSIGNED_PARAMETER" -> "customComment";
		case "DOUBLE_BRACE_INITIALIZATION" -> "customComment";
		case "NON_COMPLIANT_EQUALS" -> "customComment";
		case "INSTANCE_FIELD_CAN_BE_LOCAL" -> "unnecessaryComplex";
		case "FOR_CAN_BE_FOREACH" -> "wrongLoopType";
		case "OVERRIDE_ANNOTATION_MISSING" -> "customComment";
		case "SYSTEM_SPECIFIC_LINE_BREAK" -> "lineSeparator";
		case "BOOLEAN_GETTER_NOT_CALLED_IS" -> "identifierNaming";
		case "MEANINGLESS_CONSTANT_NAME" -> "meaninglessConstants";
		case "CONFUSING_IDENTIFIER" -> "identifierNaming";
		case "SINGLE_LETTER_LOCAL_NAME" -> "identifierNaming";
		case "IDENTIFIER_IS_ABBREVIATED_TYPE" -> "identifierNaming";
		case "CONCRETE_COLLECTION_AS_FIELD_OR_RETURN_VALUE" -> "interfaceAgainst";
		case "LIST_NOT_COPIED_IN_GETTER" -> "getterSetter";
		case "METHOD_USES_PLACEHOLDER_IMPLEMENTATION" -> "comment";
		case "UTILITY_CLASS_NOT_FINAL" -> "utilityPrivate";
		case "UTILITY_CLASS_INVALID_CONSTRUCTOR" -> "utilityPrivate";
		case "UTILITY_CLASS_MUTABLE_FIELD" -> "utilityPrivate";
		case "DEFAULT_PACKAGE_USED" -> "customComment";
		case "EMPTY_BLOCK" -> "emptyBlock";
		case "UNUSED_CODE_ELEMENT" -> "unused";
		case "REPEATED_MATH_OPERATION" -> "unnecessaryComplex";
		default -> "customComment";
		};
		assessmentController.getMistakes();
		return assessmentController.getMistakes().stream().filter(m -> m.getId().equals(id)).findAny()
				.or(() -> assessmentController.getMistakes().stream().filter(m -> m.getId().equals("customComment")).findAny());
	}

	public record AutograderAnnotation(String type, String message, String file, int startLine, int endLine) {

	}
}
