package edu.kit.kastel.sdq.eclipse.grading.client.lockstuff;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Only used for deserializing LockResult (retrieving participationID).
 * @author Robin
 *
 */
public class ParticipationDummy {

	private int participationID;

	@JsonCreator
	public ParticipationDummy(@JsonProperty("id") int participationID) {
		this.participationID = participationID;
	}

	public int getParticipationID() {
		return this.participationID;
	}
}
