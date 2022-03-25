package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultsDTO implements Serializable {
	private static final long serialVersionUID = 6637703343535347213L;

	@JsonProperty
	public int id;
	@JsonProperty
	public Date completionDate;
	@JsonProperty
	public Boolean hasFeedback;
	@JsonProperty
	public Feedback[] feedbacks;
	@JsonProperty
	public Boolean successful;
	@JsonProperty
	public int score;
	@JsonProperty
	public Boolean rated;
	@JsonProperty
	public String resultString;

	public ResultsDTO() {
		// NOP
	}
}
