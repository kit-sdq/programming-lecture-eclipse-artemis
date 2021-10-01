package edu.kit.kastel.sdq.eclipse.grading.api.artemis;

import java.util.List;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IFeedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IParticipation;

/**
 * This is gotten from acquiring a lock (no matter if the lock is already held
 * by the caller or not). It is used to calculate the assessment result.
 *
 */
public interface ILockResult {

	// TODO check if IExercise has that field, too!
	double getMaxPoints();

	/**
	 *
	 * @return the participationID this submissionID belongs to (one participation
	 *         has one or many submissions).
	 */
	IParticipation getParticipation();

	/**
	 *
	 * @return all {@link IFeedback}s that are saved in Artemis. This is used to
	 *         calculate the assessment result which is sent back to Artemis.
	 */
	List<IFeedback> getPreexistentFeedbacks();

	/**
	 *
	 * @return the submissionID this result belongs to (one participation has one or
	 *         many submissions).
	 */
	int getSubmissionID();
}
