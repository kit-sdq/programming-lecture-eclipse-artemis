package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.File;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.Constants;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IExerciseArtemisController;
import edu.kit.kastel.sdq.eclipse.grading.client.git.GitException;
import edu.kit.kastel.sdq.eclipse.grading.client.git.GitHandler;

public class ExerciseArtemisController implements IExerciseArtemisController {
	private static final ILog log = Platform.getLog(ExerciseArtemisController.class);

	private IProjectFileNamingStrategy namingStrategy;
	public ExerciseArtemisController(IProjectFileNamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
	}

	@Override
	public void downloadExerciseAndSubmission(IExercise exercise, ISubmission submission, File dir) throws ArtemisClientException {
		final File projectDirectory = namingStrategy.getProjectFileInWorkspace(dir, exercise, submission);
		try {
			if (projectDirectory.exists()) {
				throw new ArtemisClientException("Could not clone project " + projectDirectory.getName() + ", " + "directory already exists!");
			}

			// Download test repository
			GitHandler.cloneRepo(projectDirectory, exercise.getTestRepositoryUrl(), Constants.MASTER_BRANCH_NAME);
			// download submission inside the exercise project directory
			GitHandler.cloneRepo(namingStrategy.getAssignmentFileInProjectDirectory(projectDirectory), //
					submission.getRepositoryUrl(), Constants.MASTER_BRANCH_NAME);
		} catch (GitException e) {
			throw new ArtemisClientException("Unable to download exercise and submission: " + e.getMessage(), e);
		}
		
	}

	@Override
	public void downloadExercise(IExercise exercise, File dir, String repoUrl) throws ArtemisClientException {
		final File projectDirectory = namingStrategy.getProjectFileInWorkspace(dir, exercise, null);
		try {
			if (projectDirectory.exists()) {
				throw new ArtemisClientException("Could not clone project " + projectDirectory.getName() + ", " + "directory already exists!");
			}

			// Download test repository
			GitHandler.cloneRepo(projectDirectory, repoUrl, Constants.MASTER_BRANCH_NAME);
		} catch (GitException e) {
			throw new ArtemisClientException("Unable to download exercise: " + e.getMessage(), e);
		}
	}
}
