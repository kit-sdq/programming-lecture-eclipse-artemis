/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.api.client;

import java.util.Optional;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.AssessmentResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;

/**
 * REST-Client to execute calls concerning assessment.
 */
public interface IAssessmentArtemisClient {
	/**
	 * Submit the assessment to Artemis. Must have been started by
	 * {@link #startAssessment(int)} or {@link #startNextAssessment(int, int)}
	 * before!
	 *
	 * @param participation THOU SHALT NOT PROVIDE THE SUBMISSIONID, HERE! The
	 *                      participationID can be gotten from the
	 *                      {@link ILockResult}, via {@link #startAssessment(int)}
	 *                      or {@link #startNextAssessment(int, int)}! * @param
	 *                      submit determine whether the assessment should be
	 *                      submitted or just saved.
	 * @param assessment    the assessment
	 *
	 * @throws ArtemisClientException
	 */
	void saveAssessment(ParticipationDTO participation, boolean submit, AssessmentResult assessment) throws ArtemisClientException;

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

}
