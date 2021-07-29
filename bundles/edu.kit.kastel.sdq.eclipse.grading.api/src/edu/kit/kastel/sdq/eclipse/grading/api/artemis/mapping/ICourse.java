package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.util.Collection;

/**
 * This Class represents an artemis course.
 */
public interface ICourse {
	int getCourseId();

	Collection<IExam> getExams();

	Collection<IExercise> getExercises();

	/**
	 *
	 * @return the unique shortName of this course.
	 */
	String getShortName();

	String getTitle();
}
