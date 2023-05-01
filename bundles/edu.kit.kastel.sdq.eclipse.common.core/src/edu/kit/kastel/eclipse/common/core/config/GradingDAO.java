/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.core.config;

import java.io.IOException;

import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;

/**
 * Encapsulates read access to config sources, which might come from a file
 */
public interface GradingDAO {
	ExerciseConfig getExerciseConfig(IExercise exercise) throws IOException, ExerciseConfigConverterException;
}
