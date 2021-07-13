package edu.kit.kastel.sdq.eclipse.grading.api;

import java.util.Collection;

public interface IExerciseGroup {

	int getExerciseGroupId();

	Collection<IExercise> getExercises();

	String getTitle();

	boolean isMandatory();
}
