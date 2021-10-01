package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.util.List;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;

public interface IExam {

	int getExamId();

	List<IExerciseGroup> getExerciseGroups() throws ArtemisClientException;

	String getTitle();
}
