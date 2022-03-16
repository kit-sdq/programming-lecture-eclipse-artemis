package edu.kit.kastel.sdq.eclipse.grading.api.controller;

import java.io.File;
import java.util.Optional;
import java.util.Set;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

/**
 * Controls tasks concerned with load, saving and cleaning exercises in the
 * local workspace.
 */
public interface IExerciseArtemisController extends IController {
	/**
	 * Clones exercise and a submission into one project.
	 * 
	 * @param exercise
	 * @param submission
	 * @param dir
	 * @param namingStrategy
	 * @throws ArtemisClientException
	 */
	void downloadExerciseAndSubmission(IExercise exercise, ISubmission submission, File dir, IProjectFileNamingStrategy namingStrategy)
			throws ArtemisClientException;

	/**
	 * Clones git project of repoUrl into local workspace.
	 * 
	 * @param course
	 * @param exercise
	 * @param projectNaming
	 * @param repoUrl
	 * @throws ArtemisClientException
	 */
	void loadExerciseInWorkspaceForStudent(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming, String repoUrl)
			throws ArtemisClientException;

	/**
	 * Cleans state of local exercise.
	 * 
	 * @param course
	 * @param exercise
	 * @param projectNaming
	 * @return
	 */
	Optional<Set<String>> cleanWorkspace(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming);

	/**
	 * Commits exercise and pushed it to the remote workspace.
	 * 
	 * @param course
	 * @param exercise
	 * @param projectNaming
	 * @return
	 * @throws ArtemisClientException
	 */
	boolean commitAndPushExercise(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming) throws ArtemisClientException;

	/**
	 * Delete folder of exercise in local workspace.
	 * 
	 * @param course
	 * @param exercise
	 * @param projectNaming
	 * @throws ArtemisClientException if error occurred.
	 */
	void deleteExercise(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming) throws ArtemisClientException;

	/**
	 * 
	 * @param course
	 * @param exercise
	 * @param projectNaming
	 * @return true if exercise is in local workspace.
	 */
	boolean isExerciseInWorkspace(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming);

}
