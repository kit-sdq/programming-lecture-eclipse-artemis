/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.view.controllers;

import java.util.List;

import edu.kit.kastel.eclipse.common.api.controller.IArtemisController;
import edu.kit.kastel.eclipse.common.api.controller.ISystemwideController;
import edu.kit.kastel.eclipse.common.api.controller.IViewInteraction;
import edu.kit.kastel.sdq.artemis4j.api.ArtemisClientException;

/**
 * This abstract class is the base for controllers for a view for artemis. It
 * holds all general controllers for the backend calls.
 *
 * @param <C> the type of the {@link ISystemwideController}
 */
public abstract class AbstractArtemisViewController<C extends ISystemwideController> {
	protected final C systemwideController;

	private IArtemisController artemisController;
	protected final IViewInteraction viewObserver;

	protected AbstractArtemisViewController(C systemwideController) {
		this.systemwideController = systemwideController;
		this.viewObserver = systemwideController.getViewInteractionHandler();
	}

	protected void initializeControllersAndObserver() {
		this.artemisController = this.systemwideController.getArtemisController();
	}

	/**
	 * @return the name of all courses
	 */
	public List<String> getCourseShortNames() {
		return this.artemisController.getCourseShortNames();
	}

	/**
	 * @param courseTitle (of the selected course in the combo)
	 * @return all exams of the given course
	 */
	public List<String> getExamShortNames(String courseTitle) {
		return this.artemisController.getExamTitles(courseTitle);
	}

	/**
	 * @param courseName (selected course in the combo)
	 * @return all exercises from the given course
	 */
	public List<String> getExerciseShortNames(String courseName) {
		try {
			return this.systemwideController.setCourseIdAndGetExerciseShortNames(courseName);
		} catch (ArtemisClientException e) {
			this.viewObserver.error(e.getMessage(), e);
			return List.of();
		}
	}

	/**
	 * @param examShortName (of the selected exam in the combo)
	 * @return all exercises of the given exam
	 */
	public List<String> getExercisesShortNamesForExam(String examShortName) {
		return this.artemisController.getExerciseShortNamesFromExam(examShortName);
	}

	/**
	 * Sets the exercise ID of the selected exercise
	 *
	 * @param exerciseShortName (of the selected exercise in the combo)
	 */
	public void setExerciseId(String exerciseShortName) {
		try {
			this.systemwideController.setExerciseId(exerciseShortName);
		} catch (ArtemisClientException e) {
			this.viewObserver.error(e.getMessage(), e);
		}
	}

}
