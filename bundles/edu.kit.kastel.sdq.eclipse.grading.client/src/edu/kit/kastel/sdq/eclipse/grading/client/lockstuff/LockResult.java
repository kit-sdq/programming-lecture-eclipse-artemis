package edu.kit.kastel.sdq.eclipse.grading.client.lockstuff;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IFeedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;

public class LockResult implements ILockResult {

	private int id;
	private Collection<IFeedback> preexistentFeedbacks;

	//results: Map<Integer,

	@JsonCreator
	public LockResult(
			@JsonProperty("id") int id,
			@JsonProperty("results") List<AssessmentResult> previousAssessmentresults) {
		this.id = id;
		//TODO should be only one, right? Get the last, for now...
		this.preexistentFeedbacks = previousAssessmentresults.get(0).getFeedbacks();
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public Collection<IFeedback> getPreexistentFeedbacks() {
		return this.preexistentFeedbacks;
	}

	@Override
	public String toString() {
		return "LockResult [id=" + this.id + ", preexistentFeedbacks=" + this.preexistentFeedbacks + "]";
	}

}
