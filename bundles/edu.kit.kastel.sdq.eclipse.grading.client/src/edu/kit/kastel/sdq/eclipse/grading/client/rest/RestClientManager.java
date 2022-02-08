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
	private ISubmissionsArtemisClient submissionClient;
	private ICourseArtemisClient courseClient;
	private IExamArtemisClient examClient;
	private IFeedbackArtemisClient feedbackClient;
	private IParticipationArtemisClient participationClient;
	private IUtilArtemisClient utilClient;
	private IAssessmentArtemisClient assessmentClient;
	
	public RestClientManager(String hostname, String username, String password) {
		this.username = username;
		this.password = password;	
		this.hostname = hostname;
		
		this.loginManager = new LoginManager(hostname, username, password);
	}
	
	public void login() throws ArtemisClientException {
		if(!isReady()) {
			throw new ArtemisClientException("No credentials set in the preferences tab.");
		}
		loginManager.init();
	}
	
    public boolean isReady() {
        return !(this.hostname.isBlank() && this.username.isBlank() && this.password.isBlank());
    }
	
	public IAuthenticationArtemisClient getAuthenticationClient() {

		return loginManager;
	}
	
	public ISubmissionsArtemisClient getSubmissionArtemisClient() {
		if(submissionClient == null)
			submissionClient = new SubmissionsArtemisClient(hostname, loginManager.getBearerToken(), loginManager.getAssessor());
		return submissionClient;
	}
	
	public ICourseArtemisClient getCourseArtemisClient() {
		if (courseClient== null)
			courseClient = new MappingLoaderArtemisClient(getSubmissionArtemisClient(), hostname, loginManager.getBearerToken());
		return courseClient;
	}
	
	public IExamArtemisClient getExamArtemisClient() {
		if (examClient== null)
			examClient = new ExamArtemisClient(hostname, loginManager.getBearerToken());
		return examClient;
	}
	
	public IFeedbackArtemisClient getFeedbackArtemisClient() {
		if(feedbackClient == null)
			feedbackClient = new FeedbackArtemisClient(hostname, loginManager.getBearerToken());
		return feedbackClient;
	}
	
	public IParticipationArtemisClient getParticipationArtemisClient() {
		if(participationClient == null)
			participationClient = new ParticipationArtemisClient(hostname, loginManager.getBearerToken());
		return participationClient;
	}
	
	public IUtilArtemisClient getUtilArtemisClient() {
		if(utilClient == null)
			utilClient =  new UtilArtemisClient(hostname, loginManager.getBearerToken());
		return utilClient;
	}
	
	public IAssessmentArtemisClient getAssessmentArtemisClient() {
		if(assessmentClient == null)
			assessmentClient = new AssessmentArtemisClient(hostname, loginManager.getBearerToken());
		return assessmentClient;
	}
}
