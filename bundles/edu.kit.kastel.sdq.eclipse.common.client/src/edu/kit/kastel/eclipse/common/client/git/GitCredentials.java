/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.git;

import java.util.Objects;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public record GitCredentials(String username, String password) {

	public GitCredentials(String username, String password) {
		this.username = Objects.requireNonNull(username);
		this.password = Objects.requireNonNull(password);
	}

	public CredentialsProvider toCredentialsProvider() {
		return new UsernamePasswordCredentialsProvider(username, password);
	}

}
