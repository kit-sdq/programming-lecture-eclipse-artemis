package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.core.annotation.Annotation;
import edu.kit.kastel.sdq.eclipse.grading.core.annotation.AnnotationDao;
import edu.kit.kastel.sdq.eclipse.grading.core.annotation.JsonFileAnnotationDao;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ConfigDao;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ExerciseConfig;
import edu.kit.kastel.sdq.eclipse.grading.core.config.JsonFileConfigDao;

public class AssessmentController implements IAssessmentController {

	private File configFile;
	private ConfigDao configDao;
	private String exerciseName;
	private AnnotationDao annotationDao;
	
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
	public Collection<IMistakeType> getMistakes() throws IOException {
		final Optional<ExerciseConfig> exerciseConfigOptional = this.configDao.getExerciseConfigs().stream()
				.filter(exerciseConfig -> exerciseConfig.getShortName().equals(this.exerciseName))
				.findFirst();
		if (exerciseConfigOptional.isPresent()) {
			return exerciseConfigOptional.get().getIMistakeTypes();
		} else throw new IOException("TODO: write msg");
	}

	@Override
	public void addAnnotation(IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName,
			Optional<String> customMessage, Optional<Double> customPenalty) {
		this.annotationDao.addAnnotation(mistakeType, startLine, endLine, fullyClassifiedClassName, customMessage, customPenalty);
		
	}

	@Override
	public Collection<IAnnotation> getAnnotations(String className) {
		return this.annotationDao.getAnnotations().stream()
				.filter(annotation -> annotation.getFullyClassifiedClassName().equals(className))
				.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public void removeAnnotation(int annotationId) {
		this.annotationDao.removeAnnotation(annotationId);
	}

	@Override
	public void modifyAnnotation(int annatationId, Optional<String> customMessage, Optional<Double> customPenalty) {
		//TODO problem: need to modify annotation 
		// without the annotation to be able to be edited by the caller, which would make the state inconsistent
		// (cast to Annotation class would be dumb..)
		// ==> Best Idea so far: create a method in AnnotationDao which creates a new Object with the same ID
		throw new RuntimeException("AssessmentController::modifyAnnotation Not implemented yet");		
	}

}
