package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.util.List;

/**
 *	Represents a StudentExam Object from Artemis. It contains a regular exam object and all its exercises. 
 */
public interface IStudentExam {
	
	/*
	 * Return the exam of the StudentExam.
	 */
	IExam getExam();
	
	/*
	 * Returns all the exercises of the exam.
	 */
	List<IExercise> getExercises();

}
