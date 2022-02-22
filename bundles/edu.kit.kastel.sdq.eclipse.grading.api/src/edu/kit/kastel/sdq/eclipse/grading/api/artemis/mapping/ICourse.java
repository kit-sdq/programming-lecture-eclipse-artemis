package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.io.Serializable;
import java.util.List;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;

/**
 * This Class represents an artemis course.
 */
public interface ICourse extends Serializable {
	int getCourseId();

	List<IExam> getExams() throws ArtemisClientException;

	List<IExercise> getExercises() throws ArtemisClientException;

	String getShortName();

	String getTitle();

	boolean isInstructor(Assessor assessor);

}
