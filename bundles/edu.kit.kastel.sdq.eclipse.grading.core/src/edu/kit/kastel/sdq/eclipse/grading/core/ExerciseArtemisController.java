package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.File;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
		final File eclipseWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();

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

			// Download test repository
			GitHandler.cloneRepo(projectDirectory, exercise.getTestRepositoryUrl(), Constants.MASTER_BRANCH_NAME);
			// download submission inside the exercise project directory
			GitHandler.cloneRepo(namingStrategy.getAssignmentFileInProjectDirectory(projectDirectory), //
					submission.getRepositoryUrl(), Constants.MASTER_BRANCH_NAME);
		} catch (GitException e) {
			throw new ArtemisClientException("Unable to download exercise and submission: " + e.getMessage(), e);
		}

	}

	public void downloadExercise(IExercise exercise, File dir, String repoUrl, IProjectFileNamingStrategy namingStrategy) throws ArtemisClientException {
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

	@Override
	public Optional<Set<String>> cleanWorkspace(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming) {
		final File eclipseWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
		File exeriseRepo = projectNaming.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, null);
		File gitFileInRepo = projectNaming.getGitFileInProjectDirectory(exeriseRepo);
		try {
			return Optional.of(GitHandler.cleanRepo(gitFileInRepo));
		} catch (GitException e) {
			return Optional.empty();
		}
	}

	@Override
	public boolean commitAndPushExercise(ICourse course, IExercise exercise, IProjectFileNamingStrategy projectNaming) throws ArtemisClientException {
		final File eclipseWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
		File exeriseRepo = projectNaming.getProjectFileInWorkspace(eclipseWorkspaceRoot, exercise, null);
		File gitFileInRepo = projectNaming.getGitFileInProjectDirectory(exeriseRepo);
		try {
			GitHandler.pullExercise(username, password, exeriseRepo);
			GitHandler.commitExercise(username, username, createCommitMsg(course, exercise), gitFileInRepo);
		} catch (GitException e) {
			throw new ArtemisClientException("Can't save selected exercise " + exercise.getShortName() //
					+ ".\n Exercise not found in workspace. \n Please load exercise before submitting it.", e);
		}

		try {
			GitHandler.pushExercise(username, password, gitFileInRepo);
		} catch (GitException e) {
			throw new ArtemisClientException("Can't upload solution. Please check if submissions are still possible.", e);
		}
		return true;
	}

	private void existsAndThrow(File file) throws ArtemisClientException {
		if (file.exists()) {
			throw new ArtemisClientException("Project " + file.getName() + " could not be cloned since the workspace "
					+ "already contains a project with that name. \n"
					+ "Trying to load and merge previously created annotations. Please double-check them before submitting the assessment! \n"
					+ "If you want to start again from skretch, please delete the project and retry.");
		}
	}

	private String createCommitMsg(ICourse course, IExercise exercise) {
		return String.format("[ECLIPSE-STUDENT] - uploaded new solution for exercise %s of course %s.", exercise.getShortName(), course.getShortName());
	}
}
