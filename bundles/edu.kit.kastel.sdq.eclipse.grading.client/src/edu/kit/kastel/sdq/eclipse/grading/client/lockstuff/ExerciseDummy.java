package edu.kit.kastel.sdq.eclipse.grading.client.lockstuff;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExerciseDummy {

	private double maxPoints;

	@JsonCreator
	public ExerciseDummy(@JsonProperty("maxPoints") double maxPoints) {
		this.maxPoints = maxPoints;
	}

	public double getMaxPoints() {
		return this.maxPoints;
	}
}
