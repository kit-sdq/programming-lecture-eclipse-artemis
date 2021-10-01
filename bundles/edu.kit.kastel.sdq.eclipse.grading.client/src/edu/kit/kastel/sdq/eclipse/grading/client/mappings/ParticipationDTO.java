package edu.kit.kastel.sdq.eclipse.grading.client.mappings;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used for deserializing those information into ArtemisSubmission:
 *
 * <li>participantIdentifier
 * <li>participantName
 * <li>repositoryUrl
 *
 */
public class ParticipationDTO {

	@JsonProperty
	private String participantIdentifier;
	@JsonProperty
	private String participantName;
	@JsonProperty
	private String repositoryUrl;

	public ParticipationDTO() {
		// NOP
	}

	public String getParticipantIdentifier() {
		return this.participantIdentifier;
	}

	public String getParticipantName() {
		return this.participantName;
	}

	public String getRepositoryUrl() {
		return this.repositoryUrl;
	}

}
