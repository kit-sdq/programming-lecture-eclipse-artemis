package edu.kit.kastel.sdq.eclipse.grading.api;

import java.io.File;
import java.util.Collection;

/**
 * Defines interface between "backend" and e.g. ArtemisRestClient. The latter implements this interface.
 * TODO define:
 * <li> @See {@code IArtemisGUIController}: (mostly or wholly) same methods but more params (or whole Objects).
 * <li> abstract class or interface? See {@code ArtemisRESTClient} (in testPlugin). Constructor might be worth it.
 */
public interface IArtemisClient {
	
	//TODO the following two methods might be better if in a Constructor!
	 
	/**
	 * Hands artemis credentials to the client.
	 * @param username
	 * @param password
	 */
	public IArtemisClient withArtemisCredentials(String username, String password);

	/**
	 * Hands the artemis host name to the client.
	 * @param username
	 * @param password
	 */
	public IArtemisClient withArtemisHostName(String artemisHostname);


	/**
	 * 
	 * @return all available courses (contains exercises and available submissions
	 */
	public Collection<ICourse> getCourses();
	
	/**
	 * Download submissions defined by the given submissionIds
	 * @param submissionIds
	 */
	public void downloadSubmissions(Collection<Integer> submissionIds, File directory);
	
	
	
	/**
	 * Starts an assessment for the given submission. Acquires a lock in the process.
	 * @param submissionID
	 */
	public void startAssessments(Collection<Integer> submissionID);
	
	/**
	 * Submit the assessment to Artemis. Must have been started by {@code startAssessment}, before!
	 */
	public void submitAssessments(Collection<Integer> submissionID);
}
