/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.core;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import edu.kit.kastel.sdq.eclipse.common.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.common.api.controller.AbstractController;
import edu.kit.kastel.sdq.eclipse.common.api.controller.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.common.api.controller.IViewInteraction;
import edu.kit.kastel.sdq.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.common.api.model.IRatingGroup;
import edu.kit.kastel.sdq.eclipse.common.core.artemis.AnnotationDeserializer;
import edu.kit.kastel.sdq.eclipse.common.core.artemis.WorkspaceUtil;
import edu.kit.kastel.sdq.eclipse.common.core.artemis.calculation.DefaultPenaltyCalculationStrategy;
import edu.kit.kastel.sdq.eclipse.common.core.config.ConfigDAO;
import edu.kit.kastel.sdq.eclipse.common.core.model.annotation.AnnotationException;
import edu.kit.kastel.sdq.eclipse.common.core.model.annotation.DefaultAnnotationDao;
import edu.kit.kastel.sdq.eclipse.common.core.model.annotation.IAnnotationDao;

public class AssessmentController extends AbstractController implements IAssessmentController {

	private GradingSystemwideController systemWideController;
	private IAnnotationDao annotationDao;

	private final ICourse course;
	private final IExercise exercise;
	private ISubmission submission;

	/**
	 * Protected, because the way to get a specific assessment controller should be
	 * over a SystemwideController.
	 *
	 * @param configFile   path to the config file
	 * @param exerciseName the shortName of the exercise (must be same in the config
	 *                     file).
	 */
	protected AssessmentController(GradingSystemwideController systemWideController, ICourse course, IExercise exercise, ISubmission submission) {
		this.systemWideController = systemWideController;

		this.course = course;
		this.exercise = exercise;
		this.submission = submission;

		this.annotationDao = new DefaultAnnotationDao();

		try {
			this.initializeWithDeserializedAnnotations();
		} catch (IOException e) {
			this.warn("Deserializing Annotations from Artemis failed (most likely none were present)!");
		}
	}

	@Override
	public void addAnnotation(String annotationID, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName, String customMessage,
			Double customPenalty) {
		try {
			this.annotationDao.addAnnotation(annotationID, mistakeType, startLine, endLine, fullyClassifiedClassName, customMessage, customPenalty);
		} catch (AnnotationException e) {
			this.error(e.getMessage(), e);
		}

	}

	@Override
	public double calculateCurrentPenaltyForMistakeType(IMistakeType mistakeType) {
		return new DefaultPenaltyCalculationStrategy(this.getAnnotations(), this.getMistakes()).calculatePenaltyForMistakeType(mistakeType);
	}

	@Override
	public double calculateCurrentPenaltyForRatingGroup(IRatingGroup ratingGroup) {
		return new DefaultPenaltyCalculationStrategy(this.getAnnotations(), this.getMistakes()).calcultatePenaltyForRatingGroup(ratingGroup);
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
		return this.annotationDao.getAnnotations().stream().toList();
	}

	@Override
	public Optional<IAnnotation> getAnnotationByUUID(String uuid) {
		return this.annotationDao.getAnnotations().stream().filter(annotation -> annotation.getUUID().equals(uuid)).findFirst();
	}

	@Override
	public List<IAnnotation> getAnnotations(String className) {
		return this.annotationDao.getAnnotations().stream().filter(annotation -> annotation.getClassFilePath().equals(className)).toList();
	}

	private ConfigDAO getConfigDao() {
		return this.systemWideController.getConfigDao();
	}

	@Override
	public ICourse getCourse() {
		return this.course;
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
			return this.getConfigDao().getExerciseConfig().getIMistakeTypes();
		} catch (IOException e) {
			this.error("Exercise Config not parseable: " + e.getMessage(), e);
			return List.of();
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
	public IRatingGroup getRatingGroupByShortName(final String shortName) {
		Optional<IRatingGroup> ratingGroupOptional = this.getRatingGroups().stream().filter(ratingGroup -> ratingGroup.getShortName().equals(shortName))
				.findFirst();
		if (ratingGroupOptional.isPresent()) {
			return ratingGroupOptional.get();
		}
		this.error("Rating Group \"" + shortName + "\" not found in config!", null);
		return null;
	}

	@Override
	public List<IRatingGroup> getRatingGroups() {
		try {
			return this.getConfigDao().getExerciseConfig().getIRatingGroups();
		} catch (IOException e) {
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
		this.annotationDao.modifyAnnotation(annatationId, customMessage, customPenalty);
	}

	@Override
	public void removeAnnotation(String annotationId) {
		this.annotationDao.removeAnnotation(annotationId);
	}

	@Override
	public void resetAndRestartAssessment(IProjectFileNamingStrategy projectNaming) {
		this.deleteEclipseProject(projectNaming);
		this.systemWideController.getArtemisController().startAssessment(this.submission);
		this.systemWideController.downloadExerciseAndSubmission(this.course, this.exercise, this.submission, projectNaming);

		this.annotationDao = new DefaultAnnotationDao();

		try {
			this.initializeWithDeserializedAnnotations();
		} catch (IOException e) {
			this.info("Deserializing Annotations from Artemis failed: " + e.getMessage());
		}
	}

	@Override
	public IViewInteraction getViewInteraction() {
		return this.getViewInteractionHandler();
	}
}
