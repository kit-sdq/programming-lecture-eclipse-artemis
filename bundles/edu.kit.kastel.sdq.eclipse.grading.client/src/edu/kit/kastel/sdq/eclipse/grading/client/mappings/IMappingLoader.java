package edu.kit.kastel.sdq.eclipse.grading.client.mappings;

import java.util.ArrayList;
import java.util.List;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

public interface IMappingLoader {

	List<IExerciseGroup> getExerciseGroupsForExam(IExam artemisExam, ICourse course) throws ArtemisClientException;

	default List<ISubmission> getSubmissionsForExercise(IExercise artemisExercise) throws ArtemisClientException {
		List<ISubmission> submissions = new ArrayList<>();
		// Correction Round 0 and 1
		submissions.addAll(this.getSubmissionsForExercise(artemisExercise, 0));
		submissions.addAll(this.getSubmissionsForExercise(artemisExercise, 1));
		return submissions;
	}

	List<ISubmission> getSubmissionsForExercise(IExercise artemisExercise, int correctionRound) throws ArtemisClientException;

	List<IExam> getExamsForCourse(ICourse artemisCourse) throws ArtemisClientException;

	List<IExercise> getExercisesForCourse(ICourse artemisCourse) throws ArtemisClientException;

}
