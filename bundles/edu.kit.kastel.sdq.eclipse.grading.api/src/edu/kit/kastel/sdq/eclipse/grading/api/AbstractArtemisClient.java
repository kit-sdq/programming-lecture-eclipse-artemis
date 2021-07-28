package edu.kit.kastel.sdq.eclipse.grading.api;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

import javax.security.sasl.AuthenticationException;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IAssessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

/**
 * Defines interface between "backend" and e.g. ArtemisRestClient. The latter implements this interface.
 * <br/>
 * TODO:
 * <li> @See {@code IArtemisGUIController}: (mostly or wholly) same methods but more params (or whole Objects).
 * <li> abstract class or interface? See {@code ArtemisRESTClient} (in testPlugin). Constructor might be worth it.
 * <li> NO IDs, here! That would mean useless rest calls or holding data in the client (which makes no sense)!
 */
public abstract class AbstractArtemisClient {

	private String artemisUsername;
	private String artemisPassword;
	private String artemisHostname;

	/**
	 *
	 * @param artemisUsername for login to artemis
	 * @param artemisPassword for login to artemis
	 * @param artemisHostname the hostname, only! (e.g. "test.kit.edu")
	 */
	public AbstractArtemisClient(String artemisUsername, String artemisPassword, String artemisHostname) {
		this.artemisUsername = artemisUsername;
		this.artemisPassword = artemisPassword;
		this.artemisHostname = artemisHostname;
	}

	/**
	 * Clones exercise and a submission into one project.
	 */
	public abstract void downloadExerciseAndSubmission(IExercise exercise, ISubmission submission,
			File directory, IProjectFileNamingStrategy projectFileNamingStrategy) throws ArtemisClientException;

	protected String getArtemisHostname() {
		return this.artemisHostname;
	}

	protected String getArtemisPassword() {
		return this.artemisPassword;
	}

	protected String getArtemisUsername() {
		return this.artemisUsername;
	}

	/**
	 *
	 * @return the artemis "assessor" object (needed for submitting the assessment).
	 * @throws Exception TODO create an exception type!
	 */
	public abstract IAssessor getAssessor() throws ArtemisClientException, AuthenticationException;


	/**
	 *
	 * @return all available courses, containing exercises and available submissions
	 * @throws Exception TODO create an exception type!
	 */
	public abstract Collection<ICourse> getCourses() throws ArtemisClientException, AuthenticationException;

	/**
	 *
	 * @param exerciseID
	 * @param assessedByTutor only return those submissions on which the caller has (started, saved or submitted) the assessment.
	 * @return submissions for the given exerciseID, filterable.
	 * @throws Exception TODO create an exception type!
	 */
	public abstract Collection<ISubmission> getSubmissions(int exerciseID, boolean assessedByTutor)
			throws Exception;

	/**
	 * Submit the assessment to Artemis. Must have been started by {@link #startAssessment(int)} or {@link #startNextAssessment(int, int)} before!
	 * @param participationID THOU SHALT NOT PROVIDE THE SUBMISSIONID, HERE!
 * 							The participationID can be gotten from the {@link ILockResult}, via {@link #startAssessment(int)} or {@link #startNextAssessment(int, int)}!
	 * @param payload the payload formatted correctly.
	 */
	public abstract void saveAssessment(int participationID, boolean submit, String payload) throws AuthenticationException;

	/**
	 * Starts an assessment for the given submission. Acquires a lock in the process.
	 *
	 * @param submissionID
	 * @return the data gotten back. Needed for submitting correctly.	 *
	 * @throws Exception TODO create an exception type!
	 */

	public abstract ILockResult startAssessment(int submissionID) throws AuthenticationException, ArtemisClientException;

	/**
	 * Starts an assessment for any submission (determined by artemis). Acquires a lock in the process.
	 * @throws Exception TODO create an exception type!
	 *
	 * @param correctionRound relevant for exams! may be 0 or 1
	 *
	 * @return
	 * 		<li> the data gotten back. Needed for submitting correctly.
	 * 		<li> <b>null</b> if there is no submission left to correct
	 */
	public abstract Optional<ILockResult> startNextAssessment(int exerciseID, int correctionRound)  throws AuthenticationException, ArtemisClientException;
}
