package edu.kit.kastel.sdq.eclipse.grading.api.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

/**
 * Works as an interface from backend to ArtemisClient
 *
 */
public interface IArtemisController extends IController {

	/**
	 * Download submissions defined by the given submissionIds
	 *
	 * @param submissionIds
	 * @return whether download was successful or not
	 */
	boolean downloadExerciseAndSubmission(ICourse courseID, IExercise exerciseID, ISubmission submissionID, IProjectFileNamingStrategy projectNaming);

	/**
	 *
	 * @return all IFeedbacks that were gotten in the process of locking the given
	 *         submission.
	 */
	List<Feedback> getAllFeedbacksGottenFromLocking(ISubmission submission);

	/**
	 *
	 * @param exerciseID
	 * @return all submissions of the given @link {@link IExercise}, that have been
	 *         started, saved or submitted by the caller.
	 */
	List<ISubmission> getBegunSubmissions(IExercise exercise);

	/**
	 *
	 * @return all available courses (contains exercices and available submissions
	 */
	List<ICourse> getCourses();

	/**
	 *
	 * @return the {@link ICourse#getShortName()} of all available courses
	 */
	List<String> getCourseShortNames();

	/**
	 *
	 * @return the {@link IExam#getTitle()} of all available exams in the given
	 *         {@link ICourse}
	 */
	List<String> getExamTitles(String courseShortName);

	/**
	 * Convenience method. Search the given ids in the given courses.
	 *
	 * @param courses    the data in which to search for the exercise
	 * @param courseID
	 * @param exerciseID
	 * @return the exercise, if found. null else.
	 */
	IExercise getExerciseFromCourses(List<ICourse> courses, int courseID, int exerciseID);

	List<IExercise> getExercises(ICourse course, boolean withExamExercises);

	List<IExercise> getExercisesFromExam(String examTitle);

	/**
	 *
	 * @return the {@link IExercise#getShortName()}s of the given {@link ICourse}
	 */
	List<String> getExerciseShortNames(String courseShortName);

	/**
	 *
	 * @return the {@link IExercise#getShortName()}s of the given {@link IExam}
	 */
	List<String> getExerciseShortNamesFromExam(String examTitle);

	/**
	 * Pre-condition: You need to have called startAssessment or startNextAssessment
	 * prior to calling this method!
	 *
	 * @return all auto feedbacks gotten by starting the assessment (junit test
	 *         results).
	 */
	List<Feedback> getPrecalculatedAutoFeedbacks(ISubmission submission);

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
	boolean saveAssessment(IExercise exercise, ISubmission submission, boolean submit, boolean invalidSubmission);

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
	
	boolean loadExerciseInWorkspaceForStudent(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming);
	
	boolean submitSolution(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming);

	Optional<Set<String>> cleanWorkspace(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming);
}
