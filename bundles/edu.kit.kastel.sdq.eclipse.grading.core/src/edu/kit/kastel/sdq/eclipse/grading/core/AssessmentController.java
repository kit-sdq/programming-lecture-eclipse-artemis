package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import edu.kit.kastel.sdq.eclipse.grading.api.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ConfigDao;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ExerciseConfig;
import edu.kit.kastel.sdq.eclipse.grading.core.config.JsonFileConfigDao;

public class AssessmentController implements IAssessmentController {

	private File configFile;
	private ConfigDao configDao;
	private String exerciseName;
	
	/**
	 * 
	 * @param configFile path to the config file
	 * @param exerciseName the shortName of the exercise (must be same in the config file).
	 */
	public AssessmentController(File configFile, String exerciseName) {
		this.configFile = configFile;
		this.configDao = new JsonFileConfigDao(configFile);
		this.exerciseName = exerciseName;
	}
	
	@Override
	public Collection<IMistakeType> getMistakes() throws IOException {
		final Optional<ExerciseConfig> exerciseConfigOptional = this.configDao.getExerciseConfigs().stream().filter(exerciseConfig -> exerciseConfig.getShortName().equals(this.exerciseName)).findFirst();
		if (exerciseConfigOptional.isPresent()) {
			return exerciseConfigOptional.get().getIMistakeTypes();
		} else throw new IOException("TODO: write msg");
	}

	@Override
	public void addAnnotation(int startLine, int endLine, String fullyClassifiedClassName,
			Optional<String> customMessage, Optional<Double> customPenalty) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<IAnnotation> getAnnotations(String className) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeAnnotation(int annotationId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyAnnotation(int annatationId, Optional<String> customMessage, Optional<Double> customPenalty) {
		// TODO Auto-generated method stub
		
	}

}
