/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.mappings.lock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.eclipse.common.api.artemis.mapping.Feedback;

/**
 * Used for deserializing assessmentResults in lock calls into feedbacks.
 */
public class LockCallAssessmentResult {

	private int id;
	private List<Feedback> feedbacks;

	public LockCallAssessmentResult(@JsonProperty("id") int id, @JsonProperty("feedbacks") Feedback[] feedbacks) {
		this.id = id;
		if (feedbacks != null) {
			this.feedbacks = Arrays.asList(feedbacks);
		}
	}

	public int getId() {
		return id;
	}

	public List<Feedback> getFeedbacks() {
		if (this.feedbacks != null) {
			return new ArrayList<>(this.feedbacks);
		}
		return List.of();
	}
}
