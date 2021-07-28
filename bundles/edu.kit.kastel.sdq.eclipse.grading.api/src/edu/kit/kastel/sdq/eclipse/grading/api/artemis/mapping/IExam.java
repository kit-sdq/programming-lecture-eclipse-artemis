package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.util.Collection;

public interface IExam {

	int getExamId();

	Collection<IExerciseGroup> getExerciseGroups();

	String getTitle();
}
