/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.rest;

import java.io.Serializable;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.User;
import edu.kit.kastel.eclipse.common.api.client.IAuthenticationArtemisClient;
import edu.kit.kastel.eclipse.common.client.BrowserLogin;

public class LoginManager extends AbstractArtemisClient implements IAuthenticationArtemisClient {
	private String username;
	private String password;
	private String token;
	private WebTarget endpoint;
	private User assessor;

	public LoginManager(String hostname, String token) {
		super(hostname);
		this.token = token;
		this.endpoint = this.getEndpoint(this.getApiRootURL());
	}

	public LoginManager(String hostname, String username, String password) {
		super(hostname);
		this.username = username;
		this.password = password;
		this.endpoint = this.getEndpoint(this.getApiRootURL());
	}

	@Override
	public String getArtemisUrl() {
		return this.getRootURL();
	}

	@Override
	public void login() throws ArtemisClientException {
		try {

			if (this.hostname.isBlank()) {
				throw new ArtemisClientException("Login without hostname is impossible");
			} else if (username.isBlank() || password.isBlank()) {
				BrowserLogin login = new BrowserLogin(getRootURL());
				this.token = login.getToken();
			} else {
				this.token = this.loginViaUsernameAndPassword();
			}
			this.assessor = this.fetchAssessor();
		} catch (ProcessingException e) {
			throw new ArtemisClientException(e.getMessage(), e);
		}
	}

	@Override
	public boolean isLoggedIn() {
		return this.token != null;
	}

	@Override
	public String getRawToken() {
		return this.token;
	}

	@Override
	public String getBearerToken() {
		return this.token;
	}

	@Override
	public User getUser() {
		return this.assessor;
	}

	private User fetchAssessor() throws ArtemisClientException {
		final Response rsp = this.endpoint.path("account").request().cookie(getAuthCookie(this.getBearerToken())).buildGet().invoke();
		this.throwIfStatusUnsuccessful(rsp);
		return this.read(rsp.readEntity(String.class), User.class);
	}

	private String loginViaUsernameAndPassword() throws ArtemisClientException, ProcessingException {
		String payload = this.payload(this.getAuthenticationEntity());
		final Response authenticationResponse = this.endpoint.path("authenticate").request().buildPost(Entity.json(payload)).invoke();

		this.throwIfStatusUnsuccessful(authenticationResponse);
		final String authRspEntity = authenticationResponse.readEntity(String.class);
		return this.read(authRspEntity, Token.class).token;
	}

	private final AuthenticationEntity getAuthenticationEntity() {
		AuthenticationEntity entity = new AuthenticationEntity();
		entity.username = this.username;
		entity.password = this.password;
		return entity;
	}

	private static final class AuthenticationEntity implements Serializable {
		private static final long serialVersionUID = -6291795795865534155L;
		@JsonProperty
		private String username;
		@JsonProperty
		private String password;
		@JsonProperty
		private boolean rememberMe = true;
	}

	private static final class Token implements Serializable {
		private static final long serialVersionUID = -3729961485516556014L;
		@JsonProperty("id_token")
		private String token;
	}
}
