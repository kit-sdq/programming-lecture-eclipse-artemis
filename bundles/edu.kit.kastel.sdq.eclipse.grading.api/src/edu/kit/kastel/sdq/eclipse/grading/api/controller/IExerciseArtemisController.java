package edu.kit.kastel.sdq.eclipse.grading.api.controller;

import java.io.File;
import java.util.Optional;
import java.util.Set;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

public interface IExerciseArtemisController extends IController {
	/**
	 * Clones exercise and a submission into one project.
	 */
	void downloadExerciseAndSubmission(IExercise exercise, ISubmission submission, File dir, IProjectFileNamingStrategy namingStrategy)
			throws ArtemisClientException;

	void loadExerciseInWorkspaceForStudent(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming, String repoUrl)
			throws ArtemisClientException;

	Optional<Set<String>> cleanWorkspace(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming);

	boolean commitAndPushExercise(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming) throws ArtemisClientException;

}
