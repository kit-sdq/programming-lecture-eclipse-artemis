package edu.kit.kastel.sdq.eclipse.grading.core.config;

import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.core.model.MistakeType;
import edu.kit.kastel.sdq.eclipse.grading.core.model.RatingGroup;

public class JsonConfigFileMapped {
	
	private Collection<ExerciseConfig> exerciseConfigs;
	
	public JsonConfigFileMapped(Collection<ExerciseConfig> exerciseConfigs) {
		
		this.exerciseConfigs = exerciseConfigs;
	}

	public Collection<ExerciseConfig> getExerciseConfigs() {
		return exerciseConfigs;
	}	
	
	
}
