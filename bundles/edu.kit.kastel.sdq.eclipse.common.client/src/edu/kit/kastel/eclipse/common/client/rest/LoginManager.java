/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.rest;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.User;
import edu.kit.kastel.eclipse.common.api.client.IAuthenticationArtemisClient;
import edu.kit.kastel.eclipse.common.client.BrowserLogin;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginManager extends AbstractArtemisClient implements IAuthenticationArtemisClient {
	private String username;
	private String password;
	private String token;

	private final OkHttpClient client;
	private User assessor;

	public LoginManager(String hostname, String username, String password) {
		super(hostname);
		this.username = username;
		this.password = password;
		// Create without token ..
		this.client = this.createClient(null);
	}

	@Override
	public String getArtemisUrl() {
		return this.getRootURL();
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
		if (this.token == null) {
			return null;
		}
		OkHttpClient clientWithToken = this.createClient(this.token);
		Request request = new Request.Builder().url(this.path("account")).get().build();
		return this.call(clientWithToken, request, User.class);
	}

	private String loginViaUsernameAndPassword() throws ArtemisClientException {
		String payload = this.payload(this.getAuthenticationEntity());

		Request request = new Request.Builder() //
				.url(this.path("authenticate")).post(RequestBody.create(payload, JSON)).build();

		try (Response response = this.client.newCall(request).execute()) {
			this.throwIfStatusUnsuccessful(response);
			// jwt=JWT_CONTENT_HERE; Path=/; Max-Age=2592000; Expires=Sun, 26 Feb 2023
			// 23:56:30 GMT; Secure; HttpOnly; SameSite=Lax
			var cookieHeader = response.headers().get("set-cookie");
			if (cookieHeader != null && cookieHeader.startsWith(COOKIE_NAME_JWT)) {
				return cookieHeader.split(";", 2)[0].trim().substring((COOKIE_NAME_JWT + "=").length());
			}
			throw new ArtemisClientException("Authentication was not successful. Cookie not received!");
		} catch (IOException e) {
			throw new ArtemisClientException(e.getMessage(), e);
		}
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
}
