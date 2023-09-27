/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.rest;

import edu.kit.kastel.eclipse.common.client.BrowserLogin;
import edu.kit.kastel.sdq.artemis4j.api.ArtemisClientException;

public class LoginManager extends edu.kit.kastel.sdq.artemis4j.client.LoginManager {

	public LoginManager(String hostname, String username, String password) {
		super(hostname, username, password);
	}

	@Override
	public void login() throws ArtemisClientException {
		if (this.hostname.isBlank()) {
			throw new ArtemisClientException("Login without hostname is impossible");
		} else if (this.username.isBlank() || this.password.isBlank()) {
			BrowserLogin login = new BrowserLogin(this.getRootURL());
			this.token = login.getToken();
		} else {
			this.token = this.loginViaUsernameAndPassword();
		}

		this.assessor = this.fetchAssessor();
	}
}
