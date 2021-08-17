package edu.kit.kastel.sdq.eclipse.grading.client.mappings;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used for deserializing those information into ArtemisSubmission:
 *
 * <li> hasSubmittedAssessment
 * <li> hasSavedAssessment;
 *
 */
public class ResultsDummy {


	@JsonProperty
	public String completionDate;
	@JsonProperty
	public Boolean hasFeedback;

	public ResultsDummy() { }
}
