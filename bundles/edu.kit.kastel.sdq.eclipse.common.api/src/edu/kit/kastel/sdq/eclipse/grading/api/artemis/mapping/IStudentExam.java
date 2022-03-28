/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.util.List;

/**
 * Represents a StudentExam Object from Artemis. It contains a regular exam
 * object and all its exercises.
 */
public interface IStudentExam {

	/**
	 * @return the exam of the StudentExam.
	 */
	IExam getExam();

	/**
	 * @return all the exercises of the exam.
	 */
	List<IExercise> getExercises();

	/**
	 * @return true if a solution was submitted.
	 */
	boolean isSubmitted();

	/**
	 * @return true if the submission period ended.
	 */
	boolean isEnded();

	/**
	 * @return true if the student has stated the student exam.
	 */
	boolean isStarted();
}
