package edu.kit.kastel.sdq.eclipse.grading.api.client;

import java.util.ArrayList;
import java.util.List;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Assessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

public interface ISubmissionsArtemisClient {
	default List<ISubmission> getSubmissions(IExercise exercise) throws ArtemisClientException {
		List<ISubmission> submissions = new ArrayList<>(this.getSubmissions(exercise, 0));

		if (exercise.hasSecondCorrectionRound()) {
			submissions.addAll(this.getSubmissions(exercise, 1));
		}

		return submissions;
	}
	
	/**
	 *
	 * @param exerciseID
	 * @param assessedByTutor only return those submissions on which the caller has
	 *                        (started, saved or submitted) the assessment.
	 * @return submissions for the given exerciseID, filterable.
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */
	List<ISubmission> getSubmissions(IExercise exercise, int correctionRound) throws ArtemisClientException;
	
	ISubmission getSubmissionById(IExercise artemisExercise, int submissionId) throws ArtemisClientException;

}
