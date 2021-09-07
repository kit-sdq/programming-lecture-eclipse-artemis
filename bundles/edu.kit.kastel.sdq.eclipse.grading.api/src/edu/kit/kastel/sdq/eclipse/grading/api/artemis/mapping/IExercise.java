package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;

public interface IExercise {

    int getExerciseId();

    Boolean getSecondCorrectionEnabled();

    String getShortName();

    Collection<ISubmission> getSubmissions() throws ArtemisClientException;

    String getTestRepositoryUrl();

    String getTitle();
}
