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
	 *
	 *  Get assessment controller for current state (courseID, exerciseID, submissionID, exerciseConfig).
	 */
	IAssessmentController getCurrentAssessmentController();

	void onReloadAssessmentButton();

	void onSaveAssessmentButton();

	/**
	 * GUI Button method:
	 * <li> current state (exerciseID, courseID) is used for call to artemis: nextAssessement
	 * <li> if an assessment is available, it is downloaded and locked.
	 * @return whether a new assessment was started or not, depending on whether there was a submission available.
	 */
	boolean onStartAssessmentButton();

	boolean onStartCorrectionRound1Button();

	boolean onStartCorrectionRound2Button();

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
