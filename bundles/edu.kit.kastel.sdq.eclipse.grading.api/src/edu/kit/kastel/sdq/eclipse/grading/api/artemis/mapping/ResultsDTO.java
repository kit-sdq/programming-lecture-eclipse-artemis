package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultsDTO implements Serializable {
	private static final long serialVersionUID = 6637703343535347213L;

	@JsonProperty
	public String completionDate;
	@JsonProperty
	public Boolean hasFeedback;

	public ResultsDTO() {
		// NOP
	}
}
