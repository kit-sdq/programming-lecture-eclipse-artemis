package edu.kit.kastel.sdq.eclipse.grading.client.mappings.lock;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Only used for deserializing LockResult (retrieving participationID). Much like {@link ExerciseDTO}
 */
public class ParticipationDTO {

	private int participationID;
	private double exerciseMaxPoints;

	@JsonCreator
	public ParticipationDTO(@JsonProperty("id") int participationID, @JsonProperty("exercise") ExerciseDTO exerciseDummy) {
		this.participationID = participationID;
		this.exerciseMaxPoints = exerciseDummy.getMaxPoints();
	}

	public double getExerciseMaxPoints() {
		return this.exerciseMaxPoints;
	}

	public int getParticipationID() {
		return this.participationID;
	}
}
