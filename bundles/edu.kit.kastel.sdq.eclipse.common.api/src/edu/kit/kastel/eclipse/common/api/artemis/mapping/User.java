/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.artemis.mapping;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class User implements Serializable {
	@Serial
	private static final long serialVersionUID = -5600566607034486129L;

	@JsonProperty("id")
	private int id;
	@JsonProperty("login")
	private String login;
	@JsonProperty("firstName")
	private String firstName;
	@JsonProperty("lastName")
	private String lastName;
	@JsonProperty("email")
	private String email;
	@JsonProperty("activated")
	private boolean activated;
	@JsonProperty("langKey")
	private String langKey;
	@JsonProperty("lastNotificationRead")
	private String lastNotificationRead;
	@JsonProperty("name")
	private String name;
	@JsonProperty("participantIdentifier")
	private String participantIdentifier;
	@JsonProperty("groups")
	private List<String> groups;
	@JsonProperty("vcsAccessToken")
	private String vcsAccessToken;

	public String getEmail() {
		return this.email;
	}

	public int getId() {
		return this.id;
	}

	public String getLogin() {
		return this.login;
	}

	public String getName() {
		return this.name;
	}

	public List<String> getGroups() {
		return this.groups;
	}

	public String getVcsAccessToken() {
		return vcsAccessToken;
	}

}
