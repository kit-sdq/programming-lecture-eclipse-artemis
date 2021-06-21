package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.File;
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

	private final File configFile;
	private final ConfigDao configDao;
	private final String exerciseName;
	private final AnnotationDao annotationDao;

	/**
	 *
	 * @param configFile path to the config file
	 * @param exerciseName the shortName of the exercise (must be same in the config file).
	 */
	public AssessmentController(File configFile, String exerciseName) {
		this.configFile = configFile;
		this.configDao = new JsonFileConfigDao(configFile);

		this.exerciseName = exerciseName;

		this.annotationDao = new JsonFileAnnotationDao();
	}

	@Override
	public void addAnnotation(IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName,
			String customMessage, Double customPenalty) {
		this.annotationDao.addAnnotation(mistakeType, startLine, endLine, fullyClassifiedClassName, customMessage, customPenalty);

	}

	@Override
	public Collection<IAnnotation> getAnnotations(String className) {
		return this.annotationDao.getAnnotations().stream()
				.filter(annotation -> annotation.getFullyClassifiedClassName().equals(className))
				.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public Collection<IMistakeType> getMistakes() throws IOException {
		final Optional<ExerciseConfig> exerciseConfigOptional = this.configDao.getExerciseConfigs().stream()
				.filter(exerciseConfig -> exerciseConfig.getShortName().equals(this.exerciseName))
				.findFirst();
		if (exerciseConfigOptional.isPresent()) {
			return exerciseConfigOptional.get().getIMistakeTypes();
		}
		throw new IOException("Exercise not found in config!");
	}

	@Override
	public Collection<IRatingGroup> getRatingGroups() throws IOException {

		final Optional<ExerciseConfig> exerciseConfigOptional = this.configDao.getExerciseConfigs().stream()
				.filter(exerciseConfig -> exerciseConfig.getShortName().equals(this.exerciseName))
				.findFirst();
		if (exerciseConfigOptional.isPresent()) {
			return exerciseConfigOptional.get().getIRatingGroups();
		}
		throw new IOException("Exercise not found in config!");
	}

	@Override
	public void modifyAnnotation(int annatationId, Optional<String> customMessage, Optional<Double> customPenalty) {
		this.annotationDao.modifyAnnotation(annatationId, customMessage, customPenalty);
	}

	@Override
	public void removeAnnotation(int annotationId) {
		this.annotationDao.removeAnnotation(annotationId);
	}

}
