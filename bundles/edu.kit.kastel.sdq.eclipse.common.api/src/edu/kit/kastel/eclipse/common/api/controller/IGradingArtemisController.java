/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.controller;

import java.util.Optional;

import edu.kit.kastel.sdq.artemis4j.api.ArtemisClientException;
import edu.kit.kastel.sdq.artemis4j.api.artemis.Exercise;
import edu.kit.kastel.sdq.artemis4j.api.artemis.ExerciseStats;
import edu.kit.kastel.sdq.artemis4j.api.artemis.assessment.Submission;

/**
 * Works as an interface from backend to REST-clients. It handles specific tasks
 * concerned with the grading product.
 */
public interface IGradingArtemisController extends IArtemisController {
	/**
	 * Submit the assessment to Artemis. Must have been started by
	 * {@link #startAssessment(ISubmission)} or
	 * {@link #startNextAssessment(IExercise, int)}.
	 *
	 * @param submit   should the assessment be submitted or merely saved to
	 *                 artemis?
	 * @param exercise the exercise is used to internally identify which annotations
	 *                 should be sent.
	 * @return whether the operation was successful.
	 */
	boolean saveAssessment(IAssessmentController assessmentController, Exercise exercise, Submission submission, boolean submit);

	/**
	 * Starts an assessment for the given submission. Acquires a lock in the
	 * process.
	 */
	void startAssessment(Submission submissionId);

	/**
	 * Starts the next assessment of the given correction round. Which one is smh
	 * determined by artemis.
	 *
	 * @param exercise        the exercise (found in your ICourse-Collection gotten
	 *                        via IArtemisController::getCourses())
	 * @param correctionRound for non-exams: 0. For exams: either 0 or 1
	 * @return
	 *         <li>the submissionID which defines what is assessed.
	 *         <li>Optional.empty(), if no assessment is left!
	 */
	Optional<Submission> startNextAssessment(Exercise exercise, int correctionRound);

	/**
	 * Get statistics for exercise.
	 *
	 * @param exercise the exercise
	 * @return the statistics
	 */
	ExerciseStats getStats(Exercise exercise) throws ArtemisClientException;

}
