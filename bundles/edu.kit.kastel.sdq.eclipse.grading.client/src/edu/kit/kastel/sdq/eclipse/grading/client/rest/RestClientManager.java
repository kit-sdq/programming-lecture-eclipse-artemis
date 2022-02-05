package edu.kit.kastel.sdq.eclipse.grading.client.rest;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.client.IAssessmentArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.client.IAuthenticationArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.client.ICourseArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.client.IExamArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.client.IFeedbackArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.client.IParticipationArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.client.ISubmissionsArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.client.IUtilArtemisClient;

public class RestClientManager {
	private String username = "";
	private String password = "";
	private String hostname = "";
	
	private IAuthenticationArtemisClient loginManager;
	
	public RestClientManager(String hostname, String username, String password) throws ArtemisClientException {
		this.username = username;
		this.password = password;	
		this.hostname = hostname;
		this.loginManager = new LoginManager(hostname, username, password);
	}
	
    public boolean isReady() {
        return !(this.hostname.isBlank() && this.username.isBlank() && this.password.isBlank());
    }
	
	public IAuthenticationArtemisClient getAuthenticationClient() {
		return loginManager;
	}
	public ISubmissionsArtemisClient getSubmissionArtemisClient() {
			return new SubmissionsArtemisClient(hostname, loginManager.getToken(), loginManager.getAssessor());
	}
	
	public ICourseArtemisClient getCourseArtemisClient() {
		return new MappingLoaderArtemisClient(getSubmissionArtemisClient(), hostname, loginManager.getToken());
	}
	
	public IExamArtemisClient getExamArtemisClient() {
		return new ExamArtemisClient(hostname, loginManager.getToken());
	}
	
	public IFeedbackArtemisClient getFeedbackArtemisClient() {
		return new FeedbackArtemisClient(hostname, loginManager.getToken());
	}
	
	public IParticipationArtemisClient getParticipationArtemisClient() {
		return new ParticipationArtemisClient(hostname, loginManager.getToken());
	}
	
	public IUtilArtemisClient getUtilArtemisClient() {
		return new UtilArtemisClient(hostname, loginManager.getToken());
	}
	
	public IAssessmentArtemisClient getAssessmentArtemisClient() {
		return new AssessmentArtemisClient(hostname, loginManager.getToken());
	}
}