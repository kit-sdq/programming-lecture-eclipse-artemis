package edu.kit.kastel.sdq.eclipse.grading.core.config;

import java.io.IOException;

/**
 * Encapsulates read access to Config sources, which might come from a file
 *
 * A Config is
 */
public interface ConfigDAO {

	ExerciseConfig getExerciseConfig() throws IOException;
}
