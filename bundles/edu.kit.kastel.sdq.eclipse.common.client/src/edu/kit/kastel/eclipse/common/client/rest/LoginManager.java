/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.rest;

import java.io.Serializable;
import java.util.function.Consumer;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.User;
import edu.kit.kastel.eclipse.common.api.client.IAuthenticationArtemisClient;

public class LoginManager extends AbstractArtemisClient implements IAuthenticationArtemisClient {

	private static final ILog log = Platform.getLog(LoginManager.class);

	private String username;
	private String jwtToken;

	private Consumer<String> newTokenCallback;
	private transient String initialPasswordOrToken;

	private WebTarget endpoint;
	private User assessor;

	public LoginManager(String hostname, String username, String passwordOrToken, Consumer<String> newTokenCallback) {
		super(hostname);
		this.username = username;
		this.initialPasswordOrToken = passwordOrToken;
		this.newTokenCallback = newTokenCallback;

		this.endpoint = this.getEndpoint(this.getApiRootURL());
	}

	@Override
	public String getArtemisUrl() {
		return this.getRootURL();
	}

	@Override
	public void init() throws ArtemisClientException {
		if (jwtToken != null) {
			return;
		}

		try {
			if (JWTTokenUtils.isJWTToken(this.initialPasswordOrToken)) {
				this.jwtToken = this.initialPasswordOrToken;
			} else {
				this.jwtToken = login();
			}
			updateTokenIfPossible();

			this.assessor = this.fetchAssessor();
		} catch (ProcessingException e) {
			throw new ArtemisClientException(e.getMessage(), e);
		}
	}

	private void updateTokenIfPossible() throws ArtemisClientException {
		final Response rsp = this.endpoint.path("personal-access-token").request().header(AUTHORIZATION_NAME, this.getBearerToken())
				.buildPost(Entity.json(null)).invoke();
		if (!this.isStatusSuccessful(rsp)) {
			log.error("Cannot update JWT Token. Please check that Artemis PAT profile is active!");
			return;
		}
		Token token = this.read(rsp.readEntity(String.class), Token.class);
		this.jwtToken = token.token;
		newTokenCallback.accept(token.token);
	}

	@Override
	public String getRawToken() {
		return this.jwtToken;
	}

	@Override
	public String getBearerToken() {
		return "Bearer " + this.jwtToken;
	}

	@Override
	public User getUser() {
		return this.assessor;
	}

	private User fetchAssessor() throws ArtemisClientException {
		final Response rsp = this.endpoint.path("account").request().header(AUTHORIZATION_NAME, this.getBearerToken()).buildGet().invoke();
		this.throwIfStatusUnsuccessful(rsp);
		return this.read(rsp.readEntity(String.class), User.class);
	}

	private String login() throws ArtemisClientException, ProcessingException {
		String payload = this.payload(this.getAuthenticationEntity());
		final Response authenticationResponse = this.endpoint.path("authenticate").request().buildPost(Entity.json(payload)).invoke();

		this.throwIfStatusUnsuccessful(authenticationResponse);
		final String authRspEntity = authenticationResponse.readEntity(String.class);
		return this.read(authRspEntity, Token.class).token;
	}

	private final AuthenticationEntity getAuthenticationEntity() {
		AuthenticationEntity entity = new AuthenticationEntity();
		entity.username = this.username;
		entity.password = this.initialPasswordOrToken;
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
