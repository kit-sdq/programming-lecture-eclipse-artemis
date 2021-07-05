package edu.kit.kastel.sdq.eclipse.grading.api.artemis;

import java.util.Collection;

public interface ILockResult {

	double getMaxPoints();

	/**
	 *
	 * @return the participationID this submissionID belongs to (one participation has one or many submissions).
	 */
	int getParticipationID();

	Collection<IFeedback> getPreexistentFeedbacks();

	/**
	 *
	 * @return the submissionID this result belongs to (one participation has one or many submissions).
	 */
	int getSubmissionID();
}
