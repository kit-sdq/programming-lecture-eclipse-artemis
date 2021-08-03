package edu.kit.kastel.sdq.eclipse.grading.client.git;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.eclipse.egit.core.op.CloneOperation;
import org.eclipse.jgit.transport.URIish;

public class EgitGitHandler extends AbstractGitHandler {

	private static final String REMOTE_NAME = "origin";

	public EgitGitHandler(String repoURL) {
		super(repoURL);
	}

	//TODO static evtl
	@Override
	public void cloneRepo(final File destination, final String branch) throws GitException {
		// TODO this currently clones all branches. Sparse checkout might be better?!
		try {
			new CloneOperation(this.getURIish(), true, null, destination, branch, REMOTE_NAME, 0).run(new DumbProgressMonitor());
		} catch (InvocationTargetException | InterruptedException | URISyntaxException e) {
			throw new GitException("Git clone failed with exception [" + e.getClass() + "]:"+ e.getMessage(), e);
		}
	}

	private URIish getURIish() throws URISyntaxException {
		return new URIish(this.getRepoURL());
	}

}
