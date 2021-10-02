package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;

public interface IExercise {

	int getExerciseId();

	double getMaxPoints();

	Boolean getSecondCorrectionEnabled();

	String getShortName();

	String getTestRepositoryUrl();

	String getTitle();

	String getType();

	ICourse getCourse();

	ISubmission getSubmission(int id) throws ArtemisClientException;
}
