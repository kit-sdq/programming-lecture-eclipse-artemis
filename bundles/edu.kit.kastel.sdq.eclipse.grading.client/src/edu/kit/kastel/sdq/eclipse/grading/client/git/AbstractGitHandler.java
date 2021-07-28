package edu.kit.kastel.sdq.eclipse.grading.client.git;

import java.io.File;

public abstract class AbstractGitHandler {

	private String repoURL;

	protected AbstractGitHandler(String repoURL) {
		this.repoURL = repoURL;
	}

	public abstract void cloneRepo(final File destination, final String branch) throws GitException;

	public String getRepoURL() {
		return this.repoURL;
	}

}
