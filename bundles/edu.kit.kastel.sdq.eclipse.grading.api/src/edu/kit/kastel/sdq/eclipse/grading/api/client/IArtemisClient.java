package edu.kit.kastel.sdq.eclipse.grading.api.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.AssessmentResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.ILockResult;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Assessor;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;

public interface IArtemisClient {
	/**
	 * Clones exercise and a submission into one project.
	 */
	void downloadExerciseAndSubmission(IExercise exercise, ISubmission submission, File dir, IProjectFileNamingStrategy namingStrategy)
			throws ArtemisClientException;
	/**
	 *
	 * @return the artemis "assessor" object (needed for submitting the assessment).
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */
	Assessor getAssessor() throws ArtemisClientException ;

	/**
	 *
	 * @return all available courses, containing exercises and available submissions
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */
	List<ICourse> getCourses() throws ArtemisClientException;

	default List<ISubmission> getSubmissions(IExercise exercise) throws ArtemisClientException {
		List<ISubmission> submissions = new ArrayList<>(this.getSubmissions(exercise, 0));

		if (exercise.hasSecondCorrectionRound()) {
			submissions.addAll(this.getSubmissions(exercise, 1));
		}

		return submissions;
	}

	/**
	 *
	 * @param exerciseID
	 * @param assessedByTutor only return those submissions on which the caller has
	 *                        (started, saved or submitted) the assessment.
	 * @return submissions for the given exerciseID, filterable.
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */
	List<ISubmission> getSubmissions(IExercise exercise, int correctionRound) throws ArtemisClientException;

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
	 * @param assessment    the assessment
	 *
	 * @throws ArtemisClientException
	 */
	void saveAssessment(ParticipationDTO participation, boolean submit, AssessmentResult assessment) throws ArtemisClientException;

	/**
	 * Starts an assessment for the given submission. Acquires a lock in the
	 * process.
	 *
	 * @param submission
	 * @return the data gotten back, which is needed for submitting the assessment
	 *         result correctly ({@link #saveAssessment(int, boolean, String)}
	 * @throws ArtemisClientException if some errors occur while parsing the result.
	 */

	ILockResult startAssessment(ISubmission submission) throws ArtemisClientException;

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
	Optional<ILockResult> startNextAssessment(IExercise exerciseID, int correctionRound) throws ArtemisClientException;

	void downloadExercise(IExercise exercise, File dir, IProjectFileNamingStrategy namingStrategy, String repoUrl) throws ArtemisClientException;
	
	ParticipationDTO startParticipationForExercise(ICourse couse, IExercise exercise) throws ArtemisClientException;
	
	ParticipationDTO resumeParticipationForExercise(ICourse couse, IExercise exercise) throws ArtemisClientException;
	
	ParticipationDTO getParticipationForExercise(ICourse couse, IExercise exercise)  throws ArtemisClientException;
	
	ParticipationDTO getParticipationWithLatestResultForExercise(int participationId)  throws ArtemisClientException;
	
	IExam startExam(ICourse course, IExam exam) throws ArtemisClientException;
	
	Feedback[] getFeedbackForResult(int particiaptionId, int resultId) throws ArtemisClientException;
}
