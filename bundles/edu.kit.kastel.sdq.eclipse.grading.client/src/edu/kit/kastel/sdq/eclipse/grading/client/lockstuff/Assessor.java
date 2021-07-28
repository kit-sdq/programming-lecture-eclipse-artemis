package edu.kit.kastel.sdq.eclipse.grading.client.lockstuff;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IAssessor;

public class Assessor implements IAssessor {

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

	@JsonCreator
	public Assessor(
			@JsonProperty("id") int id,
			@JsonProperty("login") String login,
			@JsonProperty("firstName") String firstName,
			@JsonProperty("lastName") String lastName,
			@JsonProperty("email") String email,
			@JsonProperty("activated") boolean activated,
			@JsonProperty("langKey") String langKey,
			@JsonProperty("lastNotificationRead") String lastNotificationRead,
			@JsonProperty("name") String name,
			@JsonProperty("participantIdentifier") String participantIdentifier) {
		this.id = id;
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.activated = activated;
		this.langKey = langKey;
		this.lastNotificationRead = lastNotificationRead;
		this.name = name;
		this.participantIdentifier = (participantIdentifier == null) ? this.login : null;
	}

	@Override
	public boolean getActivated() {
		return this.activated;
	}

	@Override
	public String getEmail() {
		return this.email;
	}

	@Override
	public String getFirstName() {
		return this.firstName;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getLangKey() {
		return this.langKey;
	}

	@Override
	public String getLastName() {
		return this.lastName;
	}

	@Override
	public String getLastNotificationRead() {
		return this.lastNotificationRead;
	}

	@Override
	public String getLogin() {
		return this.login;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getParticipantIdentifier() {
		return this.participantIdentifier;
	}

	@Override
	public String toString() {
		return "Assessor [id=" + this.id + ", login=" + this.login + ", firstName=" + this.firstName + ", lastName=" + this.lastName
				+ ", email=" + this.email + ", activated=" + this.activated + ", langKey=" + this.langKey + ", lastNotificationRead="
				+ this.lastNotificationRead + ", name=" + this.name + ", participantIdentifier=" + this.participantIdentifier + "]";
	}


}
