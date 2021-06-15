package edu.kit.kastel.sdq.eclipse.grading.client.git;

import java.io.File;

public abstract class AbstractGitHandler {
	
	private String repoURL;
	
	public AbstractGitHandler(String repoURL) {
		this.repoURL = repoURL;
	}
	
	public abstract void cloneRepo(final File destination, final String branch);
	
	public abstract void checkout(final String branch, final boolean create);
	
	public String getRepoURL() {
		return repoURL;
	}
	
}
