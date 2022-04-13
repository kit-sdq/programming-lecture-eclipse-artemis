/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.client.mappings;

import java.util.List;

import edu.kit.kastel.sdq.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.ISubmission;

public interface IMappingLoader {

	List<IExerciseGroup> getExerciseGroupsForExam(IExam artemisExam, ICourse course) throws ArtemisClientException;

	List<IExam> getExamsForCourse(ICourse artemisCourse) throws ArtemisClientException;

	List<IExercise> getStudentExercisesForCourse(ICourse artemisCourse) throws ArtemisClientException;

	List<IExercise> getGradingExercisesForCourse(ICourse artemisCourse) throws ArtemisClientException;

	ISubmission getSubmissionById(IExercise artemisExercise, int submissionId) throws ArtemisClientException;

}
