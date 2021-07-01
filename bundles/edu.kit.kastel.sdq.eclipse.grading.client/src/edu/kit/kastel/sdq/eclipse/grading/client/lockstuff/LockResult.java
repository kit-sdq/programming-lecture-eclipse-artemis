package edu.kit.kastel.sdq.eclipse.grading.client.lockstuff;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IFeedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;

public class LockResult implements ILockResult {

	private int submissionID;
	private Collection<IFeedback> preexistentFeedbacks;
	private int participationID;

	@JsonCreator
	public LockResult(
			@JsonProperty("id") int submissionID,
			@JsonProperty("results") List<LockCallAssessmentResult> previousAssessmentresults,
			@JsonProperty("participation") ParticipationDummy participationDummy) {
		this.submissionID = submissionID;
		this.participationID = participationDummy.getParticipationID();

		//TODO should be only one, right? Get the last, for now...
		this.preexistentFeedbacks = new LinkedList();
		previousAssessmentresults.stream().forEach(prevAssessment -> this.preexistentFeedbacks.addAll(prevAssessment.getFeedbacks()));
//		this.preexistentFeedbacks = previousAssessmentresults.get(0).getFeedbacks();
	}

	@Override
	public int getParticipationID() {
		return this.participationID;
	}

	@Override
	public Collection<IFeedback> getPreexistentFeedbacks() {
		return this.preexistentFeedbacks;
	}

	@Override
	public int getSubmissionID() {
		return this.submissionID;
	}

	@Override
	public String toString() {
		return "LockResult ["
				+ "submissionID=" + this.submissionID
				+ ", participationID=" + this.participationID
				+ ", preexistentFeedbacks=" + this.preexistentFeedbacks
				+ "]";
	}

}
