package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;
import edu.kit.kastel.sdq.eclipse.grading.core.annotation.AnnotationDao;
import edu.kit.kastel.sdq.eclipse.grading.core.annotation.JsonFileAnnotationDao;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ConfigDao;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ExerciseConfig;
import edu.kit.kastel.sdq.eclipse.grading.core.config.JsonFileConfigDao;


public class AssessmentController implements IAssessmentController {

	private SystemwideController systemWideController;
	private String exerciseName;
	private JsonFileConfigDao configDao;
	private AnnotationDao annotationDao;
	//TODO global List of ASsessmentController in SystemSpecificController
	//
	//TODO pull config file up to "global state".

	/**
	 * Protected, because the way to get a specific assessment controller should be over a SystemwideController.
	 *
	 * @param configFile path to the config file
	 * @param exerciseName the shortName of the exercise (must be same in the config file).
	 */
	protected AssessmentController(SystemwideController systemWideController, String exerciseName) {
		this.systemWideController = systemWideController;
		this.exerciseName = exerciseName;
		this.annotationDao = new JsonFileAnnotationDao();
	}

	@Override
	public void addAnnotation(IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName,
			String customMessage, Double customPenalty) {
		this.annotationDao.addAnnotation(mistakeType, startLine, endLine, fullyClassifiedClassName, customMessage, customPenalty);

	}

	@Override
	public double calculateCurrentPenaltyForMistakeType(IMistakeType mistakeType) {
		// TODO Auto-generated method stub
		return mistakeType.calculatePenalty(
			this.getAnnotations(this.exerciseName).stream()
				.filter(annotation -> annotation.getMistakeType().equals(mistakeType))
				.collect(Collectors.toList())
		);
	}

	@Override
	public double calculateCurrentPenaltyForRatingGroup(IRatingGroup ratingGroup) throws IOException {
		return this.getMistakes().stream()
				.map(this::calculateCurrentPenaltyForMistakeType)
				.collect(Collectors.summingDouble(Double::doubleValue));
	}

	@Override
	public Collection<IAnnotation> getAnnotations(String className) {
		return this.annotationDao.getAnnotations().stream()
				.filter(annotation -> annotation.getFullyClassifiedClassName().equals(className))
				.collect(Collectors.toUnmodifiableList());
	}

	private ConfigDao getConfigDao() {
		return this.systemWideController.getConfigDao();
	}

	public String getExerciseName() {
		return this.exerciseName;
	}

	@Override
	public Collection<IMistakeType> getMistakes() throws IOException {
		final Optional<ExerciseConfig> exerciseConfigOptional = this.getConfigDao().getExerciseConfigs().stream()
				.filter(exerciseConfig -> exerciseConfig.getShortName().equals(this.exerciseName))
				.findFirst();
		if (exerciseConfigOptional.isPresent()) {
			return exerciseConfigOptional.get().getIMistakeTypes();
		}
		throw new IOException("Exercise not found in config!");
	}

	@Override
	public Collection<IRatingGroup> getRatingGroups() throws IOException {

		final Optional<ExerciseConfig> exerciseConfigOptional = this.getConfigDao().getExerciseConfigs().stream()
				.filter(exerciseConfig -> exerciseConfig.getShortName().equals(this.exerciseName))
				.findFirst();
		if (exerciseConfigOptional.isPresent()) {
			return exerciseConfigOptional.get().getIRatingGroups();
		}
		throw new IOException("Exercise not found in config!");
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
}
