package edu.kit.kastel.sdq.eclipse.grading.client.mappings;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

public class ArtemisExercise implements IExercise, Serializable {
	private static final long serialVersionUID = 5892461865571113106L;

	@JsonProperty(value = "id")
	private int exerciseId;
	@JsonProperty
	private String title;
	@JsonProperty
	private String shortName;
	@JsonProperty
	private String testRepositoryUrl;
	@JsonProperty
	private Boolean secondCorrectionEnabled;
	@JsonProperty
	private String type;
	@JsonProperty
	private double maxPoints;

	private transient List<ISubmission> submissions;
	private transient IMappingLoader client;

	private transient ICourse course;

	/**
	 * For Auto-Deserialization Need to call this::init thereafter!
	 */
	public ArtemisExercise() {

	}

	public ArtemisExercise(int exerciseId, String title, String shortName, String testRepositoryUrl, List<ISubmission> submissions) {
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
	public Boolean getSecondCorrectionEnabled() {
		return this.secondCorrectionEnabled;
	}

	@Override
	public String getShortName() {
		if (this.shortName == null) {
			return this.title;
		}
		return this.shortName;
	}

	@Override
	public List<ISubmission> getSubmissions() throws ArtemisClientException {
		if (this.submissions == null) {
			this.submissions = this.client.getSubmissionsForExercise(this);
		}
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

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public double getMaxPoints() {
		return this.maxPoints;
	}

	@Override
	public ICourse getCourse() {
		return this.course;
	}

	public void init(IMappingLoader client, ICourse course) {
		this.client = client;
		this.course = course;
	}

}
