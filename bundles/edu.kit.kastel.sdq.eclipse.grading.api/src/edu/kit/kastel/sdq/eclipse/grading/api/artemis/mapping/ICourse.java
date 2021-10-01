package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.util.List;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;

/**
 * This Class represents an artemis course.
 */
public interface ICourse {
	int getCourseId();

	List<IExam> getExams() throws ArtemisClientException;

	List<IExercise> getExercises() throws ArtemisClientException;

	String getShortName();

	String getTitle();
}
