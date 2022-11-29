/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.artemis.mapping;

import java.io.Serializable;
import java.util.Date;

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

	String getParticipantUrl();

	ISubmission getSubmission(int id) throws ArtemisClientException;

	Date getDueDate();

	Date getStartDate();

	boolean isAutomaticAssessment();

	boolean isProgramming();

}
