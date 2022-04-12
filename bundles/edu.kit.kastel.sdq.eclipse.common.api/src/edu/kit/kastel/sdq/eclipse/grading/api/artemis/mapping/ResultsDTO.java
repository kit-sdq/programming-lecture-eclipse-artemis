/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultsDTO implements Serializable {
	private static final long serialVersionUID = 6637703343535347213L;

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
	public double score;
	@JsonProperty
	public Boolean rated;
	@JsonProperty
	public String resultString;

	public ResultsDTO() {
		// NOP
	}

	public String toLocalDateTimeString() {
		var time = this.completionDate == null ? null : this.completionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		return time == null ? "" : time.format(formatter);
	}
}
