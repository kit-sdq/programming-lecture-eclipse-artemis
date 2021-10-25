package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Assessor implements Serializable {
	private static final long serialVersionUID = -5600566607034486129L;

	private int id;
	private String login;
	private String firstName;
	private String lastName;
	private String email;
	private boolean activated;
	private String langKey;
	private String lastNotificationRead;
	private String name;
	private String participantIdentifier;

	private List<String> groups;

	@JsonCreator
	public Assessor(@JsonProperty("id") int id, @JsonProperty("login") String login, @JsonProperty("firstName") String firstName,
			@JsonProperty("lastName") String lastName, @JsonProperty("email") String email, @JsonProperty("activated") boolean activated,
			@JsonProperty("langKey") String langKey, @JsonProperty("lastNotificationRead") String lastNotificationRead, @JsonProperty("name") String name,
			@JsonProperty("participantIdentifier") String participantIdentifier, @JsonProperty("groups") List<String> groups) {
		this.id = id;
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.activated = activated;
		this.langKey = langKey;
		this.lastNotificationRead = lastNotificationRead;
		this.name = name;
		this.participantIdentifier = participantIdentifier == null ? this.login : null;
		this.groups = groups;
	}

	public boolean getActivated() {
		return this.activated;
	}

	public String getEmail() {
		return this.email;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public int getId() {
		return this.id;
	}

	public String getLangKey() {
		return this.langKey;
	}

	public String getLastName() {
		return this.lastName;
	}

	public String getLastNotificationRead() {
		return this.lastNotificationRead;
	}

	public String getLogin() {
		return this.login;
	}

	public String getName() {
		return this.name;
	}

	public String getParticipantIdentifier() {
		return this.participantIdentifier;
	}

	public List<String> getGroups() {
		return this.groups;
	}

}
