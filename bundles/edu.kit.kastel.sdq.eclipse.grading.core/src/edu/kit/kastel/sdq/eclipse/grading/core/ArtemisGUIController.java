package edu.kit.kastel.sdq.eclipse.grading.core;

import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.AbstractArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.IArtemisGUIController;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.client.rest.ArtemisRESTClient;

public class ArtemisGUIController implements IArtemisGUIController {

	private final String host;
	private final SystemwideController systemwideController;
	private final AbstractArtemisClient artemisClient;

	public ArtemisGUIController(final SystemwideController systemwideController, final String host, final String username, final String password) {
		this.host = host;
		this.artemisClient = new ArtemisRESTClient(username, password, host);
		this.systemwideController = systemwideController;
	}

	@Override
	public void downloadSubmissions(Collection<Integer> submissionIds, String exerciseName) {
		final IAssessmentController assessmentController = this.systemwideController.getAssessmentController(exerciseName);
		/* TODO for each submission:
		 * * download via ArtemisClient
		 * * create a project
		 */
		//TODO get file
		this.artemisClient.downloadSubmissions(null, null);
	}

	@Override
	public Collection<ICourse> getCourses() {
		try {
			return this.artemisClient.getCourses();
		} catch (final Exception e) {
			//TODO exception handling!
			throw new RuntimeException("..");

		}
	}

	@Override
	public void startAssessment(int submissionID, String exerciseName) {
		//
		/* TODO
		 * * check if submission is already in the workspace.
		 * * acquire lock via ArtemisClient (need a method which only locks and does not download)
		 * * change signature of AbstractArtemisClient::startAssessments to startAssessment. If multiple assessments at once, then HERE, not in the client!
		 */

	}

	@Override
	public void submitAssessment(int submissionID, String exerciseName) {
		final IAssessmentController assessmentController = this.systemwideController.getAssessmentController(exerciseName);
		/* TODO change signature of AbstractArtemisClient::submitAssessments to sth like
		 *      submitAssessment(int submissionID, ISubmission (or just a json formatted String))
		 */
	//	this.artemisClient.submitAssessments(null);
	}


}
