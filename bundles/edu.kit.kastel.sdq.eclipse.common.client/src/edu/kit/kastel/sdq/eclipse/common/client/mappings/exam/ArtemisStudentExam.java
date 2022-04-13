/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.client.mappings.exam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.IStudentExam;
import edu.kit.kastel.sdq.eclipse.common.client.mappings.ArtemisExercise;

public class ArtemisStudentExam implements IStudentExam, Serializable {
	private static final long serialVersionUID = 1854716703208552700L;
	@JsonProperty
	private ArtemisExam exam;

	@JsonProperty
	private ArtemisExercise[] exercises;

	@JsonProperty
	private boolean submitted;

	@JsonProperty
	private boolean ended;

	@JsonProperty
	private Boolean started;

	@Override
	public IExam getExam() {
		return this.exam;
	}

	@Override
	public List<IExercise> getExercises() {
		if (this.exercises == null) {
			return new ArrayList<>();
		}
		return Arrays.asList(this.exercises);
	}

	@Override
	public boolean isSubmitted() {
		return this.submitted;
	}

	@Override
	public boolean isEnded() {
		return this.ended;
	}

	@Override
	public boolean isStarted() {
		return Boolean.TRUE.equals(this.started);
	}
}
