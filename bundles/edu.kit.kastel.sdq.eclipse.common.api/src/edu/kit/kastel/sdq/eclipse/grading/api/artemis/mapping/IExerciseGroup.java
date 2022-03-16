package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.io.Serializable;
import java.util.List;

/**
 *
 * Only @link {@link IExam IExams} organize @link {@link IExercise IExercises}
 * in exercise groups.
 */
public interface IExerciseGroup extends Serializable {

	int getExerciseGroupId();

	List<IExercise> getExercises();

	String getTitle();

	boolean isMandatory();
}
