package edu.kit.kastel.sdq.eclipse.grading.api;

import java.util.Collection;
import java.util.Optional;

import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObservable;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IFeedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

public interface IArtemisGUIController {

	/**
	 * Download submissions defined by the given submissionIds
	 * @param submissionIds
	 */
	void downloadExerciseAndSubmission(int courseID, int exerciseID, int submissionID);

	/**
	 *
	 * @return this AssessmentControllers {@link IAlertObservable} (Observer/ Observable pattern). This object replaces Exceptions.
	 */
	IAlertObservable getAlertObservable();


	Collection<IFeedback> getAllFeedbacksGottenFromLocking(int submissionID);

	/**
	 *
	 * @param exerciseID
	 * @return all submissions of the given @link {@link IExercise}, that have been started, saved or submitted by the caller.
	 */
	Collection<ISubmission> getBegunSubmissions(int exerciseID);

	/**
	 *
	 * @return all available courses (contains exercices and available submissions
	 */
	Collection<ICourse> getCourses();

	/**
	 *
	 * @return the {@link ICourse#getShortName()} of all available courses
	 */
	Collection<String> getCourseShortNames();

	/**
	 *
	 * @return the {@link IExam#getTitle()} of all available exams in the given {@link ICourse}
	 */
	Collection<String> getExamTitles(String courseShortName);

	IExercise getExerciseFromCourses(Collection<ICourse> courses, int courseID, int exerciseID);

	/**
	 *
	 * @return the {@link IExercise#getShortName()}s of the given {@link ICourse}
	 */
	Collection<String> getExerciseShortNames(String courseShortName);

	/**
	 *
	 * @return the {@link IExercise#getShortName()}s of the given {@link IExam}
	 */
	Collection<String> getExerciseShortNamesFromExam(String examTitle);


	/**
	 * Pre-condition: You need to have called startAssessment or startNextAssessment prior to calling this method!
	 * @return all auto feedbacks gotten by starting the assessment (junit test results).
	 */
	Collection<IFeedback> getPrecalculatedAutoFeedbacks(int submissionID);

	ISubmission getSubmissionFromExercise(IExercise exercise, int submissionID);

	/**
	 * Submit the assessment to Artemis. Must have been started by {@link #startAssessment(int)}, {@link #startNextAssessment(int)}
	 * 		or {@link #startNextAssessment(int, int)}, before!
	 * @param submissionID
	 * @param submit should the assessment be submitted or merely saved to artemis?
	 * @param invalidSubmission is the submission invalid? Will return 0 points.
	 * @param exerciseName the exercise name is used to internally identify which annotations should be sent.
	 *
	 * @return whether the operation was successful.
	 */
	boolean saveAssessment(int submissionID, boolean submit, boolean invalidSubmission);

	/**
	 * Starts an assessment for the given submission. Acquires a lock in the process.
	 * @param submissionID
	 */
	void startAssessment(int submissionID);

	/**
	 * Starts the next assessment. Which one is smh determined by artemis. Correction Round is set to 0.
	 * @param exerciseID the exerciseID (found in your ICourse-Collection gotten via IArtemisGUIController::getCourses())
	 * @return
	 * 		<li> the submissionID which defines what is assessed.
	 * 		<li> Optional.empty(), if no assessment is left!
	 */
	Optional<Integer> startNextAssessment(int exerciseID);

	/**
	 * Starts the next assessment of the given correction round. Which one is smh determined by artemis.
	 * @param exerciseID the exerciseID (found in your ICourse-Collection gotten via IArtemisGUIController::getCourses())
	 * @param correctionRound for non-exams: 0. For exams: either 0 or 1 (TODO really? or (1,2) or (0,2)?? ==> find out..
	 * @return
	 * 		<li> the submissionID which defines what is assessed.
	 * 		<li> Optional.empty(), if no assessment is left!
	 */
	Optional<Integer> startNextAssessment(int exerciseID, int correctionRound);
}
