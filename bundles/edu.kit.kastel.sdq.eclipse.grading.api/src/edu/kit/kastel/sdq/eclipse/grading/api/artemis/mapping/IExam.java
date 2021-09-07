package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;

public interface IExam {

    int getExamId();

    Collection<IExerciseGroup> getExerciseGroups() throws ArtemisClientException;

    String getTitle();
}
