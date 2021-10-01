package edu.kit.kastel.sdq.eclipse.grading.api;

import java.io.File;
import java.util.List;
import java.util.Optional;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IAssessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IParticipation;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

/**
 * Encapsulates methods to get data from and send data to Artemis
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
	protected AbstractArtemisClient(String artemisUsername, String artemisPassword, String artemisHostname) {
		this.artemisUsername = artemisUsername;
		this.artemisPassword = artemisPassword;
		this.artemisHostname = artemisHostname;
	}

	/**
	 * Clones exercise and a submission into one project.
	 */
	public abstract void downloadExerciseAndSubmission(IExercise exercise, ISubmission submission, File directory,
			IProjectFileNamingStrategy projectFileNamingStrategy) throws ArtemisClientException;

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
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */
	public abstract IAssessor getAssessor() throws ArtemisClientException;

	/**
	 *
	 * @return all available courses, containing exercises and available submissions
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */
	public abstract List<ICourse> getCourses() throws ArtemisClientException;

	/**
	 *
	 * @param exerciseID
	 * @param assessedByTutor only return those submissions on which the caller has
	 *                        (started, saved or submitted) the assessment.
	 * @return submissions for the given exerciseID, filterable.
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */
	public abstract List<ISubmission> getSubmissions(int exerciseID, boolean assessedByTutor) throws ArtemisClientException;

	/**
	 * Submit the assessment to Artemis. Must have been started by
	 * {@link #startAssessment(int)} or {@link #startNextAssessment(int, int)}
	 * before!
	 *
	 * @param participation THOU SHALT NOT PROVIDE THE SUBMISSIONID, HERE! The
	 *                      participationID can be gotten from the
	 *                      {@link ILockResult}, via {@link #startAssessment(int)}
	 *                      or {@link #startNextAssessment(int, int)}! * @param
	 *                      submit determine whether the assessment should be
	 *                      submitted or just saved.
	 * @param payload       the payload formatted correctly. TODO ISubmission statt
	 *                      ID TODO Payload verstecken, irgendein
	 *                      Vorberechnungsergebnis
	 * @throws ArtemisClientException
	 */
	public abstract void saveAssessment(IParticipation participation, boolean submit, String payload) throws ArtemisClientException;

	/**
	 * Starts an assessment for the given submission. Acquires a lock in the
	 * process.
	 *
	 * @param submissionID
	 * @return the data gotten back, which is needed for submitting the assessment
	 *         result correctly ({@link #saveAssessment(int, boolean, String)}
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */

	public abstract ILockResult startAssessment(int submissionID) throws ArtemisClientException;

	/**
	 * Starts an assessment for any available submission (determined by artemis).
	 * Acquires a lock in the process.
	 *
	 * @param correctionRound relevant for exams! may be 0 or 1
	 * @return
	 *         <li>the data gotten back. Needed for submitting correctly.
	 *         <li><b>null</b> if there is no submission left to correct
	 * @throws ArtemisClientException if some errors occur while parsing the result
	 *                                or if authentication fails.
	 */
	public abstract Optional<ILockResult> startNextAssessment(int exerciseID, int correctionRound) throws ArtemisClientException;
}
