/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.mappings;

import java.io.Serial;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExam;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;

public class ArtemisExercise implements IExercise {
	@Serial
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
	// assessmentType == AUTOMATIC it shall not be present in Grading Tool
	@JsonProperty
	private String assessmentType;
	@JsonProperty
	private double maxPoints;
	@JsonProperty
	private Date dueDate = null;
	@JsonProperty
	private Date startDate = null;

	private transient IMappingLoader client;
	private transient ICourse course;
	private transient IExam exam;

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
		return this.secondCorrectionEnabled != null && this.secondCorrectionEnabled;
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
	public double getMaxPoints() {
		return this.maxPoints;
	}

	@Override
	public ICourse getCourse() {
		return this.course;
	}

	public void init(IMappingLoader client, ICourse course, IExam exam) {
		this.client = client;
		this.course = course;
		this.exam = exam;
	}

	public void init(IMappingLoader client, ICourse course) {
		this.init(client, course, null);
	}

	@Override
	public ISubmission getSubmission(int id) throws ArtemisClientException {
		return this.client.getSubmissionById(this, id);
	}

	@Override
	public boolean hasSecondCorrectionRound() {
		if (this.exam == null) {
			return false;
		}
		return this.exam.hasSecondCorrectionRound();
	}

	@Override
	@JsonIgnore
	public boolean isAutomaticAssessment() {
		return "AUTOMATIC".equals(assessmentType);
	}

	@Override
	@JsonIgnore
	public boolean isProgramming() {
		return "programming".equals(type);
	}
}
