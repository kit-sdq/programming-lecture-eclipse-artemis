package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.util.Collection;

/**
 *
 * Only @link {@link IExam}s organize @link {@link IExercise}s in exercise groups.
 */
public interface IExerciseGroup {

	int getExerciseGroupId();

	Collection<IExercise> getExercises();

	String getTitle();

	boolean isMandatory();
}
