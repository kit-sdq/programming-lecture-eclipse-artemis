package edu.kit.kastel.sdq.eclipse.grading.api;

import java.io.File;
import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObservable;

public interface ISystemwideController {

	IAlertObservable getAlertObservable();

	/**
	 *
	 * @return the one artemis gui controller.
	 */
	IArtemisGUIController getArtemisGUIController();

	/**
	 * BACKLOG TAB.
	 * Get all submissionIDs which were sometime started by the calling tutor. Based on current exercise.
	 * ISystemwideController::setExerciseId() must have been called before!
	 *
	 * @param filter determine which kinds of submissions should be filtered (= be in the result)
	 * @return
	 */
	Collection<Integer> getBegunSubmissions(ISubmission.Filter filter);

	/**
	 *
	 *  Get assessment controller for current state (courseID, exerciseID, submissionID, exerciseConfig).
	 */
	IAssessmentController getCurrentAssessmentController();

	/**
	 * BACKLOG TAB.
	 * You need to select a submission via {@link #setAssessedSubmission(int)}, first!
	 * Has the same effect as {@link #onStartAssessmentButton()} otherwise.
	 */
	void onLoadAgainButton();

	/**
	 * Deletes local project. Renews the lock and downloads the submission project again.
	 */
	void onReloadAssessmentButton();

	/**
	 * Saves the assessment to Artemis.
	 */
	void onSaveAssessmentButton();

	/**
	 * GUI Button method:
	 * <li> current state (exerciseID, courseID) is used for call to artemis: nextAssessement
	 * <li> if an assessment is available, it is downloaded and locked.
	 * @return whether a new assessment was started or not, depending on whether there was a submission available.
	 */
	boolean onStartAssessmentButton();

	/**
	 * The same as {@link #onStartAssessmentButton()}.
	 * @return whether a new assessment was started or not, depending on whether there was a submission available.
	 */
	boolean onStartCorrectionRound1Button();

	/**
	 * Like {@link #onStartAssessmentButton()}, but with correction round 2 as a parameter.
	 * @return whether a new assessment was started or not, depending on whether there was a submission available.
	 */
	boolean onStartCorrectionRound2Button();

	/**
	 * Saves and submits the assessment to Artemis. Deletes project (in eclipse and on files system) thereafter.
	 */
	void onSubmitAssessmentButton();

	/**
	 * BACKLOG TAB.
	 */
	void setAssessedSubmission(int submissionID);

	/**
	 * set the new config globally.
	 * @param newConfigFile
	 */
	void setConfigFile(File newConfigFile);


	Collection<String> setCourseIdAndGetExerciseTitles(String courseShortName);

	/**
	 * STATE: current exercise ID.
	 * @param exerciseShortName
	 */
	void setExerciseId(String exerciseShortName);
}
