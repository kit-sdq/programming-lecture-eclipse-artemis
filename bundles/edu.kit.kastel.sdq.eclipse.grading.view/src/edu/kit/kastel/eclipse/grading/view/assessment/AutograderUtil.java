/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.grading.view.assessment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.eclipse.common.api.PreferenceConstants;
import edu.kit.kastel.eclipse.common.api.controller.IAssessmentController;
import edu.kit.kastel.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.common.view.activator.CommonActivator;
import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;
import edu.kit.kastel.eclipse.grading.view.activator.Activator;

public class AutograderUtil {
	private static final ILog LOG = Platform.getLog(AutograderUtil.class);

	public static void runAutograder(IAssessmentController assessmentController, Path path, Consumer<Boolean> onCompletion, boolean forceExecution) {
		if (!forceExecution && !assessmentController.getAnnotations().isEmpty()) {
			LOG.info("Skipping autograder as there already annotation present");
			return; // Don't run the autograder if there are already annotations
		}

		Job.create("Autograder", monitor -> {
			try {
				// Store the current exercise
				var submission = assessmentController.getSubmission();

				// Read the configuration
				Map<String, String> config = getConfig();
				String autograderConfig = "[" + config.keySet().stream().collect(Collectors.joining(", ")) + "]";

				monitor.beginTask("Autograder", 8); // Download, Compile, PMD, CPD, SpotBugs, Spoon, integrated, parsing

				monitor.subTask("Downloading Autograder JAR");
				LOG.info("Downloading autograder JAR");
				Path autograderJar = maybeDownloadAutograderRelease();
				monitor.worked(1);

				monitor.subTask("Running Autograder checks");
				ProcessBuilder processBuilder = new ProcessBuilder("java", "-DFile.Encoding=UTF-8", "-jar", autograderJar.toAbsolutePath().toString(),
						autograderConfig, path.toAbsolutePath().toString(), "--static-only", "--output-json", "--pass-config", "--java-version", "17");
				var process = processBuilder.start();
				Scanner autograderOutput = new Scanner(process.getInputStream(), StandardCharsets.UTF_8);

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
					LOG.error("Autograder failed: " + errorOutput);
					onCompletion.accept(false);
					Display.getDefault().asyncExec(() -> MessageDialog.openWarning(AssessmentUtilities.getWindowsShell(), "Autograder failed",
							"Autograder failed. Please assess the submission normally. Additional information can be found in the Eclipse log"));
				} else if (assessmentController.getSubmission().getSubmissionId() != submission.getSubmissionId()) {
					Display.getDefault().asyncExec(() -> MessageDialog.openWarning(AssessmentUtilities.getWindowsShell(), "Submission changed",
							"Autograder completed successfully, but the current submission has changed during analysis, so no annotations will be created."));
				} else {
					LOG.info("Autograder completed successfully");

					List<AutograderAnnotation> annotations = Arrays.asList(new ObjectMapper().readValue(problems, AutograderAnnotation[].class));

					for (AutograderAnnotation annotation : annotations) {
						var type = mapAnnotation(assessmentController, annotation, config);
						if (type.isPresent()) {
							String id = IAnnotation.createID();
							assessmentController.addAnnotation(id, type.get(), annotation.startLine() - 1, annotation.endLine() - 1, "src/" + annotation.file(),
									annotation.message(), type.get().isCustomPenalty() ? 0.0 : null);
							AssessmentUtilities.createMarkerByAnnotation(assessmentController.getAnnotationById(id).get(),
									Activator.getDefault().getSystemwideController().getCurrentProjectName(), "assignment/");
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
				LOG.error("Autograder failed: " + ex.getMessage(), ex);
				Display.getDefault().asyncExec(() -> MessageDialog.openWarning(AssessmentUtilities.getWindowsShell(), "Autograder failed",
						"Autograder failed. Please assess the submission normally. Additional information can be found in the Eclipse log"));
			}
		}).schedule();
	}

	public static Path maybeDownloadAutograderRelease() throws IOException {
		if (!CommonActivator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.AUTOGRADER_DOWNLOAD_JAR)) {
			return Path.of(CommonActivator.getDefault().getPreferenceStore().getString(PreferenceConstants.AUTOGRADER_JAR_PATH));
		}

		URLConnection connection = new URL("https://github.com/Feuermagier/autograder/releases/latest").openConnection();
		connection.connect();
		var inputStream = connection.getInputStream(); // Open stream to force redirect to the latest release
		String[] components = connection.getURL().getFile().split("/");
		String tag = components[components.length - 1];
		inputStream.close();

		Path existingJAR = Path.of(CommonActivator.getDefault().getPreferenceStore().getString(PreferenceConstants.AUTOGRADER_DOWNLOADED_JAR_PATH));
		if (!Files.exists(existingJAR) || !existingJAR.getFileName().toString().startsWith(tag)) {
			Files.deleteIfExists(existingJAR);
			Display.getDefault().asyncExec(() -> MessageDialog.openInformation(AssessmentUtilities.getWindowsShell(), "Downloading Autograder",
					"Downloading Autograder " + tag + ". This may take a moment. You can safely close this window."));
			existingJAR = downloadAutograderRelease(tag);
		} else {
			LOG.info("Skipping autograder JAR download as most recent one is already present at " + existingJAR.toAbsolutePath().toString());
		}

		return existingJAR;
	}

	private static Path downloadAutograderRelease(String version) {
		try {
			Path targetPath = Files.createTempFile(version + "_autograder_jar", ".jar");
			LOG.info("Downloading autograder JAR with version/tag " + version + " to " + targetPath.toAbsolutePath().toString());
			Files.deleteIfExists(targetPath);
			Files.createFile(targetPath);
			URL url = new URL("https://github.com/Feuermagier/autograder/releases/latest/download/autograder-cmd.jar");
			ReadableByteChannel channel = Channels.newChannel(url.openStream());
			FileOutputStream outStream = new FileOutputStream(targetPath.toFile());
			outStream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
			CommonActivator.getDefault().getPreferenceStore().setValue(PreferenceConstants.AUTOGRADER_DOWNLOADED_JAR_PATH,
					targetPath.toAbsolutePath().toString());
			return targetPath;
		} catch (IOException e) {
			LOG.error("Failed to download the autograder JAR", e);
			return null;
		}
	}

	private static Optional<IMistakeType> mapAnnotation(IAssessmentController assessmentController, AutograderAnnotation annotation,
			Map<String, String> config) {
		String id = config.get(annotation.type());
		return assessmentController.getMistakes().stream().filter(m -> m.getIdentifier().equals(id)).findAny()
				.or(() -> assessmentController.getMistakes().stream().filter(m -> m.getIdentifier().equals("custom")).findAny());
	}

	public static Map<String, String> getConfig() throws IOException {
		Path autograderConfigPath = Path.of(CommonActivator.getDefault().getPreferenceStore().getString(PreferenceConstants.AUTOGRADER_CONFIG_PATH));
		return new ObjectMapper().readValue(Files.readString(autograderConfigPath), new TypeReference<Map<String, String>>() {
		});
	}

	public record AutograderAnnotation(String type, String message, String file, int startLine, int endLine) {

	}
}
