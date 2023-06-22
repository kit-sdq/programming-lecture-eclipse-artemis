/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.client;

import java.util.ArrayList;
import java.util.List;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;

public interface ISubmissionsArtemisClient {
	default List<ISubmission> getSubmissions(IExercise exercise) throws ArtemisClientException {
		List<ISubmission> submissions = new ArrayList<>(this.getSubmissions(exercise, 0));

		if (exercise.hasSecondCorrectionRound()) {
			submissions.addAll(this.getSubmissions(exercise, 1));
		}

		return submissions;
	}

	/**
	 * @return submissions for the given exercise and correction round.
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */
	List<ISubmission> getSubmissions(IExercise exercise, int correctionRound) throws ArtemisClientException;

	/**
	 *
	 * @param artemisExercise exercise to load submission.
	 * @param submissionId    of submission to be returned
	 * @return submission with submissionId.
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */
	ISubmission getSubmissionById(IExercise artemisExercise, int submissionId) throws ArtemisClientException;

}
