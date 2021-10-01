package edu.kit.kastel.sdq.eclipse.grading.client.mappings.lock;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IParticipation;

/**
 * Only used for deserializing LockResult (retrieving participationID).
 */
public class ParticipationDTO implements IParticipation {

	private int participationID;

	@JsonCreator
	public ParticipationDTO(@JsonProperty("id") int participationID) {
		this.participationID = participationID;
	}

	@Override
	public int getParticipationID() {
		return this.participationID;
	}
}
