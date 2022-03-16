package edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	@JsonProperty
	private int numberOfCorrectionRounds;
	@JsonProperty
	private Date startDate;
	@JsonProperty
	private Date endDate;
	@JsonProperty
	private boolean started;
	@JsonIgnore
	private transient ICourse course;
	@JsonIgnore
	private transient List<IExerciseGroup> exerciseGroups;
	@JsonIgnore
	private transient IMappingLoader client;

	/**
	 * For Auto-Deserialization Need to call this::init thereafter!
	 */
	public ArtemisExam() {
		// NOP
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

	@Override
	public boolean hasSecondCorrectionRound() {
		return this.numberOfCorrectionRounds >= 2;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	public int getNumberOfCorrectionRounds() {
		return numberOfCorrectionRounds;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	public ICourse getCourse() {
		return course;
	}

	@Override
	public boolean isExamExpired(Date currentDate) {
		boolean result = false;
		if (startDate != null) {
			result = startDate.after(currentDate);
		}
		if (endDate != null) {
			result = endDate.before(currentDate);
		}
		return result;
	}
}
