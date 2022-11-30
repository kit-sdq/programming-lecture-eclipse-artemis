/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.mappings.exam;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExam;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.eclipse.common.client.mappings.IMappingLoader;

public class ArtemisExam implements IExam, Serializable {

	private static final long serialVersionUID = 97898730702942861L;

	@JsonProperty(value = "id")
	private int examId;
	@JsonProperty
	private String title;
	@JsonProperty("numberOfCorrectionRoundsInExam")
	private int numberOfCorrectionRounds;
	@JsonProperty
	private Date startDate;
	@JsonProperty
	private Date endDate;
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
		return this.endDate;
	}

	public int getNumberOfCorrectionRounds() {
		return this.numberOfCorrectionRounds;
	}

	@Override
	public Date getStartDate() {
		return this.startDate;
	}

	public ICourse getCourse() {
		return this.course;
	}
}
