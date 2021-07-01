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
	 * @param submissionID the submission for which this assessmentController should manage the assessment.
	 * @param exerciseConfigName the name identifying the exercise configuration.
	 * @return an exercise-specific assessment controller.
	 */
	IAssessmentController getAssessmentController(int submissionID, String exerciseConfigName);

	/**
	 * set the new config globally.
	 * @param newConfigFile
	 */
	void setConfigFile(File newConfigFile);
}
