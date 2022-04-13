/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.client.mappings.lock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping.Feedback;

/**
 * Used for deserializing assessmentResults in lock calls into feedbacks.
 */
public class LockCallAssessmentResult {

	private List<Feedback> feedbacks;

	public LockCallAssessmentResult(@JsonProperty("feedbacks") Feedback[] feedbacks) {
		if (feedbacks != null) {
			this.feedbacks = Arrays.asList(feedbacks);
		}
	}

	public List<Feedback> getFeedbacks() {
		if (this.feedbacks != null) {
			return new ArrayList<>(this.feedbacks);
		}
		return List.of();
	}
}
