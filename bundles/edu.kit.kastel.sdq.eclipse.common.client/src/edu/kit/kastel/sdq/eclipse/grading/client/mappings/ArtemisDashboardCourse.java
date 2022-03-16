package edu.kit.kastel.sdq.eclipse.grading.client.mappings;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Assessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam.ArtemisExam;

public class ArtemisDashboardCourse implements ICourse, Serializable {

	private static final long serialVersionUID = 2684752502228538009L;

	@JsonProperty(value = "id")
	private int courseId;

	@JsonProperty
	private String title;

	@JsonProperty
	private String shortName;

	@JsonProperty("instructorGroupName")
	private String instructorGroup;

	@JsonProperty("exercises")
	private ArtemisExercise[] exercises = {};

	@JsonProperty("exams")
	private ArtemisExam[] exams = {};

	private transient IMappingLoader client;

	@Override
	public int getCourseId() {
		return courseId;
	}

	@Override
	public List<IExam> getExams() throws ArtemisClientException {
		if (exams == null) {
			this.exams = (ArtemisExam[]) this.client.getExamsForCourse(this).toArray();
		}
		return Arrays.asList(exams);
	}

	@Override
	public List<IExercise> getExercises() throws ArtemisClientException {
		if (exercises == null) {
			this.exercises = (ArtemisExercise[]) this.client.getStudentExercisesForCourse(this).toArray();
		}
		return Arrays.asList(exercises);
	}

	@Override
	public String getShortName() {
		return shortName;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public boolean isInstructor(Assessor assessor) {
		return false;
	}

	public void init(IMappingLoader client) {
		this.client = client;
	}
}
