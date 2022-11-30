/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.mappings.exam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExam;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.eclipse.common.client.mappings.ArtemisExercise;
import edu.kit.kastel.eclipse.common.client.mappings.IMappingLoader;

public class ArtemisExerciseGroup implements IExerciseGroup, Serializable {
	private static final long serialVersionUID = 1797252671567588724L;

	@JsonProperty(value = "id")
	private int exerciseGroupId;
	@JsonProperty
	private String title;
	@JsonProperty
	private boolean isMandatory;
	@JsonProperty
	private List<ArtemisExercise> exercises;

	/**
	 * For Auto-Deserialization Need to call this::init thereafter!
	 */
	public ArtemisExerciseGroup() {
		// NOP
	}

	@Override
	public List<IExercise> getExercises() {
		return new ArrayList<>(this.exercises);
	}

	public void init(IMappingLoader client, ICourse course, IExam exam) {
		if (this.exercises == null) {
			this.exercises = List.of();
			return;
		}
		this.exercises = this.exercises.stream().filter(exercise -> exercise.getShortName() != null).toList();
		// TODO Check Filter ..
		this.exercises = this.exercises.stream().filter(IExercise::isProgramming).toList();

		for (ArtemisExercise artemisExercise : this.exercises) {
			artemisExercise.init(client, course, Optional.of(exam));
		}
	}
}
