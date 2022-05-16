/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client.rest;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.client.IAssessmentArtemisClient;
import edu.kit.kastel.eclipse.common.api.client.IAuthenticationArtemisClient;
import edu.kit.kastel.eclipse.common.api.client.ICourseArtemisClient;
import edu.kit.kastel.eclipse.common.api.client.IExamArtemisClient;
import edu.kit.kastel.eclipse.common.api.client.IFeedbackArtemisClient;
import edu.kit.kastel.eclipse.common.api.client.IParticipationArtemisClient;
import edu.kit.kastel.eclipse.common.api.client.ISubmissionsArtemisClient;
import edu.kit.kastel.eclipse.common.api.client.IUtilArtemisClient;
import edu.kit.kastel.eclipse.common.api.messages.Messages;

public class RestClientManager {
	private final String username;
	private final String password;
	private final String hostname;

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

	public String getArtemisUrl() {
		return this.loginManager.getArtemisUrl();
	}

	public void login() throws ArtemisClientException {
		if (!this.isReady()) {
			throw new ArtemisClientException(Messages.CLIENT_NO_CREDENTIALS);
		}
		this.loginManager.init();
	}

	public boolean isReady() {
		return !(this.hostname.isBlank() || this.username.isBlank() || this.password.isBlank());
	}

	public IAuthenticationArtemisClient getAuthenticationClient() {
		return this.loginManager;
	}

	public ISubmissionsArtemisClient getSubmissionArtemisClient() {
		if (this.submissionClient == null) {
			this.submissionClient = new SubmissionsArtemisClient(this.hostname, this.loginManager.getBearerToken(), this.loginManager.getUser());
		}
		return this.submissionClient;
	}

	public ICourseArtemisClient getCourseArtemisClient() {
		if (this.courseClient == null) {
			this.courseClient = new MappingLoaderArtemisClient(this.getSubmissionArtemisClient(), this.hostname, this.loginManager.getBearerToken());
		}
		return this.courseClient;
	}

	public IExamArtemisClient getExamArtemisClient() {
		if (this.examClient == null) {
			this.examClient = new ExamArtemisClient(this.hostname, this.loginManager.getBearerToken());
		}
		return this.examClient;
	}

	public IFeedbackArtemisClient getFeedbackArtemisClient() {
		if (this.feedbackClient == null) {
			this.feedbackClient = new FeedbackArtemisClient(this.hostname, this.loginManager.getBearerToken());
		}
		return this.feedbackClient;
	}

	public IParticipationArtemisClient getParticipationArtemisClient() {
		if (this.participationClient == null) {
			this.participationClient = new ParticipationArtemisClient(this.hostname, this.loginManager.getBearerToken());
		}
		return this.participationClient;
	}

	public IUtilArtemisClient getUtilArtemisClient() {
		if (this.utilClient == null) {
			this.utilClient = new UtilArtemisClient(this.hostname);
		}
		return this.utilClient;
	}

	public IAssessmentArtemisClient getAssessmentArtemisClient() {
		if (this.assessmentClient == null) {
			this.assessmentClient = new AssessmentArtemisClient(this.hostname, this.loginManager.getBearerToken());
		}
		return this.assessmentClient;
	}

}
