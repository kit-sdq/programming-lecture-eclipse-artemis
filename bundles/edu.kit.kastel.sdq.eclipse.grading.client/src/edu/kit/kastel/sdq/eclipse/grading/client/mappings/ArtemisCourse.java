package edu.kit.kastel.sdq.eclipse.grading.client.mappings;

import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;

public class ArtemisCourse implements ICourse {

	private int courseId;
	private String title;
	private String shortName;
	private Collection<IExercise> exercises;
	private Collection<IExam> exams;

	public ArtemisCourse(int courseId, String title, String shortName, Collection<IExercise> exercises, Collection<IExam> exams) {
		this.courseId = courseId;
		this.title = title;
		this.shortName = shortName;
		this.exercises = exercises;
		this.exams = exams;
	}

	@Override
	public int getCourseId() {
		return this.courseId;
	}

	@Override
	public Collection<IExam> getExams() {
		return this.exams;
	}

	@Override
	public Collection<IExercise> getExercises() {
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
	public String toString() {
		return "ArtemisCourse [courseId=" + this.courseId + ", title=" + this.title + ", shortName=" + this.shortName + ", exercises="
				+ this.exercises + ", exams=" + this.exams + "]";
	}
}
