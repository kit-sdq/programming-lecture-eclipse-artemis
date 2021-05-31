package edu.kit.kastel.sdq.eclipse.grading.api;

import java.util.Collection;

public interface IArtemisGUIController {

	/**
	 * TODO baseURL hardcoded or config file or param here?
	 * 
	 * @param username
	 * @param password
	 * @return true if login succeeded
	 */
	public boolean loginToArtemis(String username, String password);
	
	/**
	 * 
	 * @return all available courses (contains exercices and available submissions
	 */
	public Collection<ICourse> getCourses();
	
	/**
	 * Download submissions defined by the given submissionIds
	 * @param submissionIds
	 */
	public void downloadSubmissions(Collection<Integer> submissionIds);
	
	/**
	 * Starts an assessment for the given submission
	 * @param submissionID
	 */
	public void startAssessment(int submissionID);
	
	/**
	 * Submit the assessment to Artemis. Must have been started by {@code startAssessment}, before.
	 */
	public void submitAssessment(int submissionID);
}
