package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;

/**
 * This Class represents an artemis course.
 */
public interface ICourse {
    int getCourseId();

    Collection<IExam> getExams() throws ArtemisClientException;

    Collection<IExercise> getExercises() throws ArtemisClientException;

    /**
     *
     * @return the unique shortName of this course.
     */
    String getShortName();

    String getTitle();
}
