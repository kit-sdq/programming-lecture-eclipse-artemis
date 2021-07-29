package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.util.function.Predicate;

public interface ISubmission {

	/**
	 *
	 * Used to be able to discriminate submissions that were started by the caller.
	 */
	enum Filter {
		SAVED_BUT_NOT_SUBMITTED(submission -> submission.hasSavedAssessment() && !submission.hasSubmittedAssessment()),
		SAVED_AND_SUBMITTED(submission -> submission.hasSavedAssessment() && submission.hasSubmittedAssessment()),
		NOT_SUBMITTED(submission -> !submission.hasSubmittedAssessment()),
		ALL(submission -> true);

		private Predicate<ISubmission> filterPredicate;
		Filter(Predicate<ISubmission> filterPredicate) {
			this.filterPredicate = filterPredicate;
		}

		public Predicate<ISubmission> getFilterPredicate() {
			return this.filterPredicate;
		}
	}

	String getParticipantIdentifier();

	String getParticipantName();

	String getRepositoryUrl();

	int getSubmissionId();

	/**
	 *
	 * @return whether this submission has an assessment known to artemis which is "saved" or "submitted".
	 */
	boolean hasSavedAssessment();

	/**
	 *
	 * @return whether this submission has an assessment known to artemis which is "submitted".
	 */
	boolean hasSubmittedAssessment();
}
