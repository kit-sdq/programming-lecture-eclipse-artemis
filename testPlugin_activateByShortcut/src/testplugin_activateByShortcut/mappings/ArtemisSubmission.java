package testplugin_activateByShortcut.mappings;

import edu.kit.kastel.sdq.eclipse.grading.api.ISubmission;

public class ArtemisSubmission implements ISubmission {
	
	private int submissionId;
	private String participantIdentifier;
	private String participantName;
	
	private String repositoryUrl;
	private String commitHash;
	
	

	public ArtemisSubmission(int submissionId, String participantIdentifier, String participantName,
			String repositoryUrl, String commitHash) {
		super();
		this.submissionId = submissionId;
		this.participantIdentifier = participantIdentifier;
		this.participantName = participantName;
		this.repositoryUrl = repositoryUrl;
		this.commitHash = commitHash;
	}

	@Override
	public int getSubmissionId() {
		return submissionId;
	}

	@Override
	public String getParticipantIdentifier() {
		return participantIdentifier;
	}

	@Override
	public String getParticipantName() {
		return participantName;
	}
	
	protected String getRepositoryUrl() {
		return repositoryUrl;
	}

	protected String getCommitHash() {
		return commitHash;
	}

	@Override
	public String toString() {
		return "ArtemisSubmission [submissionId=" + submissionId + ", participantIdentifier=" + participantIdentifier
				+ ", participantName=" + participantName + "]";
	}

	/**
	 * 
	 * @return a String like {@code toString}, but with fields not contained in ISubmission
	 */
	public String toDebugString() {
		return "ArtemisSubmission [submissionId=" + submissionId + ", participantIdentifier=" + participantIdentifier
				+ ", participantName=" + participantName + ", repositoryUrl=" + repositoryUrl + ", commitHash="
				+ commitHash + "]";
	}
}
