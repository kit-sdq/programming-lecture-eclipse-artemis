/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.artemis.mapping;

import java.io.Serializable;
import java.util.List;

/**
 *
 * Only @link {@link IExam IExams} organize @link {@link IExercise IExercises}
 * in exercise groups.
 */
public interface IExerciseGroup extends Serializable {
	List<IExercise> getExercises();
}
