/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.api.artemis.mapping;

import java.util.function.Predicate;

/**
 *
 * Used to be able to discriminate submissions that were started by the caller.
 */
public enum SubmissionFilter implements Predicate<ISubmission> {
	SAVED_BUT_NOT_SUBMITTED(submission -> submission.hasSavedAssessment() && !submission.hasSubmittedAssessment()), //
	SAVED_AND_SUBMITTED(submission -> submission.hasSavedAssessment() && submission.hasSubmittedAssessment()), //
	NOT_SUBMITTED(submission -> !submission.hasSubmittedAssessment()), //
	ALL(submission -> true);

	private Predicate<ISubmission> filterPredicate;

	SubmissionFilter(Predicate<ISubmission> filterPredicate) {
		this.filterPredicate = filterPredicate;
	}

	@Override
	public boolean test(ISubmission t) {
		return this.filterPredicate.test(t);
	}
}