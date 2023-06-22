/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.client;

import java.util.Optional;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.AssessmentResult;
import edu.kit.kastel.eclipse.common.api.artemis.ILockResult;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.Feedback;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.LongFeedbackText;
import edu.kit.kastel.eclipse.common.api.controller.ExerciseStats;

/**
 * REST-Client to execute calls concerning assessment.
 */
public interface IAssessmentArtemisClient {

	/**
	 * Starts an assessment for the given submission. Acquires a lock in the
	 * process.
	 *
	 * @param submission
	 * @return the data gotten back, which is needed for submitting the assessment
	 *         result correctly ({@link #saveAssessment(int, boolean, String)}
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */

	ILockResult startAssessment(ISubmission submission) throws ArtemisClientException;

	/**
	 * Starts an assessment for any available submission (determined by artemis).
	 * Acquires a lock in the process.
	 *
	 * @param correctionRound relevant for exams! may be 0 or 1
	 * @return
	 *         <li>the data gotten back. Needed for submitting correctly.
	 *         <li><b>null</b> if there is no submission left to correct
	 * @throws ArtemisClientException if some errors occur while parsing the result
	 *                                or if authentication fails.
	 */
	Optional<ILockResult> startNextAssessment(IExercise exercise, int correctionRound) throws ArtemisClientException;

	/**
	 * Submit the assessment to Artemis. Must have been started by
	 * {@link #startAssessment(int)} or {@link #startNextAssessment(int, int)}
	 * before!
	 *
	 * @param participation YOU SHALL NOT PROVIDE THE SUBMISSIONID, HERE! The
	 *                      participationId can be gotten from the
	 *                      {@link ILockResult}, via {@link #startAssessment(int)}
	 *                      or {@link #startNextAssessment(int, int)}!
	 * @param submit        determine whether the assessment should be submitted or
	 *                      just saved.
	 * @param assessment    the assessment
	 */
	void saveAssessment(int participationId, boolean submit, AssessmentResult assessment) throws ArtemisClientException;

	/**
	 * Get statistics for exercise.
	 *
	 * @param exercise the exercise
	 * @return the statistics
	 */
	ExerciseStats getStats(IExercise exercise) throws ArtemisClientException;

	LongFeedbackText getLongFeedback(int resultId, Feedback feedback) throws ArtemisClientException;
}
