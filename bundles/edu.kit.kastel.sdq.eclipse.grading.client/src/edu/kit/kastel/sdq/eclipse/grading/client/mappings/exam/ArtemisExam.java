package edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.IMappingLoader;

public class ArtemisExam implements IExam, Serializable {

	private static final long serialVersionUID = 97898730702942861L;

	@JsonProperty(value = "id")
	private int examId;
	@JsonProperty
	private String title;

	private transient ICourse course;
	private transient List<IExerciseGroup> exerciseGroups;
	private transient IMappingLoader client;

	/**
	 * For Auto-Deserialization Need to call this::init thereafter!
	 */
	public ArtemisExam() {
	}

	public ArtemisExam(List<IExerciseGroup> exerciseGroups, int examId, String title) {
		this.exerciseGroups = exerciseGroups;
		this.examId = examId;
		this.title = title;
	}

	@Override
	public int getExamId() {
		return this.examId;
	}

	@Override
	public List<IExerciseGroup> getExerciseGroups() throws ArtemisClientException {
		if (this.exerciseGroups == null) {
			this.exerciseGroups = this.client.getExerciseGroupsForExam(this, this.course);
		}
		return this.exerciseGroups;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	public void init(IMappingLoader client, ICourse course) {
		this.course = course;
		this.client = client;
	}

}
