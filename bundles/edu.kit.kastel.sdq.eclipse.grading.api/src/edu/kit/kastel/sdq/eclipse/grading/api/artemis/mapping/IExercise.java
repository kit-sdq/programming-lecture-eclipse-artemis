package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.util.Collection;

public interface IExercise {

	int getExerciseId();

	String getShortName();

	Collection<ISubmission> getSubmissions();

	String getTestRepositoryUrl();

	String getTitle();
}
