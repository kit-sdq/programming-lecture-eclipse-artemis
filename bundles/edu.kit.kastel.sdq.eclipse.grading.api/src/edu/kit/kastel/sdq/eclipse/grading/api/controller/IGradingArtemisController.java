package edu.kit.kastel.sdq.eclipse.grading.api.controller;

import java.util.Optional;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

/**
 * Works as an interface from backend to REST-clients. It handles specific tasks concerned with the grading product.
 */
public interface IGradingArtemisController extends IArtemisController {
	/**
	 * Submit the assessment to Artemis. Must have been started by
	 * {@link #startAssessment(int)}, {@link #startNextAssessment(int)} or
	 * {@link #startNextAssessment(int, int)}, before!
	 *
	 * @param submission
	 * @param submit            should the assessment be submitted or merely saved
	 *                          to artemis?
	 * @param invalidSubmission is the submission invalid? Will return 0 points.
	 * @param exerciseName      the exercise name is used to internally identify
	 *                          which annotations should be sent.
	 *
	 * @return whether the operation was successful.
	 */
	boolean saveAssessment(IAssessmentController assessmentController, IExercise exercise, ISubmission submission, boolean submit, boolean invalidSubmission);

	/**
	 * Starts an assessment for the given submission. Acquires a lock in the
	 * process.
	 *
	 * @param submissionID
	 */
	void startAssessment(ISubmission submissionID);

	/**
	 * Starts the next assessment. Which one is smh determined by artemis.
	 * Correction Round is set to 0.
	 *
	 * @param exerciseID the exerciseID (found in your ICourse-Collection gotten via
	 *                   IArtemisController::getCourses())
	 * @return
	 *         <li>the submissionID which defines what is assessed.
	 *         <li>Optional.empty(), if no assessment is left!
	 */
	Optional<ISubmission> startNextAssessment(IExercise exercise);

	/**
	 * Starts the next assessment of the given correction round. Which one is smh
	 * determined by artemis.
	 *
	 * @param exerciseID      the exerciseID (found in your ICourse-Collection
	 *                        gotten via IArtemisController::getCourses())
	 * @param correctionRound for non-exams: 0. For exams: either 0 or 1
	 * @return
	 *         <li>the submissionID which defines what is assessed.
	 *         <li>Optional.empty(), if no assessment is left!
	 */
	Optional<ISubmission> startNextAssessment(IExercise exercise, int correctionRound);

}
