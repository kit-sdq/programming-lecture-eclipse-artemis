/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.artemis.mapping;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultsDTO implements Serializable {
	@Serial
	private static final long serialVersionUID = 6637703343535347213L;

	@JsonProperty
	public int id;
	@JsonProperty
	public Date completionDate;
	@JsonProperty
	public Feedback[] feedbacks;
	@JsonProperty
	public Boolean successful;
	@JsonProperty
	public double score;
	@JsonProperty
	public Boolean rated;

	public ResultsDTO() {
		// NOP
	}
}
