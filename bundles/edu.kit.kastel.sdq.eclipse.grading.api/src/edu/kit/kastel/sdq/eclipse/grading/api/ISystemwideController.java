package edu.kit.kastel.sdq.eclipse.grading.api;

import java.io.File;
import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObservable;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

public interface ISystemwideController {

	/**
	 * You may subscribe to the {@link IAlertObservable} this method returns to be alerted on errors since
	 * this replaces Exceptions.
	 * @return this SystemwideControllers {@link IAlertObservable} (Observer/ Observable pattern).
	 */
	IAlertObservable getAlertObservable();

	/**
	 *
	 * @return the one artemis gui controller.
	 */
	IArtemisGUIController getArtemisGUIController();

	/**
	 * <B>BACKLOG</B><br/>
	 * Get all submissions (their project names) which were sometime started by the calling tutor. Based on current exercise.
	 * ISystemwideController::setExerciseId() must have been called before!
	 * TODO see tolle Zustandsautomat-Grafik!
	 *
	 * @param filter determine which kinds of submissions should be filtered (= be in the result)
	 * @return the respective project Names (unique).
	 */
	Collection<String> getBegunSubmissionsProjectNames(ISubmission.Filter filter);

	/**
	 *
	 *  Get assessment controller for current state (courseID, exerciseID, submissionID, exerciseConfig).
	 */
	IAssessmentController getCurrentAssessmentController();

	/**
	 * <B>BACKLOG</B><br/>
	 * <li>Loads an already assessed (started, saved or even submitted) submission for re-assessment.
	 *
	 * <li>You need to select a submission via {@link #setAssessedSubmission(int)}, first!
	 * Has the same effect as {@link #startAssessment()} otherwise.
	 * <li>See docs/Zustandshaltung-Automat
	 */
	void loadAgain();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>Deletes local project. Renews the lock and downloads the submission project again.
	 * <li>See docs/Zustandshaltung-Automat
	 */
	void reloadAssessment();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>Saves the assessment to Artemis.
	 * <li>See docs/Zustandshaltung-Automat
	 */
	void saveAssessment();

	/**
	 * <B>BACKLOG</B><br/>
	 * <li>Only sets the submission, does not start the assessment!.
	 * <li>You want to have called {@link #getBegunSubmissions(ISubmission.Filter)}, first!
	 * <li>See docs/Zustandshaltung-Automat
	 *
	 */
	void setAssessedSubmissionByProjectName(String projectName);

	/**
	 * set the new annotation model config globally.
	 * @param newConfigFile
	 */
	void setConfigFile(File newConfigFile);

	/**
	 *
	 * <B>ASSESSMENT - STATE</B><br/>
	 * <li>Set the current course for further assessment-related actions, such as {@link #setExerciseId(String)}
	 * <li>See docs/Zustandshaltung-Automat
	 *
	 * @param courseShortName unique short name
	 * @return all exercise short names. Can be used to call {@link #setExerciseId(String)}.
	 */
	Collection<String> setCourseIdAndGetExerciseShortNames(String courseShortName);

	/**
	 * <B>ASSESSMENT - STATE</B><br/>
	 * <li>Set the current exercise for further assessment-related actions, such as {@link #startAssessment()}
	 * <li>See docs/Zustandshaltung-Automat
	 *
	 * @param exerciseShortName unique short name
	 */
	void setExerciseId(String exerciseShortName);

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li> current state (exerciseID, courseID) is used for call to artemis: nextAssessement
	 * <li> if an assessment is available, it is downloaded and locked.
	 * <li> See docs/Zustandshaltung-Automat
	 *
	 * @return whether a new assessment was started or not, depending on whether there was a submission available.
	 */
	boolean startAssessment();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>The same as {@link #startAssessment()}.
	 * <li>See docs/Zustandshaltung-Automat
	 * @return whether a new assessment was started or not, depending on whether there was a submission available.
	 */
	boolean startCorrectionRound1();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>Like {@link #startAssessment()}, but with correction round 2 as a parameter.
	 * <li>See docs/Zustandshaltung-Automat
	 * @return whether a new assessment was started or not, depending on whether there was a submission available.
	 */
	boolean startCorrectionRound2();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>Saves and submits the assessment to Artemis. Deletes project (in eclipse and on files system) thereafter.
	 * <li>See docs/Zustandshaltung-Automat
	 */
	void submitAssessment();
}
