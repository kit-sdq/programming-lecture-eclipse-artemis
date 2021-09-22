package edu.kit.kastel.sdq.eclipse.grading.api;

import java.util.Collection;
import java.util.Optional;

import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObservable;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IFeedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

/**
 * Works as an interface from backend to ArtemisClient
 *
 */
public interface IArtemisController {

    /**
     * Download submissions defined by the given submissionIds
     *
     * @param submissionIds
     * @return whether download was successful or not
     */
    boolean downloadExerciseAndSubmission(int courseID, int exerciseID, int submissionID,
            IProjectFileNamingStrategy projectNaming);

    /**
     * You may subscribe to the {@link IAlertObservable} this method returns to be alerted on errors
     * since this replaces Exceptions.
     *
     * @return this ArtemisGUIController's {@link IAlertObservable} (Observer/ Observable pattern).
     */
    IAlertObservable getAlertObservable();

    /**
     *
     * @return all IFeedbacks that were gotten in the process of locking the given submission.
     */
    Collection<IFeedback> getAllFeedbacksGottenFromLocking(int submissionID);

    /**
     *
     * @param exerciseID
     * @return all submissions of the given @link {@link IExercise}, that have been started, saved
     *         or submitted by the caller.
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

    /**
     * Convenience method. Search the given ids in the given courses.
     *
     * @param courses
     *            the data in which to search for the exercise
     * @param courseID
     * @param exerciseID
     * @return the exercise, if found. null else.
     */
    IExercise getExerciseFromCourses(Collection<ICourse> courses, int courseID, int exerciseID);

    Collection<IExercise> getExercises(int courseID, boolean withExamExercises);

    Collection<IExercise> getExercisesFromExam(String examTitle);

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
     * Pre-condition: You need to have called startAssessment or startNextAssessment prior to
     * calling this method!
     *
     * @return all auto feedbacks gotten by starting the assessment (junit test results).
     */
    Collection<IFeedback> getPrecalculatedAutoFeedbacks(int submissionID);

    /**
     * Convenience method. Search the given ids in the given courses.
     *
     * @param courses
     *            the data in which to search for the submission
     * @param courseID
     * @param exerciseID
     * @return the submission, if found. null else.
     */
    ISubmission getSubmissionFromExercise(IExercise exercise, int submissionID);

    /**
     * Submit the assessment to Artemis. Must have been started by {@link #startAssessment(int)},
     * {@link #startNextAssessment(int)} or {@link #startNextAssessment(int, int)}, before!
     *
     * @param submissionID
     * @param submit
     *            should the assessment be submitted or merely saved to artemis?
     * @param invalidSubmission
     *            is the submission invalid? Will return 0 points.
     * @param exerciseName
     *            the exercise name is used to internally identify which annotations should be sent.
     *
     * @return whether the operation was successful.
     */
    boolean saveAssessment(int submissionID, boolean submit, boolean invalidSubmission);

    /**
     * Starts an assessment for the given submission. Acquires a lock in the process.
     *
     * @param submissionID
     */
    void startAssessment(int submissionID);

    /**
     * Starts the next assessment. Which one is smh determined by artemis. Correction Round is set
     * to 0.
     *
     * @param exerciseID
     *            the exerciseID (found in your ICourse-Collection gotten via
     *            IArtemisController::getCourses())
     * @return
     *         <li>the submissionID which defines what is assessed.
     *         <li>Optional.empty(), if no assessment is left!
     */
    Optional<Integer> startNextAssessment(int exerciseID);

    /**
     * Starts the next assessment of the given correction round. Which one is smh determined by
     * artemis.
     *
     * @param exerciseID
     *            the exerciseID (found in your ICourse-Collection gotten via
     *            IArtemisController::getCourses())
     * @param correctionRound
     *            for non-exams: 0. For exams: either 0 or 1
     * @return
     *         <li>the submissionID which defines what is assessed.
     *         <li>Optional.empty(), if no assessment is left!
     */
    Optional<Integer> startNextAssessment(int exerciseID, int correctionRound);
}
