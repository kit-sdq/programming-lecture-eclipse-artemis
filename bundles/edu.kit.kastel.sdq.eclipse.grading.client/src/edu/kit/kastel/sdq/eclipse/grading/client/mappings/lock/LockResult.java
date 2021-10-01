package edu.kit.kastel.sdq.eclipse.grading.client.mappings.lock;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IFeedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IParticipation;

public class LockResult implements ILockResult {

	private int submissionID;
	private List<IFeedback> preexistentFeedbacks;
	private IParticipation participation;

	@JsonCreator
	public LockResult(@JsonProperty("id") int submissionID, @JsonProperty("results") List<LockCallAssessmentResult> previousAssessmentresults,
			@JsonProperty("participation") ParticipationDTO participation) {
		this.submissionID = submissionID;
		this.participation = participation;

		this.preexistentFeedbacks = new LinkedList<>();
		LockCallAssessmentResult latestResult = previousAssessmentresults.isEmpty() //
				? null //
				: previousAssessmentresults.get(previousAssessmentresults.size() - 1);

		if (latestResult != null) {
			this.preexistentFeedbacks.addAll(latestResult.getFeedbacks());
		}
		// previousAssessmentresults.stream().forEach(prevAssessment ->
		// this.preexistentFeedbacks.addAll(prevAssessment.getFeedbacks()));
	}

	@Override
	public IParticipation getParticipation() {
		return this.participation;
	}

	@Override
	public List<IFeedback> getPreexistentFeedbacks() {
		return this.preexistentFeedbacks;
	}

	@Override
	public int getSubmissionId() {
		return this.submissionID;
	}

	@Override
	public String toString() {
		return "LockResult [" + "submissionID=" + this.submissionID + ", participationID=" + this.participation + ", preexistentFeedbacks="
				+ this.preexistentFeedbacks + "]";
	}

}
