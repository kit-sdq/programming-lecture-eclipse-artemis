package edu.kit.kastel.sdq.eclipse.grading.client.mappings;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
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

	private transient IMappingLoader client;
	private transient ICourse course;
	private transient Optional<IExam> exam;

	/**
	 * For Auto-Deserialization Need to call this::init thereafter!
	 */
	public ArtemisExercise() {
		// NOP
	}

	@Override
	public int getExerciseId() {
		return this.exerciseId;
	}

	@Override
	public boolean isSecondCorrectionEnabled() {
		return this.secondCorrectionEnabled == null ? false : this.secondCorrectionEnabled;
	}

	@Override
	public String getShortName() {
		if (this.shortName == null) {
			return this.title;
		}
		return this.shortName;
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

	public void init(IMappingLoader client, ICourse course, Optional<IExam> exam) {
		this.client = client;
		this.course = course;
		this.exam = exam;
	}

	@Override
	public ISubmission getSubmission(int id) throws ArtemisClientException {
		return this.client.getSubmissionById(this, id);
	}

	@Override
	public boolean hasSecondCorrectionRound() {
		return this.exam.map(IExam::hasSecondCorrectionRound).orElse(false);
	}

}
