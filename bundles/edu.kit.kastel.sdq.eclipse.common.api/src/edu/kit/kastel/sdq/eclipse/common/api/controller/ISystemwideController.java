/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.api.controller;

import java.util.List;

import edu.kit.kastel.sdq.eclipse.common.api.ArtemisClientException;

public interface ISystemwideController extends IController {
	/**
	 *
	 * @return the one artemis gui controller.
	 */
	IArtemisController getArtemisController();

	/**
	 * 
	 * @return the one exercise controller to load submit and clean exercises.
	 */
	IExerciseArtemisController getExerciseArtemisController();

	/**
	 *
	 * <B>ASSESSMENT - STATE</B><br/>
	 * <li>Set the current course for further assessment-related actions, such as
	 * {@link #setExerciseId(String)}
	 * <li>See docs/Zustandshaltung-Automat
	 *
	 * @param courseShortName unique short name
	 * @return all exercise short names. Can be used to call
	 *         {@link #setExerciseId(String)}.
	 * @throws ArtemisClientException
	 */
	List<String> setCourseIdAndGetExerciseShortNames(String courseShortName) throws ArtemisClientException;

	/**
	 * <B>ASSESSMENT - STATE</B><br/>
	 * <li>Set the current exercise for further assessment-related actions, such as
	 * {@link #startAssessment()}
	 * <li>See docs/Zustandshaltung-Automat
	 *
	 * @param exerciseShortName unique short name
	 * @throws ArtemisClientException
	 */
	void setExerciseId(String exerciseShortName) throws ArtemisClientException;

	/**
	 * Pre-Condition: course, exercise and submission must be set!
	 *
	 * @return the current project name.
	 */
	String getCurrentProjectName();
}
