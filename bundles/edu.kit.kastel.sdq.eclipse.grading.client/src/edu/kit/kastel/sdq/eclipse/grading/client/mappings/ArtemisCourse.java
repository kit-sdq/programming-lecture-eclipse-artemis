package edu.kit.kastel.sdq.eclipse.grading.client.mappings;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Assessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;

public class ArtemisCourse implements ICourse, Serializable {
	private static final long serialVersionUID = -2658127210041804941L;

	@JsonProperty(value = "id")
	private int courseId;
	@JsonProperty
	private String title;
	@JsonProperty
	private String shortName;

	@JsonProperty("instructorGroupName")
	private String instructorGroup;

	private transient List<IExercise> exercises;
	private transient List<IExam> exams;
	private transient IMappingLoader client;

	/**
	 * For Auto-Deserialization Need to call this::init thereafter!
	 */
	public ArtemisCourse() {
		// NOP
	}

	@Override
	public int getCourseId() {
		return this.courseId;
	}

	@Override
	public List<IExam> getExams() throws ArtemisClientException {
		if (this.exams == null) {
			this.exams = this.client.getExamsForCourse(this);
			this.exams.sort((e1, e2) -> e1.getTitle().compareTo(e2.getTitle()));
		}
		return this.exams;
	}

	@Override
	public List<IExercise> getExercises() throws ArtemisClientException {
		if (this.exercises == null) {
			this.exercises = this.client.getGradingExercisesForCourse(this);
			this.exercises.sort((e1, e2) -> e1.getShortName().compareTo(e2.getShortName()));
		}
		return this.exercises;
	}

	@Override
	public String getShortName() {
		return this.shortName;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public boolean isInstructor(Assessor assessor) {
		return (assessor != null) && assessor.getGroups().contains(this.instructorGroup);
	}

	public void init(IMappingLoader client) {
		this.client = client;
	}

}
