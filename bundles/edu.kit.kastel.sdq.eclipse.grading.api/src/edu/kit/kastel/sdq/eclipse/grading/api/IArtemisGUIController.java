package edu.kit.kastel.sdq.eclipse.grading.api;

import java.util.Collection;
import java.util.Optional;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IFeedback;

public interface IArtemisGUIController {

	/**
	 * Download submissions defined by the given submissionIds
	 * @param submissionIds
	 */
	void downloadExerciseAndSubmission(int courseID, int exerciseID, int submissionID) throws Exception;

	/**
	 * TODO hardcoded download of some exercise and submission, also project-making so that it's all ready to work on.
	 * @return submissionID (later this is gotten via IArtemisGUIController::getCourses)
	 */
	int downloadHardcodedExerciseAndSubmissionExample();

	Collection<IFeedback> getAllFeedbacksGottenFromLocking(int submissionID);

	/**
	 *
	 * @return all available courses (contains exercices and available submissions
	 */
	Collection<ICourse> getCourses();

	/**
	 * Pre-condition: You need to have called startAssessment or startNextAssessment prior to calling this method!
	 * @return all auto feedbacks gotten by starting the assessment (junit test results).
	 */
	Collection<IFeedback> getPrecalculatedAutoFeedbacks(int submissionID);

	/**
	 * Starts an assessment for the given submission
	 * @param submissionID
	 */
	void startAssessment(int submissionID) throws Exception;

	/**
	 * Starts the next assessment. Which one is smh determined by artemis.
	 * @param exerciseID the exerciseID (found in your ICourse-Collection gotten via IArtemisGUIController::getCourses())
	 * @return
	 * 		<li> the submissionID which defines what is assessed.
	 * 		<li> Optional.empty(), if no assessment is left!
	 */
	Optional<Integer> startNextAssessment(int exerciseID) throws Exception;

	/**
	 * Submit the assessment to Artemis. Must have been started by {@code startAssessment}, before.
	 * @param submissionID
	 * @param exerciseName the exercise name is used to internally identify which annotations should be sent.
	 */
	void submitAssessment(int submissionID) throws Exception;
}
