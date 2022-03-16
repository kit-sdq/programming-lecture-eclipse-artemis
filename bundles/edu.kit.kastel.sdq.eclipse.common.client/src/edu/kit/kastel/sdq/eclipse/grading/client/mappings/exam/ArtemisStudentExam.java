package edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IStudentExam;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisExercise;

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

	@Override
	public IExam getExam() {
		return exam;
	}

	@Override
	public List<IExercise> getExercises() {
		if (exercises == null) {
			return new ArrayList<>();
		}
		return Arrays.asList(exercises);
	}

	@Override
	public boolean isSubmitted() {
		return submitted;
	}

	@Override
	public boolean isEnded() {
		return ended;
	}

}
