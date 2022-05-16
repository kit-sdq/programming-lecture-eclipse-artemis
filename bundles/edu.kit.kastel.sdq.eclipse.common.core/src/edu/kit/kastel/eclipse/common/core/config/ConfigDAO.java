/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.core.config;

import java.io.IOException;

/**
 * Encapsulates read access to Config sources, which might come from a file
 *
 * A Config is
 */
public interface ConfigDAO {

	ExerciseConfig getExerciseConfig() throws IOException;
}
