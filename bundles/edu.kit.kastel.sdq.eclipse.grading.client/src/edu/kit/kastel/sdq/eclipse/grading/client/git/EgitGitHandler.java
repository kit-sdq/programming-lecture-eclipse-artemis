package edu.kit.kastel.sdq.eclipse.grading.client.git;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.core.EclipseGitProgressTransformer;
import org.eclipse.egit.core.op.CloneOperation;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FileUtils;

public class EgitGitHandler extends AbstractGitHandler {

	private static final String remoteName = "origin";
	
	public EgitGitHandler(String repoURL) {
		super(repoURL);
	}

	@Override
	public void cloneRepo(final File destination, final String branch) {
		// TODO this currently clones all branches. Sparse checkout might be better?!
		try {
			System.out.println("Cloning repo " + getRepoURL() + " to " + destination);
			
			CloneOperation cloneOperation = new CloneOperation(getURIish(), true, null, destination, branch, remoteName, 0);
			
			cloneOperation.run(new DumbProgressMonitor());
		} catch (Exception e) {
			//TODO rethrow
			System.out.println("[EgitGitHandler - Erreur] Clone failed with exception [" + e.getClass() + "]:"+ e.getMessage());
			e.printStackTrace();
			return;
		}
		System.out.println("[EgitGitHandler] Clone ran through! ");
	}

	@Override
	public void checkout(String branch, boolean create) {
		// TODO Auto-generated method stub

	}
	
	private URIish getURIish() throws URISyntaxException {
		return new URIish(getRepoURL());
	}

}
