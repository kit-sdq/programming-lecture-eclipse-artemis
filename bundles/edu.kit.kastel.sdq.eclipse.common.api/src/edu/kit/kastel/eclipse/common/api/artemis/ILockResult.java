/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.artemis;

import java.io.Serializable;
import java.util.List;

import edu.kit.kastel.eclipse.common.api.artemis.mapping.Feedback;

/**
 * This is gotten from acquiring a lock (no matter if the lock is already held
 * by the caller or not). It is used to calculate the assessment result.
 *
 */
public interface ILockResult extends Serializable {

	/**
	 *
	 * @return the participationId this submissionId belongs to (one participation
	 *         has one or many submissions).
	 */
	int getParticipationId();

	/**
	 *
	 * @return all {@link Feedback Feedbacks} that are saved in Artemis. This is
	 *         used to calculate the assessment result which is sent back to
	 *         Artemis.
	 */
	List<Feedback> getLatestFeedback();

	/**
	 *
	 * @return the submissionId this result belongs to (one participation has one or
	 *         many submissions).
	 */
	int getSubmissionId();
}
