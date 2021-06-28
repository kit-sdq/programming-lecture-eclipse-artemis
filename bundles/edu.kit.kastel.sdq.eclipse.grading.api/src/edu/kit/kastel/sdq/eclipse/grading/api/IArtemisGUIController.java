package edu.kit.kastel.sdq.eclipse.grading.api;

import java.util.Collection;

public interface IArtemisGUIController {

	/**
	 * TODO hardcoded download of some exercise and submission, also project-making so that it's all ready to work on.
	 * @return submissionID (later this is gotten via IArtemisGUIController::getCourses)
	 */
	int downloadHardcodedExerciseAndSubmissionExample();

	/**
	 * Download submissions defined by the given submissionIds
	 * @param submissionIds
	 */
	void downloadSubmissions(Collection<Integer> submissionIds, String courseName, String exerciseName);

	/**
	 *
	 * @return all available courses (contains exercices and available submissions
	 */
	Collection<ICourse> getCourses();

	/**
	 * Starts an assessment for the given submission
	 * @param submissionID
	 */
	void startAssessment(int submissionID, String exerciseName) throws Exception;

	/**
	 * Submit the assessment to Artemis. Must have been started by {@code startAssessment}, before.
	 * @param submissionID
	 * @param exerciseName the exercise name is used to internally identify which annotations should be sent.
	 */
	void submitAssessment(int submissionID, String exerciseName) throws Exception;
}
