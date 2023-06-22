/* Licensed under EPL-2.0 2023. */
package edu.kit.kastel.eclipse.common.api.artemis.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LongFeedbackText {
	@JsonProperty
	private int id;
	@JsonProperty
	private String text;

	public String getText() {
		return text;
	}
}
