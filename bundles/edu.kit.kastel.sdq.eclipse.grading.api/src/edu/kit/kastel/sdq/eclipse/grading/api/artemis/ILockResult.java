package edu.kit.kastel.sdq.eclipse.grading.api.artemis;

import java.util.Collection;

/**
 * This is gotten from acquiring a lock (no matter if the lock is already held by the caller or not).
 * It is used to calculate the assessment result.
 *
 */
public interface ILockResult {

	double getMaxPoints();

	/**
	 *
	 * @return the participationID this submissionID belongs to (one participation has one or many submissions).
	 */
	int getParticipationID();

	/**
	 *
	 * @return all {@link IFeedback}s that are saved in Artemis. This is used to calculate the assessment result which is sent back to Artemis.
	 */
	Collection<IFeedback> getPreexistentFeedbacks();

	/**
	 *
	 * @return the submissionID this result belongs to (one participation has one or many submissions).
	 */
	int getSubmissionID();
}
