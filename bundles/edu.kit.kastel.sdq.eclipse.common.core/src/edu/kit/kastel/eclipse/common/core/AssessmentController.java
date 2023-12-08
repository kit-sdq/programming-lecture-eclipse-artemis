/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.core;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import edu.kit.kastel.eclipse.common.api.PreferenceConstants;
import edu.kit.kastel.eclipse.common.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.eclipse.common.api.controller.AbstractController;
import edu.kit.kastel.eclipse.common.api.controller.IAssessmentController;
import edu.kit.kastel.eclipse.common.core.artemis.WorkspaceUtil;
import edu.kit.kastel.sdq.artemis4j.api.artemis.Course;
import edu.kit.kastel.sdq.artemis4j.api.artemis.Exercise;
import edu.kit.kastel.sdq.artemis4j.api.artemis.assessment.Submission;
import edu.kit.kastel.sdq.artemis4j.api.grading.IAnnotation;
import edu.kit.kastel.sdq.artemis4j.api.grading.IMistakeType;
import edu.kit.kastel.sdq.artemis4j.api.grading.IRatingGroup;
import edu.kit.kastel.sdq.artemis4j.grading.artemis.AnnotationDeserializer;
import edu.kit.kastel.sdq.artemis4j.grading.artemis.AnnotationMapper;
import edu.kit.kastel.sdq.artemis4j.grading.config.ExerciseConfig;
import edu.kit.kastel.sdq.artemis4j.grading.config.GradingConfig;
import edu.kit.kastel.sdq.artemis4j.grading.config.JsonFileConfig;
import edu.kit.kastel.sdq.artemis4j.grading.model.annotation.AnnotationException;
import edu.kit.kastel.sdq.artemis4j.grading.model.annotation.AnnotationManagement;

public class AssessmentController extends AbstractController implements IAssessmentController {

	private final GradingSystemwideController systemWideController;

	private final Course course;
	private final Exercise exercise;
	private final Submission submission;

	private GradingConfig gradingConfig;
	private AnnotationManagement annotations;

	/**
	 * Protected, because the way to get a specific assessment controller should be
	 * over a SystemwideController.
	 */
	protected AssessmentController(GradingSystemwideController systemWideController, Course course, Exercise exercise, Submission submission) {
		super(systemWideController.getViewInteractionHandler());
		this.systemWideController = systemWideController;

		this.course = course;
		this.exercise = exercise;
		this.submission = submission;

		this.annotations = new AnnotationManagement();
		this.gradingConfig = this.loadGradingDAO();

		try {
			ExerciseConfig exerciseConfig = this.gradingConfig.getExerciseConfig(this.exercise);
			if (!exerciseConfig.getAllowedExercises().isEmpty() && !exerciseConfig.getAllowedExercises().contains(this.exercise.getExerciseId())) {
				// using interaction handler of the system wide controller, as the own
				// interaction handler is not set during the constructor
				systemWideController.getViewInteractionHandler().warn("""
						You are using a configuration file not designed for this exercise!
						Your file is "%s", however you are correcting exercise "%s"!
						Please double check your settings!
						""".formatted(exerciseConfig.getShortName(), this.exercise.getShortName()));
			}
		} catch (IllegalStateException | IOException e) {
			this.error("Exercise Config not parseable: " + e.getMessage(), e);
		}

		try {
			this.initializeWithDeserializedAnnotations();
		} catch (IOException e) {
			this.warn("Deserializing Annotations from Artemis failed (most likely none were present)!");
		}
	}

	private GradingConfig loadGradingDAO() {
		return new JsonFileConfig(new File(this.systemWideController.getPreferences().getString(PreferenceConstants.GRADING_ABSOLUTE_CONFIG_PATH)));
	}

	@Override
	public void addAnnotation(String annotationId, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName, String customMessage,
			Double customPenalty) {
		try {
			this.annotations.addAnnotation(annotationId, mistakeType, startLine, endLine, fullyClassifiedClassName, customMessage, customPenalty);
		} catch (AnnotationException e) {
			this.error(e.getMessage(), e);
		}

	}

	@Override
	public void deleteEclipseProject(IProjectFileNamingStrategy projectNaming) {
		final String projectName = projectNaming
				.getProjectFileInWorkspace(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile(), this.exercise, this.submission).getName();
		try {
			WorkspaceUtil.deleteEclipseProject(projectName);
		} catch (CoreException | IOException e) {
			this.error("Eclipse Exception occurred while trying to delete project: " + e.getMessage(), e);
		}

	}

	@Override
	public List<IAnnotation> getAnnotations() {
		return this.annotations.getAnnotations().stream().toList();
	}

	@Override
	public List<IAnnotation> getAnnotationsByMistakeType(IMistakeType mistakeType) {
		return this.annotations.getAnnotations().stream().filter(annotation -> annotation.getMistakeType().equals(mistakeType)).toList();
	}

	@Override
	public Optional<IAnnotation> getAnnotationById(String id) {
		return this.annotations.getAnnotations().stream().filter(annotation -> annotation.getUUID().equals(id)).findFirst();
	}

	@Override
	public Exercise getExercise() {
		return this.exercise;
	}

	@Override
	public Submission getSubmission() {
		return this.submission;
	}

	@Override
	public List<IMistakeType> getMistakes() {
		try {
			return this.gradingConfig.getExerciseConfig(this.exercise).getIMistakeTypes();
		} catch (IllegalStateException | IOException e) {
			this.error("Exercise Config not parseable: " + e.getMessage(), e);
			return List.of();
		}
	}

	@Override
	public boolean isPositiveFeedbackAllowed() {
		try {
			return this.gradingConfig.getExerciseConfig(this.exercise).isPositiveFeedbackAllowed();
		} catch (IllegalStateException | IOException e) {
			this.error("Exercise Config not parseable: " + e.getMessage(), e);
			return true;
		}
	}

	@Override
	public IRatingGroup getRatingGroupById(String id) {
		Optional<IRatingGroup> ratingGroupOptional = this.getRatingGroups().stream().filter(ratingGroup -> ratingGroup.getIdentifier().equals(id)).findFirst();
		if (ratingGroupOptional.isPresent()) {
			return ratingGroupOptional.get();
		}
		this.error("Rating Group \"" + id + "\" not found in config!", null);
		return null;
	}

	@Override
	public List<IRatingGroup> getRatingGroups() {
		try {
			return this.gradingConfig.getExerciseConfig(this.exercise).getIRatingGroups();
		} catch (IllegalStateException | IOException e) {
			this.error("Exercise Config not parseable: " + e.getMessage(), e);
			return List.of();
		}
	}

	@Override
	public String getTooltipForMistakeType(String languageKey, IMistakeType mistakeType) {
		List<IAnnotation> annotations = this.getAnnotationsByMistakeType(mistakeType);
		return mistakeType.getTooltip(languageKey, annotations);
	}

	private void initializeWithDeserializedAnnotations() throws IOException {
		final AnnotationDeserializer annotationDeserializer = new AnnotationDeserializer(this.getMistakes());
		var allFeedbacksGottenFromLocking = this.systemWideController.getArtemisController().getAllFeedbacksGottenFromLocking(this.submission);
		if (allFeedbacksGottenFromLocking == null) {
			throw new IOException("No feedbacks gotten from locking could be acquired.");
		}

		for (IAnnotation annotation : annotationDeserializer.deserialize(allFeedbacksGottenFromLocking)) {
			this.addAnnotation(annotation.getUUID(), annotation.getMistakeType(), annotation.getStartLine(), annotation.getEndLine(),
					annotation.getClassFilePath(), annotation.getCustomMessage().orElse(null), annotation.getCustomPenalty().orElse(null));
		}
	}

	@Override
	public void modifyAnnotation(String annatationId, String customMessage, Double customPenalty) {
		this.annotations.modifyAnnotation(annatationId, customMessage, customPenalty);
	}

	@Override
	public void removeAnnotation(String annotationId) {
		this.annotations.removeAnnotation(annotationId);
	}

	@Override
	public void resetAndRestartAssessment(IProjectFileNamingStrategy projectNaming) {
		this.deleteEclipseProject(projectNaming);
		this.systemWideController.getArtemisController().startAssessment(this.submission);
		this.systemWideController.downloadExerciseAndSubmission(this.course, this.exercise, this.submission, projectNaming);

		this.annotations = new AnnotationManagement();
		this.gradingConfig = this.loadGradingDAO();

		try {
			this.initializeWithDeserializedAnnotations();
		} catch (IOException e) {
			this.info("Deserializing Annotations from Artemis failed: " + e.getMessage());
		}
	}

	@Override
	public double getCurrentPenaltyForRatingGroup(IRatingGroup ratingGroup) {
		AnnotationMapper mapper = //
				new AnnotationMapper(this.exercise, this.submission, this.getAnnotations(), this.getRatingGroups(), null, null);
		return mapper.calculatePointsForRatingGroup(ratingGroup).points();
	}
}
