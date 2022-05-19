/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.mappings;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArtemisExerciseWrapper {
	@JsonProperty
	private List<ArtemisExercise> exercises = new ArrayList<>();

	public List<ArtemisExercise> getExercises() {
		return exercises == null ? List.of() : new ArrayList<>(exercises);
	}
}
