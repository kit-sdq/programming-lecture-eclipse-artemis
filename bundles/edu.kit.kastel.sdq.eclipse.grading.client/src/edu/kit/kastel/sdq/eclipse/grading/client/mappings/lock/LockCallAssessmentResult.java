package edu.kit.kastel.sdq.eclipse.grading.client.mappings.lock;

import java.util.Arrays;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IFeedback;

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

	public List<IFeedback> getFeedbacks() {
		if (this.feedbacks != null) {
			return this.feedbacks.stream()
					.map(IFeedback.class::cast)
					.collect(Collectors.toList());
		}
		return List.of();
	}
}
