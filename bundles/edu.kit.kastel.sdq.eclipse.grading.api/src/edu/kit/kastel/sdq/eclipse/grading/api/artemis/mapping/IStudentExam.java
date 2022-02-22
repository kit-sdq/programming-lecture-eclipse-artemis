package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.util.List;

public interface IStudentExam {

	IExam getExam();

	List<IExercise> getExercises();

}
