package edu.kit.kastel.sdq.eclipse.grading.core.config;

import java.io.IOException;
import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.core.model.RatingGroup;

/**
 * Encapsulates read access to Config sources, which might come from a file
 *
 * A Config is
 */
public interface ConfigDao {
	
	public Collection<ExerciseConfig> getExerciseConfigs() throws IOException;
}
