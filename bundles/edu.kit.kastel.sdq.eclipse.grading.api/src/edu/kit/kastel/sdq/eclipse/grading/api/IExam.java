package edu.kit.kastel.sdq.eclipse.grading.api;

import java.util.Collection;

public interface IExam {

	int getExamId();

	Collection<IExerciseGroup> getExerciseGroups();

	String getTitle();
}
