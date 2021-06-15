package edu.kit.kastel.sdq.eclipse.grading.client.mappings;

import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.IExercise;

public class ArtemisCourse implements ICourse {

	private int courseId;
	private String title;
	private String shortName;
	private Collection<IExercise> exercises;
	
	public ArtemisCourse(int courseId, String title, String shortName, Collection<IExercise> exercises) {
		this.courseId = courseId;
		this.title = title;
		this.shortName = shortName;
		this.exercises = exercises;
	}
	
	@Override
	public int getCourseId() {
		// TODO Auto-generated method stub
		return courseId;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return title;
	}

	@Override
	public String getShortName() {
		// TODO Auto-generated method stub
		return shortName;
	}

	@Override
	public Collection<IExercise> getExercises() {
		// TODO Auto-generated method stub
		return exercises;
	}

	@Override
	public String toString() {
		return "ArtemisCourse [courseId=" + courseId + ", title=" + title + ", shortName=" + shortName + ", exercises="
				+ exercises + "]";
	}
	

}
