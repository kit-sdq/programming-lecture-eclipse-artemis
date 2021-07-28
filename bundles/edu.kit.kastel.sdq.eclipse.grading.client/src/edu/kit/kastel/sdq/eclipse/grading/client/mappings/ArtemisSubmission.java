package edu.kit.kastel.sdq.eclipse.grading.client.mappings;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

public class ArtemisSubmission implements ISubmission {

	private int submissionId;
	private String participantIdentifier;
	private String participantName;

	private String repositoryUrl;
	private String commitHash;

	private boolean hasSubmittedAssessment;
	private boolean hasSavedAssessment;

	public ArtemisSubmission(int submissionId, String participantIdentifier, String participantName,
			String repositoryUrl, String commitHash, boolean hasSubmittedAssessment, boolean hasSavedAssessment) {
		super();
		this.submissionId = submissionId;
		this.participantIdentifier = participantIdentifier;
		this.participantName = participantName;
		this.repositoryUrl = repositoryUrl;
		this.commitHash = commitHash;
		this.hasSubmittedAssessment = hasSubmittedAssessment;
		this.hasSavedAssessment = hasSavedAssessment;
	}

	protected String getCommitHash() {
		return this.commitHash;
	}

	@Override
	public String getParticipantIdentifier() {
		return this.participantIdentifier;
	}

	@Override
	public String getParticipantName() {
		return this.participantName;
	}

	@Override
	public String getRepositoryUrl() {
		return this.repositoryUrl;
	}

	@Override
	public int getSubmissionId() {
		return this.submissionId;
	}

	@Override
	public boolean hasSavedAssessment() {
		return this.hasSavedAssessment;
	}

	@Override
	public boolean hasSubmittedAssessment() {
		return this.hasSubmittedAssessment;
	}

	/**
	 *
	 * @return a String like {@code toString}, but with fields not contained in ISubmission
	 */
	public String toDebugString() {
		return "ArtemisSubmission [submissionId=" + this.submissionId + ", participantIdentifier=" + this.participantIdentifier
				+ ", participantName=" + this.participantName + ", repositoryUrl=" + this.repositoryUrl + ", commitHash="
				+ this.commitHash + "]";
	}

	@Override
	public String toString() {
		return "ArtemisSubmission [submissionId=" + this.submissionId + ", participantIdentifier=" + this.participantIdentifier
				+ ", participantName=" + this.participantName + "]";
	}
}
