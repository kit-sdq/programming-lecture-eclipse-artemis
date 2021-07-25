package edu.kit.kastel.sdq.eclipse.grading.api;

public interface ISubmission {

	//TODO define

	String getParticipantIdentifier();

	String getParticipantName();

	String getRepositoryUrl();

	int getSubmissionId();

	boolean hasSubmittedAssessment();
}
