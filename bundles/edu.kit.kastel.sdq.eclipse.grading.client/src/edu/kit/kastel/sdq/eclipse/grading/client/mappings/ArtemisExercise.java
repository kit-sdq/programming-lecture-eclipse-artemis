package edu.kit.kastel.sdq.eclipse.grading.client.mappings;

import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.ISubmission;

public class ArtemisExercise implements IExercise {


	private int exerciseId;
	private String title;
	private String shortName;
	private String testRepositoryUrl;
	private Collection<ISubmission> submissions;
	
	public ArtemisExercise(int exerciseId, String title, String shortName, String testRepositoryUrl, Collection<ISubmission> submissions) {
		this.exerciseId = exerciseId;
		this.title = title;
		this.shortName = shortName;
		this.testRepositoryUrl = testRepositoryUrl;
		this.submissions = submissions;
	}
	
	@Override
	public int getExerciseId() {
		return exerciseId;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getShortName() {
		return shortName;
	}

	@Override
	public String getTestRepositoryUrl() {
		return testRepositoryUrl;
	}

	@Override
	public Collection<ISubmission> getSubmissions() {
		return submissions;
	}

	@Override
	public String toString() {
		return "ArtemisExercise [exerciseId=" + exerciseId + ", title=" + title + ", shortName=" + shortName
				+ ", submissions=" + submissions + "]";
	}

	/**
	 * 
	 * @return a String like {@code toString}, but with fields not contained in IExercise
	 */
	public String toDebugString() {
		return "ArtemisExercise [exerciseId=" + exerciseId + ", title=" + title + ", shortName=" + shortName
				+ ", testRepositoryUrl=" + testRepositoryUrl + ", submissions=" + submissions + "]";
	}
	
	

}
