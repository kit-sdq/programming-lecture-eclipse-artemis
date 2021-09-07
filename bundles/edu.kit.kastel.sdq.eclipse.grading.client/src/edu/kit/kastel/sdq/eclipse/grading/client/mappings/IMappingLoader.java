package edu.kit.kastel.sdq.eclipse.grading.client.mappings;

import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

public interface IMappingLoader {

    Collection<IExerciseGroup> getExerciseGroupsForExam(IExam artemisExam, ICourse course)
            throws ArtemisClientException;

    Collection<ISubmission> getSubmissionsForExercise(IExercise artemisExercise) throws ArtemisClientException;

    Collection<IExam> getExamsForCourse(ICourse artemisCourse) throws ArtemisClientException;

    Collection<IExercise> getExercisesForCourse(ICourse artemisCourse) throws ArtemisClientException;

}
