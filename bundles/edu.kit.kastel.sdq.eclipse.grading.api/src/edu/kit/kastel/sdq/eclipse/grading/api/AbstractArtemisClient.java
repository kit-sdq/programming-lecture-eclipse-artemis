package edu.kit.kastel.sdq.eclipse.grading.api;

import java.io.File;
import java.util.Collection;

import javax.security.sasl.AuthenticationException;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;

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
	 * Clones Exercise n times for n submissions, like so
	 * exercise-$EXERCISEID-$EXERCISENAME_submission-$SUBMISSIONID-$SUBMISSIONNAME.
	 * E.g.: exercise-1-TestExercise_submission-5-HansPeterBaxter
	 */
	public abstract void downloadExerciseAndSubmissions(IExercise exercise, Collection<ISubmission> submissions, File directory);

	/**
	 * TODO maybe remove
	 * Using the IExercise instead of exerciseId, because the caller has gotten the IExercise object, already.
	 * @param exerciss	needed, although submissionIds are unique!
	 * @param directory the root directory. Exercise dirs are named by Exercise::getShortName
	 */
	public abstract void downloadExercises(Collection<IExercise> exercises, File directory);

	/**
	 * TODO maybe remove
	 * TODO multiple Submissions might not be possible
	 * Download submissions defined by the given submissionIds
	 * @param exerciseId	needed, although submissionIds are unique!
	 * @param submissionIds
	 */
	public abstract void downloadSubmissions(Collection<ISubmission> submissions, File directory);
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
	 * @return all available courses (contains exercises and available submissions
	 * @throws Exception TODO create an exception type!
	 */
	public abstract Collection<ICourse> getCourses() throws Exception;


	/**
	 * Starts an assessment for the given submission. Acquires a lock in the process.
	 * @param submissionID
	 * @throws Exception TODO create an exception type!
	 *
	 * @return the data gotten back. Needed for submitting correctly.
	 */
	public abstract ILockResult startAssessment(int submissionID) throws Exception;

	/**
	 * Submit the assessment to Artemis. Must have been started by {@code startAssessment}, before!
	 */
	public abstract void submitAssessment(int submissionIDs, String payload) throws AuthenticationException;
}
