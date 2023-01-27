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
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;
import edu.kit.kastel.eclipse.common.api.controller.AbstractController;
import edu.kit.kastel.eclipse.common.api.controller.IAssessmentController;
import edu.kit.kastel.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.common.api.model.IRatingGroup;
import edu.kit.kastel.eclipse.common.core.artemis.AnnotationDeserializer;
import edu.kit.kastel.eclipse.common.core.artemis.AnnotationMapper;
import edu.kit.kastel.eclipse.common.core.artemis.WorkspaceUtil;
import edu.kit.kastel.eclipse.common.core.config.ExerciseConfig;
import edu.kit.kastel.eclipse.common.core.config.ExerciseConfigConverterException;
import edu.kit.kastel.eclipse.common.core.config.GradingDAO;
import edu.kit.kastel.eclipse.common.core.config.JsonFileConfigDAO;
import edu.kit.kastel.eclipse.common.core.model.annotation.AnnotationDAO;
import edu.kit.kastel.eclipse.common.core.model.annotation.AnnotationException;
import edu.kit.kastel.eclipse.common.core.model.annotation.IAnnotationDAO;

public class AssessmentController extends AbstractController implements IAssessmentController {

	private final GradingSystemwideController systemWideController;

	private final ICourse course;
	private final IExercise exercise;
	private final ISubmission submission;

	private GradingDAO gradingDAO;
	private IAnnotationDAO annotationDAO;

	/**
	 * Protected, because the way to get a specific assessment controller should be
	 * over a SystemwideController.
	 *
	 * @param configFile   path to the config file
	 * @param exerciseName the shortName of the exercise (must be same in the config
	 *                     file).
	 */
	protected AssessmentController(GradingSystemwideController systemWideController, ICourse course, IExercise exercise, ISubmission submission) {
		super(systemWideController.getViewInteractionHandler());
		this.systemWideController = systemWideController;

		this.course = course;
		this.exercise = exercise;
		this.submission = submission;

		this.annotationDAO = new AnnotationDAO();
		this.gradingDAO = loadGradingDAO();

		try {
			ExerciseConfig exerciseConfig = this.gradingDAO.getExerciseConfig();
			if (!exerciseConfig.getAllowedExercises().isEmpty() && !exerciseConfig.getAllowedExercises().contains(this.exercise.getExerciseId())) {
				// using interaction handler of the system wide controller, as the own
				// interaction handler is not set during the constructor
				systemWideController.getViewInteractionHandler().warn("""
						You are using a configuration file not designed for this exercise!\n
						Your file is \"%s\", however you are correcting exercise \"%s\"!\n
						Please double check your settings!
						""".formatted(exerciseConfig.getShortName(), this.exercise.getShortName()));
			}
		} catch (ExerciseConfigConverterException | IOException e) {
			this.error("Exercise Config not parseable: " + e.getMessage(), e);
		}

		try {
			this.initializeWithDeserializedAnnotations();
		} catch (IOException e) {
			this.warn("Deserializing Annotations from Artemis failed (most likely none were present)!");
		}
	}

	private GradingDAO loadGradingDAO() {
		return new JsonFileConfigDAO(new File(this.systemWideController.getPreferences().getString(PreferenceConstants.GRADING_ABSOLUTE_CONFIG_PATH)));
	}

	@Override
	public void addAnnotation(String annotationId, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName, String customMessage,
			Double customPenalty) {
		try {
			this.annotationDAO.addAnnotation(annotationId, mistakeType, startLine, endLine, fullyClassifiedClassName, customMessage, customPenalty);
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
		return this.annotationDAO.getAnnotations().stream().toList();
	}

	@Override
	public Optional<IAnnotation> getAnnotationById(String id) {
		return this.annotationDAO.getAnnotations().stream().filter(annotation -> annotation.getUUID().equals(id)).findFirst();
	}

	@Override
	public IExercise getExercise() {
		return this.exercise;
	}

	@Override
	public ISubmission getSubmission() {
		return this.submission;
	}

	@Override
	public List<IMistakeType> getMistakes() {
		try {
			return gradingDAO.getExerciseConfig().getIMistakeTypes();
		} catch (ExerciseConfigConverterException | IOException e) {
			this.error("Exercise Config not parseable: " + e.getMessage(), e);
			return List.of();
		}
	}

	@Override
	public boolean isPositiveFeedbackAllowed() {
		try {
			return gradingDAO.getExerciseConfig().isPositiveFeedbackAllowed();
		} catch (ExerciseConfigConverterException | IOException e) {
			this.error("Exercise Config not parseable: " + e.getMessage(), e);
			return true;
		}
	}

	@Override
	public IRatingGroup getRatingGroupByDisplayName(final String displayName) {
		Optional<IRatingGroup> ratingGroupOptional = this.getRatingGroups().stream().filter(ratingGroup -> ratingGroup.getDisplayName().equals(displayName))
				.findFirst();
		if (ratingGroupOptional.isPresent()) {
			return ratingGroupOptional.get();
		}
		this.error("Rating Group \"" + displayName + "\" not found in config!", null);
		return null;
	}

	@Override
	public List<IRatingGroup> getRatingGroups() {
		try {
			return gradingDAO.getExerciseConfig().getIRatingGroups();
		} catch (ExerciseConfigConverterException | IOException e) {
			this.error("Exercise Config not parseable: " + e.getMessage(), e);
			return List.of();
		}
	}

	@Override
	public String getTooltipForMistakeType(IMistakeType mistakeType) {
		List<IAnnotation> annotations = this.getAnnotations().stream().filter(annotation -> annotation.getMistakeType().equals(mistakeType)).toList();
		return mistakeType.getTooltip(annotations);
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
		this.annotationDAO.modifyAnnotation(annatationId, customMessage, customPenalty);
	}

	@Override
	public void removeAnnotation(String annotationId) {
		this.annotationDAO.removeAnnotation(annotationId);
	}

	@Override
	public void resetAndRestartAssessment(IProjectFileNamingStrategy projectNaming) {
		this.deleteEclipseProject(projectNaming);
		this.systemWideController.getArtemisController().startAssessment(this.submission);
		this.systemWideController.downloadExerciseAndSubmission(this.course, this.exercise, this.submission, projectNaming);

		this.annotationDAO = new AnnotationDAO();
		this.gradingDAO = loadGradingDAO();

		try {
			this.initializeWithDeserializedAnnotations();
		} catch (IOException e) {
			this.info("Deserializing Annotations from Artemis failed: " + e.getMessage());
		}
	}

	@Override
	public double getCurrentPenaltyForRatingGroup(IRatingGroup ratingGroup) {
		AnnotationMapper mapper = //
				new AnnotationMapper(exercise, submission, getAnnotations(), getRatingGroups(), null, null);
		return mapper.calculatePointsForRatingGroup(ratingGroup).points();
	}
}
