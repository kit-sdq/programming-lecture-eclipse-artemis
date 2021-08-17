package edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExerciseGroup;

public class ArtemisExam implements IExam {

	private transient Collection<IExerciseGroup> exerciseGroups;
	@JsonProperty(value = "id")
	private int examId;
	@JsonProperty
	private String title;

	/**
	 * For Auto-Deserialization
	 * Need to call this::init thereafter!
	 */
	public ArtemisExam() { }

	public ArtemisExam(Collection<IExerciseGroup> exerciseGroups, int examId,  String title) {
		this.exerciseGroups = exerciseGroups;
		this.examId = examId;
		this.title = title;
	}

	@Override
	public int getExamId() {
		return this.examId;
	}

	@Override
	public Collection<IExerciseGroup> getExerciseGroups() {
		return this.exerciseGroups;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	public void init(Collection<IExerciseGroup> exerciseGroups) {
		this.exerciseGroups = exerciseGroups;
	}

	@Override
	public String toString() {
		return "ArtemisExam [exerciseGroups=" + this.exerciseGroups + ", examId=" + this.examId + ", title=" + this.title + "]";
	}
}
