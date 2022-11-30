/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.artemis.mapping;

import java.io.Serializable;

public interface ISubmission extends Serializable {

	String getParticipantIdentifier();

	String getRepositoryUrl();

	int getSubmissionId();

	/**
	 *
	 * @return whether this submission has an assessment known to artemis which is
	 *         {@code saved} or {@code submitted}
	 */
	boolean hasSavedAssessment();

	/**
	 *
	 * @return whether this submission has an assessment known to artemis which is
	 *         {@code submitted}
	 */
	boolean hasSubmittedAssessment();

	int getCorrectionRound();
}
