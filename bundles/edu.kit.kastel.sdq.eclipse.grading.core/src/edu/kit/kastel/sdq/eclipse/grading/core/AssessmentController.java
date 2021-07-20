package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IArtemisGUIController;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;
import edu.kit.kastel.sdq.eclipse.grading.api.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObservable;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IFeedback;
import edu.kit.kastel.sdq.eclipse.grading.core.annotation.AnnotationDao;
import edu.kit.kastel.sdq.eclipse.grading.core.annotation.AnnotationException;
import edu.kit.kastel.sdq.eclipse.grading.core.annotation.JsonFileAnnotationDao;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.AnnotationDeserializer;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.DefaultProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.WorkspaceUtil;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ConfigDao;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ExerciseConfig;
import edu.kit.kastel.sdq.eclipse.grading.core.config.JsonFileConfigDao;


public class AssessmentController implements IAssessmentController {

	private SystemwideController systemWideController;
	private int submissionID;
	private JsonFileConfigDao configDao;
	private AnnotationDao annotationDao;

	private AlertObservable alertObservable;

	private final int courseID;
	private final int exerciseID;

	private String exerciseConfigShortName;
	//TODO global List of ASsessmentController in SystemSpecificController
	//
	//TODO pull config file up to "global state".

	/**
	 * Protected, because the way to get a specific assessment controller should be over a SystemwideController.
	 *
	 * @param configFile path to the config file
	 * @param exerciseName the shortName of the exercise (must be same in the config file).
	 */
	protected AssessmentController(SystemwideController systemWideController, int courseID, int exerciseID, int submissionID,String exerciseConfigName) {
		this.systemWideController = systemWideController;
		this.submissionID = submissionID;
		this.annotationDao = new JsonFileAnnotationDao();

		this.alertObservable = new AlertObservable();

		this.exerciseID = exerciseID;
		this.courseID = courseID;

		this.exerciseConfigShortName = exerciseConfigName;

		try {
			this.initializeWithDeserializedAnnotations();
		} catch (IOException e) {
			this.alertObservable.warn("Deserializing Annotations from Artemis failed (most likely none were present)!");
		}
	}

	@Override
	public void addAnnotation(int annotationID, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName,
			String customMessage, Double customPenalty) {
		try {
			this.annotationDao.addAnnotation(annotationID, mistakeType, startLine, endLine, fullyClassifiedClassName, customMessage, customPenalty);
		} catch (AnnotationException e) {
			this.alertObservable.error(e.getMessage(), e);
		}

	}

	@Override
	public double calculateCurrentPenaltyForMistakeType(IMistakeType mistakeType) {
		return new DefaultPenaltyCalculationStrategy(this.getAnnotations(), this.getMistakes())
				.calculatePenaltyForMistakeType(mistakeType);
	}

	@Override
	public double calculateCurrentPenaltyForRatingGroup(IRatingGroup ratingGroup) {
		return new DefaultPenaltyCalculationStrategy(this.getAnnotations(), this.getMistakes())
				.calcultatePenaltyForRatingGroup(ratingGroup);
	}

	@Override
	public void deleteEclipseProject() {
		IArtemisGUIController guiController =  this.systemWideController.getArtemisGUIController();
		final Collection<ICourse> courses = guiController.getCourses();
		final IExercise exercise = guiController.getExerciseFromCourses(courses, this.courseID, this.exerciseID);
		final ISubmission submission = guiController.getSubmissionFromExercise(exercise, this.submissionID);

		final String projectName = new DefaultProjectFileNamingStrategy().getProjectFileInWorkspace(
				ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile(),
				exercise,
				submission
			).getName();
		try {
			WorkspaceUtil.deleteEclipseProject(projectName);
		} catch (CoreException e) {
			this.alertObservable.error("Eclipse Exception occurred while trying to delete project: " + e.getMessage(), e);
		}

	}

	@Override
	public IAlertObservable getAlertObservable() {
		return this.alertObservable;
	}

	@Override
	public Collection<IAnnotation> getAnnotations() {
		return this.annotationDao.getAnnotations().stream()
				.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public Collection<IAnnotation> getAnnotations(String className) {
		return this.annotationDao.getAnnotations().stream()
				.filter(annotation -> annotation.getClassFilePath().equals(className))
				.collect(Collectors.toUnmodifiableList());
	}

	private ConfigDao getConfigDao() {
		return this.systemWideController.getConfigDao();
	}

	@Override
	public int getCourseID() {
		return this.courseID;
	}

	/**
	 *
	 * @return the shortName (identifier) used to retrieve the corresponding exercise config from the ConfigDao.
	 */
	public String getExerciseConfigShortName() {
		return this.exerciseConfigShortName;
	}

	@Override
	public int getExerciseID() {
		return this.exerciseID;
	}

	@Override
	public Collection<IMistakeType> getMistakes(){
		Optional<ExerciseConfig> exerciseConfigOptional;
		try {
			exerciseConfigOptional = this.getConfigDao().getExerciseConfigs().stream()
					.filter(exerciseConfig -> exerciseConfig.getShortName().equals(this.exerciseConfigShortName))
					.findFirst();
		} catch (IOException e) {
			this.alertObservable.error(e.getMessage(), e);
			return null;
		}

		if (exerciseConfigOptional.isPresent()) {
			return exerciseConfigOptional.get().getIMistakeTypes();
		}
		this.alertObservable.error("ExerciseConfigShortName " + this.exerciseConfigShortName + " not found in config!", null);
		return null;
	}

	@Override
	public Collection<IRatingGroup> getRatingGroups() {

		Optional<ExerciseConfig> exerciseConfigOptional;
		try {
			exerciseConfigOptional = this.getConfigDao().getExerciseConfigs().stream()
					.filter(exerciseConfig -> exerciseConfig.getShortName().equals(this.exerciseConfigShortName))
					.findFirst();
		} catch (IOException e) {
			this.alertObservable.error(e.getMessage(), e);
			return null;
		}
		if (exerciseConfigOptional.isPresent()) {
			return exerciseConfigOptional.get().getIRatingGroups();
		}
		this.alertObservable.error("ExerciseConfigShortName " + this.exerciseConfigShortName + " not found in config!", null);
		return null;
	}

	public int getSubmissionID() {
		return this.submissionID;
	}

	@Override
	public String getTooltipForMistakeType(IMistakeType mistakeType) {
		return mistakeType.getTooltip(this.getAnnotations().stream()
				.filter(annotation -> annotation.getMistakeType().equals(mistakeType))
				.collect(Collectors.toList())
		);
	}

	private void initializeWithDeserializedAnnotations() throws IOException {
		final AnnotationDeserializer annotationDeserializer = new AnnotationDeserializer(this.getMistakes());
		final Collection<IFeedback> allFeedbacksGottenFromLocking = this.systemWideController.getArtemisGUIController().getAllFeedbacksGottenFromLocking(this.submissionID);
		if (allFeedbacksGottenFromLocking == null) {
			throw new IOException("No feedbacks gotten from locking could be acquired.");
		}

		for (IAnnotation annotation : annotationDeserializer.deserialize(allFeedbacksGottenFromLocking)) {
			this.addAnnotation(
					annotation.getId(),
					annotation.getMistakeType(),
					annotation.getStartLine(),
					annotation.getEndLine(),
					annotation.getClassFilePath(),
					annotation.getCustomMessage().orElse(null),
					annotation.getCustomPenalty().orElse(null)
			);
		}
	}

	/**
	 * TODO null statt optional
	 */
	@Override
	public void modifyAnnotation(int annatationId, String customMessage, Double customPenalty) {
		this.annotationDao.modifyAnnotation(annatationId, customMessage, customPenalty);
	}

	@Override
	public void removeAnnotation(int annotationId) {
		this.annotationDao.removeAnnotation(annotationId);
	}

	@Override
	public void resetAndReload() {
		this.annotationDao = new JsonFileAnnotationDao();

		try {
			this.initializeWithDeserializedAnnotations();
		} catch (IOException e) {
			this.alertObservable.warn("Deserializing Annotations from Artemis failed: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
