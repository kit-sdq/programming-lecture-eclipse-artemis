/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.controller;

import java.util.List;
import java.util.Optional;

import edu.kit.kastel.eclipse.common.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.SubmissionFilter;

public interface IGradingSystemwideController extends ISystemwideController {

	/**
	 * <B>BACKLOG</B><br/>
	 * Get all submissions (their project names) which were sometime started by the
	 * calling tutor. Based on current exercise.
	 * ISystemwideController::setExerciseId() must have been called before!
	 *
	 * @param filter determine which kinds of submissions should be filtered (= be
	 *               in the result)
	 * @return the respective project Names (unique).
	 */
	List<String> getBegunSubmissionsProjectNames(SubmissionFilter filter);

	/**
	 * Get assessment controller for current state (courseID, exerciseID,
	 * submissionID, exerciseConfig).
	 */
	IAssessmentController getCurrentAssessmentController();

	/**
	 * <B>BACKLOG</B><br/>
	 * <li>Loads an already assessed (started, saved or even submitted) submission
	 * for re-assessment.
	 * <li>You need to select a submission via {@link #setAssessedSubmission(int)},
	 * first! Has the same effect as {@link #startAssessment()} otherwise.
	 */
	void loadAgain();
	
	/**
	 * <B>BACKLOG</B><br/>
	 * <li>Deletes local project.
	 */
	void closeAssessment();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>Deletes local project. Renews the lock and downloads the submission
	 * project again.
	 *
	 */
	void reloadAssessment();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>Saves the assessment to Artemis.
	 *
	 */
	void saveAssessment();

	/**
	 * <B>BACKLOG</B><br/>
	 * <li>Only sets the submission, does not start the assessment!.
	 * <li>You want to have called {@link #getBegunSubmissions(ISubmission.Filter)},
	 * first!
	 *
	 */
	void setAssessedSubmissionByProjectName(String projectName);

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>The same as {@link #startAssessment()}.
	 *
	 *
	 * @return whether a new assessment was started or not, depending on whether
	 *         there was a submission available.
	 */
	boolean startCorrectionRound1();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>Like {@link #startAssessment()}, but with correction round 2 as a
	 * parameter.
	 *
	 *
	 * @return whether a new assessment was started or not, depending on whether
	 *         there was a submission available.
	 */
	boolean startCorrectionRound2();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>Saves and submits the assessment to Artemis. Deletes project (in eclipse
	 * and on files system) thereafter.
	 *
	 */
	void submitAssessment();

	/**
	 * Download submissions defined by the given submissionIds
	 *
	 * @param submissionIds
	 * @return whether download was successful or not
	 */
	boolean downloadExerciseAndSubmission(ICourse courseID, IExercise exerciseID, ISubmission submissionID, IProjectFileNamingStrategy projectNaming);

	boolean isAssessmentStarted();

	Optional<IExercise> getSelectedExercise();

}
