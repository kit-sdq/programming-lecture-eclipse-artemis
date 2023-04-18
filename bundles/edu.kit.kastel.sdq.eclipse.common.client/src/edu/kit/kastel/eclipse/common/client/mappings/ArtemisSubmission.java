/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.mappings;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ResultsDTO;

public class ArtemisSubmission implements ISubmission, Serializable {
	private static final long serialVersionUID = 4084879944629151733L;

	@JsonProperty(value = "id")
	private int submissionId;
	@JsonProperty
	private String commitHash;

	// for constructing hasSubmittedAssessment and hasSavedAssessment
	@JsonProperty
	private ResultsDTO[] results;

	// for getting participantIdentifier, participantName, repositoryUrl
	@JsonProperty(value = "participation", required = true)
	private ParticipationDTO participation;

	private transient int correctionRound;

	/**
	 * For Auto-Deserialization Need to call this::init thereafter!
	 */
	public ArtemisSubmission() {
		// NOP
	}

	@Override
	public String getParticipantIdentifier() {
		return this.participation.getParticipantIdentifier();
	}

	@Override
	public String getRepositoryUrl() {
		String studentsUrl = this.participation.getRepositoryUrl();
		String studentId = this.participation.getParticipantIdentifier();

		int startIndexOfUID = studentsUrl.indexOf(studentId);
		int endIndexOfUID = studentsUrl.indexOf("@");

		assert startIndexOfUID < endIndexOfUID && startIndexOfUID >= 0 && endIndexOfUID >= 0;

		String newUrl = "";
		newUrl += studentsUrl.substring(0, startIndexOfUID);
		newUrl += studentsUrl.substring(endIndexOfUID + 1);
		return newUrl;
	}

	@Override
	public int getSubmissionId() {
		return this.submissionId;
	}

	public void init(int correctionRound) {
		this.correctionRound = correctionRound;
	}

	@Override
	public int getCorrectionRound() {
		return this.correctionRound;
	}

}
