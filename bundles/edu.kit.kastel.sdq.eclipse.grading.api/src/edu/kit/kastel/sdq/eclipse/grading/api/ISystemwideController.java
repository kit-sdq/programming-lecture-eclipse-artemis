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
	 * @param exerciseName
	 * @return an exercise-specific assessment controller.
	 */
	IAssessmentController getAssessmentController(String exerciseName);

	/**
	 * set the new config globally.
	 * @param newConfigFile
	 */
	void setConfigFile(File newConfigFile);
}
