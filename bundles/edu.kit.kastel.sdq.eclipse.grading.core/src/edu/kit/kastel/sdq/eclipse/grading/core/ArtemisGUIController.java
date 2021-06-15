package edu.kit.kastel.sdq.eclipse.grading.core;

import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.AbstractArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.IArtemisGUIController;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.ICourse;

public class ArtemisGUIController implements IArtemisGUIController {

	private final IAssessmentController assessmentController;
	private AbstractArtemisClient artemisClient;
	
	public ArtemisGUIController(final IAssessmentController assessmentController) {
		this.assessmentController = assessmentController;
		//TODO initialize final artemisClient.
	}
	
	@Override
	public boolean loginToArtemis(String username, String password) {
		// TODO Implementation  
		return false;
	}

	@Override
	public Collection<ICourse> getCourses() {
		try {
			return artemisClient.getCourses();
		} catch (Exception e) {
			//TODO exception handling!
			throw new RuntimeException("..");
		}
	}

	@Override
	public void downloadSubmissions(Collection<Integer> submissionIds) {
		/* TODO for each submission: 
		 * * download via ArtemisClient
		 * * create a project
		 */
	}

	@Override
	public void startAssessment(int submissionID) {
		//  
		/* TODO
		 * * check if submission is already in the workspace.
		 * * acquire lock via ArtemisClient (need a method which only locks and does not download)
		 */
		
	}

	@Override
	public void submitAssessment(int submissionID) {
		// TODO implement
		
	}

	
}
