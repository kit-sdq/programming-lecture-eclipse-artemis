package edu.kit.kastel.sdq.eclipse.grading.client.git;

import java.io.File;

/**
 * Performs Git Operations
 *
 */
public abstract class AbstractGitHandler {

	private String repoURL;

	protected AbstractGitHandler(String repoURL) {
		this.repoURL = repoURL;
	}

	/**
	 * Clone this Handlers repo to a given destination on the file system, checking out a given branch.
	 * @param destination
	 * @param branch
	 * @throws GitException
	 */
	public abstract void cloneRepo(final File destination, final String branch) throws GitException;

	public String getRepoURL() {
		return this.repoURL;
	}

}
