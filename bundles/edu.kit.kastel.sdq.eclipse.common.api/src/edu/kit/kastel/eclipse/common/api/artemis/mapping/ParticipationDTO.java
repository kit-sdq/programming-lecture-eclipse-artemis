/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.artemis.mapping;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParticipationDTO implements Serializable {
	private static final long serialVersionUID = -9151262219739630658L;

	@JsonProperty("id")
	private int participationId;
	@JsonProperty
	private String participantIdentifier;
	@JsonProperty
	private String participantName;
	@JsonProperty
	private String repositoryUrl;
	@JsonProperty
	private ResultsDTO[] results;

	public ParticipationDTO() {
		// NOP
	}

	public int getParticipationId() {
		return this.participationId;
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

	public ResultsDTO[] getResults() {
		return this.results;
	}
}