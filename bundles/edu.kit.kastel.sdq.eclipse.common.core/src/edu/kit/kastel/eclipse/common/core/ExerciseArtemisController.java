/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.core;

import java.io.File;

import org.eclipse.jface.preference.IPreferenceStore;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.PreferenceConstants;
import edu.kit.kastel.eclipse.common.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.User;
import edu.kit.kastel.eclipse.common.api.controller.AbstractController;
import edu.kit.kastel.eclipse.common.api.controller.IExerciseArtemisController;
import edu.kit.kastel.eclipse.common.api.controller.IViewInteraction;
import edu.kit.kastel.eclipse.common.client.git.GitCredentials;
import edu.kit.kastel.eclipse.common.client.git.GitException;
import edu.kit.kastel.eclipse.common.client.git.GitHandler;

public class ExerciseArtemisController extends AbstractController implements IExerciseArtemisController {
	private final String username;
	private final String gitPassword;

	public ExerciseArtemisController(IViewInteraction viewInteractionHandler, User user, IPreferenceStore preferenceStore) {
		super(viewInteractionHandler);
		this.username = user == null ? null : user.getLogin();
		String password = preferenceStore.getString(PreferenceConstants.GENERAL_ADVANCED_ARTEMIS_PASSWORD);
		String gitToken = preferenceStore.getString(PreferenceConstants.GENERAL_ADVANCED_GIT_TOKEN);

		if (gitToken != null && !gitToken.isBlank()) {
			this.gitPassword = gitToken;
		} else if (password != null && !password.isBlank()) {
			this.gitPassword = password;
		} else if (user != null && user.getVcsAccessToken() != null) {
			this.gitPassword = user.getVcsAccessToken();
		} else {
			this.gitPassword = "";
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

			var credentials = new GitCredentials(this.username, this.gitPassword);
			// Download test repository
			GitHandler.cloneRepo(projectDirectory, exercise.getTestRepositoryUrl(), credentials);
			// download submission inside the exercise project directory
			GitHandler.cloneRepo(namingStrategy.getAssignmentFileInProjectDirectory(projectDirectory), submission.getRepositoryUrl(), credentials);
		} catch (GitException e) {
			throw new ArtemisClientException("Unable to download exercise and submission: " + e.getMessage(), e);
		}

	}

	private void existsAndThrow(File file) throws ArtemisClientException {
		if (file.exists()) {
			throw new ArtemisClientException(
					"Project " + file.getName() + " could not be cloned since the workspace " + "already contains a project with that name. \n"
							+ "Trying to load and merge previously created annotations. Please double-check them before submitting the assessment! \n"
							+ "If you want to start again from skretch, please delete the project and retry.");
		}
	}
}
