package edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam;

import java.util.Collection;

import javax.security.sasl.AuthenticationException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.client.rest.ArtemisRESTClient;

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

	public void init(ArtemisRESTClient artemisRESTClient, int courseID) throws AuthenticationException, JsonProcessingException, ArtemisClientException {
		this.exerciseGroups = artemisRESTClient.getExerciseGroupsForExam(this, courseID);
	}

	@Override
	public String toString() {
		return "ArtemisExam [exerciseGroups=" + this.exerciseGroups + ", examId=" + this.examId + ", title=" + this.title + "]";
	}
}
