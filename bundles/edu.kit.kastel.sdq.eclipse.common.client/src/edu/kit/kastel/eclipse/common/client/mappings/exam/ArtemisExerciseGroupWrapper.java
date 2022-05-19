/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.mappings.exam;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArtemisExerciseGroupWrapper {
	@JsonProperty
	private List<ArtemisExerciseGroup> exerciseGroups = new ArrayList<>();

	public List<ArtemisExerciseGroup> getExerciseGroups() {
		return exerciseGroups == null ? List.of() : new ArrayList<>(exerciseGroups);
	}
}
