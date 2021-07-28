package edu.kit.kastel.sdq.eclipse.grading.client.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.util.FileUtils;

public class JGitGitHandler extends AbstractGitHandler {

	public JGitGitHandler(String repoURL) {
		super(repoURL);
	}

	@Override
	public void cloneRepo(File destination, String branch) {
		// TODO Auto-generated method stub
		try {
			FileUtils.mkdirs(destination, true);
			CloneCommand cloneCommand = Git.cloneRepository()
				.setDirectory(destination.getCanonicalFile())
				.setURI(getRepoURL());
			Git git = cloneCommand.call();
		} catch (IOException | GitAPIException e) {
			System.out.println("[Erreur] Clone failed with exception [" + e.getClass() + "] :"+ e.getMessage());
			return;
		} 
		System.out.println("[JgitGitHandler] Clone ran through! ");

	}

	@Override
	public void checkout(String branch, boolean create) {
		// TODO Auto-generated method stub

	}

}
