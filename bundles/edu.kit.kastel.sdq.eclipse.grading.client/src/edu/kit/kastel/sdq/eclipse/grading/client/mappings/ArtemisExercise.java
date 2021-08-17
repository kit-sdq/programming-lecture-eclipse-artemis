package edu.kit.kastel.sdq.eclipse.grading.client.mappings;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

public class ArtemisExercise implements IExercise {

	@JsonProperty(value = "id")
	private int exerciseId;
	@JsonProperty
	private String title;
	@JsonProperty
	private String shortName;
	@JsonProperty
	private String testRepositoryUrl;
	private transient Collection<ISubmission> submissions;


	/**
	 * For Auto-Deserialization
	 * Need to call this::init thereafter!
	 */
	public ArtemisExercise() {

	}

	public ArtemisExercise(int exerciseId, String title, String shortName, String testRepositoryUrl, Collection<ISubmission> submissions) {
		this.exerciseId = exerciseId;
		this.title = title;
		this.shortName = shortName;
		this.testRepositoryUrl = testRepositoryUrl;
		this.submissions = submissions;
	}

	@Override
	public int getExerciseId() {
		return this.exerciseId;
	}

	@Override
	public String getShortName() {
		return this.shortName;
	}

	@Override
	public Collection<ISubmission> getSubmissions() {
		return this.submissions;
	}

	@Override
	public String getTestRepositoryUrl() {
		return this.testRepositoryUrl;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	public void init(Collection<ISubmission> submissions) {
		this.submissions = submissions;
	}

	/**
	 *
	 * @return a String like {@code toString}, but with fields not contained in IExercise
	 */
	public String toDebugString() {
		return "ArtemisExercise [exerciseId=" + this.exerciseId + ", title=" + this.title + ", shortName=" + this.shortName
				+ ", testRepositoryUrl=" + this.testRepositoryUrl + ", submissions=" + this.submissions + "]";
	}

	@Override
	public String toString() {
		return "ArtemisExercise [exerciseId=" + this.exerciseId + ", title=" + this.title + ", shortName=" + this.shortName
				+ ", submissions=" + this.submissions + "]";
	}



}
