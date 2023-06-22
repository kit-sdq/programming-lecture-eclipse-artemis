/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.artemis.mapping;

import java.io.Serializable;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;

public interface IExercise extends Serializable {

	int getExerciseId();

	boolean hasSecondCorrectionRound();

	double getMaxPoints();

	boolean isSecondCorrectionEnabled();

	String getShortName();

	String getTestRepositoryUrl();

	String getTitle();

	ICourse getCourse();

	ISubmission getSubmission(int id) throws ArtemisClientException;

	boolean isAutomaticAssessment();

	boolean isProgramming();

}
