/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;

import org.eclipse.jgit.api.TransportCommand;

import org.eclipse.jgit.lib.Repository;

import org.eclipse.jgit.transport.TransportHttp;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FileUtils;

import edu.kit.kastel.eclipse.common.api.messages.Messages;

public final class GitHandler {

	private static final String REMOTE_NAME = "origin";

	public static void cloneRepo(final File destination, String repoURL, GitCredentials credentials) throws GitException {
		Repository repository = null;
		try {
			CloneCommand cloneRepository = Git.cloneRepository();
			cloneRepository.setDirectory(destination);
			cloneRepository.setRemote(REMOTE_NAME);
			cloneRepository.setURI(String.valueOf(new URIish(repoURL)));
			cloneRepository.setCloneAllBranches(true);
			cloneRepository.setCloneSubmodules(false);
			makeAuth(cloneRepository, credentials);

			Git git = cloneRepository.call();
			repository = git.getRepository();
		} catch (final Exception e) {
			try {
				FileUtils.delete(destination, FileUtils.RECURSIVE);
			} catch (IOException ioe) {
				// NOP
			}
			throw new GitException(Messages.GIT_CLONE_FAILED + e.getMessage(), e);
		} finally {
			if (repository != null) {
				repository.close();
			}
		}
	}

	private static void makeAuth(TransportCommand<?, ?> command, GitCredentials credentials) {
		if (credentials == null)
			return;
		command.setCredentialsProvider(credentials.toCredentialsProvider());
		command.setTransportConfigCallback(transport -> {
			if (!(transport instanceof TransportHttp httpTransport))
				return;
			httpTransport.setPreemptiveBasicAuthentication(credentials.username(), credentials.password());
		});
	}

	private GitHandler() {
		throw new IllegalAccessError();
	}

}
