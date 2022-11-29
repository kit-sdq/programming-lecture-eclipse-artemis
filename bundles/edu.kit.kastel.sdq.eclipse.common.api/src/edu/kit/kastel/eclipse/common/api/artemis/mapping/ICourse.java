/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.artemis.mapping;

import java.io.Serializable;
import java.util.List;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;

public interface ICourse extends Serializable {
	int getCourseId();

	String getShortName();

	List<IExercise> getExercises() throws ArtemisClientException;

	List<IExam> getExams() throws ArtemisClientException;

	boolean isInstructor(User assessor);
}
