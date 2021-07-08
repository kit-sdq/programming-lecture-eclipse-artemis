package edu.kit.kastel.sdq.eclipse.grading.api;

import java.io.File;

public interface ISystemwideController {

	/**
	 *
	 * @return the one artemis gui controller.
	 */
	IArtemisGUIController getArtemisGUIController();

	/**
	 *
	 *  Get assessment controller for given params. courseID and exerciseID are filled by call to Artemis.
	 *
	 * @param submissionID the submission for which this assessmentController should manage the assessment.
	 * @param exerciseConfigName the name identifying the exercise configuration.
	 * @return an exercise-specific assessment controller.
	 */
	IAssessmentController getAssessmentController(int submissionID, String exerciseConfigName);

	/**
	 *
	 *  Get assessment controller for given params.
	 */
	IAssessmentController getAssessmentController(int submissionID, String exerciseConfigName, int courseID, int exerciseID);

	/**
	 * set the new config globally.
	 * @param newConfigFile
	 */
	void setConfigFile(File newConfigFile);
}
