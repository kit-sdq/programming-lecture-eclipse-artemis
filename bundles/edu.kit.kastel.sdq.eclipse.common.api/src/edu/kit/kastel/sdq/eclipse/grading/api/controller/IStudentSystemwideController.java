/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.api.controller;

import java.util.List;
import java.util.Set;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IStudentExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.WebsocketCallback;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.util.Pair;

public interface IStudentSystemwideController extends ISystemwideController {

	/**
	 * Commits and pushed selected exercise.
	 *
	 * @return true if successful
	 */
	boolean submitSolution();

	/**
	 * Cleans status of the selected exercise
	 *
	 * @return
	 */
	boolean cleanWorkspace();

	/**
	 * Returns map of all result and its feedbacks of the selected exercise.
	 *
	 * @return the current result and all feedbacks
	 */
	Pair<ResultsDTO, List<Feedback>> getFeedbackExcerise();

	/**
	 * True if the selected exercise is in the past.
	 *
	 * @return
	 */
	boolean isSelectedExerciseExpired();

	/**
	 * Returns the selected exercise.
	 *
	 * @return
	 */
	IExercise getCurrentSelectedExercise();

	/**
	 * Sets the exam as selected and returns it.
	 *
	 * @param examName
	 * @return
	 */
	IExam setExam(String examName);

	/**
	 * Sets the exercise as selected. Exercise has to be an exercise of an exam.
	 *
	 * @param exerciseShortName
	 * @throws ArtemisClientException
	 */
	void setExerciseIdWithSelectedExam(String exerciseShortName) throws ArtemisClientException;

	/**
	 * Returns the selected exam.
	 *
	 * @return
	 */
	IStudentExam getExam();

	/**
	 * Starts the selected exam and returns it.
	 *
	 * @return
	 */
	IStudentExam startExam();

	/**
	 * Returns all short names of the exercises of the exam with name
	 * "examShortName".
	 *
	 * @param examShortName
	 * @return
	 */
	List<IExercise> getExerciseShortNamesFromExam(String examShortName);

	/**
	 * Loads selected exercise into local workspace.
	 *
	 * @return true if successful, false if exercise already loaded.
	 */
	boolean loadExerciseForStudent();

	/**
	 * Connect to Artemis websocket.
	 *
	 * @param callback defines how to handle websocket events and errors.
	 * @return
	 */
	boolean connectToWebsocket(WebsocketCallback callback);

	/**
	 * Set the selected exam to null.
	 */
	void setExamToNull();

	/**
	 * Calculates the link to access the selected exam in Artemis.
	 *
	 * @return url to artemis.
	 */
	String getExamUrlForCurrentExam();

	/**
	 * Deletes the currently selected exercise from workspace and clones it again
	 * from origin.
	 *
	 * @return
	 */
	boolean resetSelectedExercise();

	/**
	 * True if exercise is in local workspace.
	 *
	 * @return
	 */
	boolean isSelectedExerciseInWorkspace();

	/**
	 * Sets all attributes in backend to null.
	 */
	void resetBackendState();

	/**
	 *
	 * @return Annotations of currently selected exercise.
	 */
	Set<IAnnotation> getAnnotations();
}
