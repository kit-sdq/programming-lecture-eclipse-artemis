/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.artemis.mapping;

import java.io.Serializable;

public interface ISubmission extends Serializable {

	String getParticipantIdentifier();

	String getRepositoryUrl();

	int getSubmissionId();

	int getCorrectionRound();
}
