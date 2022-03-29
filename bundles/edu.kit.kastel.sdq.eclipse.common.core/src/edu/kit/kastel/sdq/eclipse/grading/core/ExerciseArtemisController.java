/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.Constants;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.AbstractController;
import edu.kit.kastel.sdq.eclipse.grading.api.controller.IExerciseArtemisController;
import edu.kit.kastel.sdq.eclipse.grading.client.git.GitException;
import edu.kit.kastel.sdq.eclipse.grading.client.git.GitHandler;
import edu.kit.kastel.sdq.eclipse.grading.core.artemis.WorkspaceUtil;

public class ExerciseArtemisController extends AbstractController implements IExerciseArtemisController {
	private String username;
	private String password;

	public ExerciseArtemisController(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public void loadExerciseInWorkspaceForStudent(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming, String repoUrl)
			throws ArtemisClientException {
		final File eclipseWorkspaceRoot = WorkspaceUtil.getWorkspaceFile();

		// abort if directory already exists.
		this.existsAndThrow(projectNaming.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, null));

		try {
			this.downloadExercise(exercise, eclipseWorkspaceRoot, repoUrl, projectNaming);
		} catch (ArtemisClientException e) {
			throw new ArtemisClientException("Error, can not download exercise.", e);
		}
		try {
			WorkspaceUtil.createEclipseProject(projectNaming.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, null));
		} catch (CoreException e) {
			throw new ArtemisClientException("Error, can not create project.", e);
		}
	}

	@Override
	public void downloadExerciseAndSubmission(IExercise exercise, ISubmission submission, File dir, IProjectFileNamingStrategy namingStrategy)
			throws ArtemisClientException {
		final File projectDirectory = namingStrategy.getProjectFileInWorkspace(dir, exercise, submission);
		this.existsAndThrow(projectDirectory);
		try {
			if (projectDirectory.exists()) {
				throw new ArtemisClientException("Could not clone project " + projectDirectory.getName() + ", " + "directory already exists!");
			}

			var credentials = new UsernamePasswordCredentialsProvider(this.username, this.password);
			// Download test repository
			GitHandler.cloneRepo(projectDirectory, exercise.getTestRepositoryUrl(), Constants.MAIN_BRANCH_NAME, credentials);
			// download submission inside the exercise project directory
			GitHandler.cloneRepo(namingStrategy.getAssignmentFileInProjectDirectory(projectDirectory), submission.getRepositoryUrl(),
					Constants.MAIN_BRANCH_NAME, credentials);
		} catch (GitException e) {
			throw new ArtemisClientException("Unable to download exercise and submission: " + e.getMessage(), e);
		}

	}

	private void downloadExercise(IExercise exercise, File dir, String repoUrl, IProjectFileNamingStrategy namingStrategy) throws ArtemisClientException {
		final File projectDirectory = namingStrategy.getProjectFileInWorkspace(dir, exercise, null);
		try {
			if (projectDirectory.exists()) {
				throw new ArtemisClientException("Could not clone project " + projectDirectory.getName() + ", " + "directory already exists!");
			}

			// Download test repository
			var credentials = new UsernamePasswordCredentialsProvider(this.username, this.password);
			GitHandler.cloneRepo(projectDirectory, repoUrl, Constants.MAIN_BRANCH_NAME, credentials);
		} catch (GitException e) {
			throw new ArtemisClientException("Unable to download exercise: " + e.getMessage(), e);
		}
	}

	@Override
	public Optional<Set<String>> cleanWorkspace(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming) {
		final File eclipseWorkspaceRoot = WorkspaceUtil.getWorkspaceFile();
		File exerciseRepo = projectNaming.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, null);
		File gitFileInRepo = projectNaming.getGitFileInProjectDirectory(exerciseRepo);
		try {
			return Optional.of(GitHandler.cleanRepo(gitFileInRepo));
		} catch (GitException e) {
			return Optional.empty();
		}
	}

	@Override
	public boolean commitAndPushExercise(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming) throws ArtemisClientException {
		final File eclipseWorkspaceRoot = WorkspaceUtil.getWorkspaceFile();
		File exerciseRepo = projectNaming.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, null);
		File gitFileInRepo = projectNaming.getGitFileInProjectDirectory(exerciseRepo);
		try {
			GitHandler.pullExercise(this.username, this.password, exerciseRepo);
			GitHandler.commitExercise(this.username, this.username, this.createCommitMsg(course, exercise), gitFileInRepo);
		} catch (GitException e) {
			throw new ArtemisClientException("Can't save selected exercise " + exercise.getShortName() //
					+ ".\n Exercise not found in workspace. \n Please load exercise before submitting it.", e);
		}

		try {
			var credentials = new UsernamePasswordCredentialsProvider(this.username, this.password);
			GitHandler.pushExercise(gitFileInRepo, credentials);
		} catch (GitException e) {
			throw new ArtemisClientException("Can't upload solution. Please check if submissions are still possible.", e);
		}
		return true;
	}

	@Override
	public void deleteExercise(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming) throws ArtemisClientException {
		final File eclipseWorkspaceRoot = WorkspaceUtil.getWorkspaceFile();
		File exerciseRepo = projectNaming.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, null);
		if (!exerciseRepo.exists()) {
			throw new ArtemisClientException("Could not delete folder " + exerciseRepo.getName() + ", " + "directory does not exist!");
		}

		try {
			WorkspaceUtil.deleteEclipseProject(exerciseRepo.getName());
		} catch (IOException | CoreException e) {
			throw new ArtemisClientException("ERROR, can not delete eclipse project: " + exerciseRepo.getAbsolutePath());
		}
	}

	@Override
	public boolean isExerciseInWorkspace(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming) {
		final File eclipseWorkspaceRoot = WorkspaceUtil.getWorkspaceFile();
		File exerciseRepo = projectNaming.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, null);
		return exerciseRepo.exists();
	}

	public static boolean deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (null != files) {
				for (File file : files) {
					if (file.isDirectory()) {
						deleteDirectory(file);
					} else {
						file.delete();
					}
				}
			}
		}
		return directory.delete();
	}

	private void existsAndThrow(File file) throws ArtemisClientException {
		if (file.exists()) {
			throw new ArtemisClientException(
					"Project " + file.getName() + " could not be cloned since the workspace " + "already contains a project with that name. \n"
							+ "Trying to load and merge previously created annotations. Please double-check them before submitting the assessment! \n"
							+ "If you want to start again from skretch, please delete the project and retry.");
		}
	}

	private String createCommitMsg(ICourse course, IExercise exercise) {
		return String.format("[ECLIPSE-STUDENT] - uploaded new solution for exercise %s of course %s.", exercise.getShortName(), course.getShortName());
	}
}
