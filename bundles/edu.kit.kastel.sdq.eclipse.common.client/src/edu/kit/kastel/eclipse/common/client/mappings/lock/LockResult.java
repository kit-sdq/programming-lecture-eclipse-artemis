/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.mappings.lock;

import edu.kit.kastel.eclipse.common.api.artemis.ILockResult;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.Feedback;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ParticipationDTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LockResult implements ILockResult {
	@Serial
	private static final long serialVersionUID = -3787474578751131899L;

	private final int submissionId;
	private final int participationId;
	private final List<Feedback> latestFeedback;

	@JsonCreator
	public LockResult( //
			@JsonProperty("id") int submissionId, //
			@JsonProperty("results") List<LockCallAssessmentResult> previousAssessmentResults, //
			@JsonProperty("participation") ParticipationDTO participation) {

		this.submissionId = submissionId;
		this.participationId = participation.getParticipationId();

		this.latestFeedback = new ArrayList<>();
		LockCallAssessmentResult latestResult = previousAssessmentResults.isEmpty() //
				? null //
				: previousAssessmentResults.get(previousAssessmentResults.size() - 1);

		if (latestResult != null) {
			latestResult.getFeedbacks().stream().filter(Objects::nonNull).forEach(this.latestFeedback::add);
		}
	}

	@Override
	public int getParticipationId() {
		return this.participationId;
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
