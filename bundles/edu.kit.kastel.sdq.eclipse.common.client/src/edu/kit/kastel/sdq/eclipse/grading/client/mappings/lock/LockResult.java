/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.client.mappings.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;

public class LockResult implements ILockResult {
	private static final long serialVersionUID = -3787474578751131899L;

	private int submissionId;
	private List<Feedback> latestFeedback;
	private ParticipationDTO participation;

	@JsonCreator
	public LockResult(@JsonProperty("id") int submissionID, @JsonProperty("results") List<LockCallAssessmentResult> previousAssessmentresults,
			@JsonProperty("participation") ParticipationDTO participation) {
		this.submissionId = submissionID;
		this.participation = participation;

		this.latestFeedback = new ArrayList<>();
		LockCallAssessmentResult latestResult = previousAssessmentresults.isEmpty() //
				? null //
				: previousAssessmentresults.get(previousAssessmentresults.size() - 1);

		if (latestResult != null) {
			latestResult.getFeedbacks().stream().filter(Objects::nonNull).forEach(this.latestFeedback::add);
		}
	}

	@Override
	public ParticipationDTO getParticipation() {
		return this.participation;
	}

	@Override
	public List<Feedback> getLatestFeedback() {
		return this.latestFeedback;
	}

	@Override
	public int getSubmissionId() {
		return this.submissionId;
	}

}
