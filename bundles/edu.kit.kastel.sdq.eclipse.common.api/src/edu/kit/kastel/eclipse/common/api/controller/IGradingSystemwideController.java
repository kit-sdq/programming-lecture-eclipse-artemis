/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.controller;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import edu.kit.kastel.eclipse.common.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.artemis4j.api.artemis.Course;
import edu.kit.kastel.sdq.artemis4j.api.artemis.Exercise;
import edu.kit.kastel.sdq.artemis4j.api.artemis.ExerciseStats;
import edu.kit.kastel.sdq.artemis4j.api.artemis.assessment.Submission;

public interface IGradingSystemwideController extends ISystemwideController {

	/**
	 * <B>BACKLOG</B><br/>
	 * Get all submissions (their project names) which were sometime started by the
	 * calling tutor. Based on current exercise.
	 * ISystemwideController::setExerciseId() must have been called before!
	 *
	 * @return the respective project Names (unique).
	 */
	List<String> getBegunSubmissionsProjectNames();

	/**
	 * Get assessment controller for current state (courseID, exerciseID,
	 * submissionID, exerciseConfig).
	 */
	IAssessmentController getCurrentAssessmentController();

	/**
	 * <B>BACKLOG</B><br/>
	 * <li>Loads an already assessed (started, saved or even submitted) submission
	 * for re-assessment.
	 * <li>You need to select a submission via
	 * {@link #setAssessedSubmissionByProjectName(String)} )}, first! Has the same
	 * effect as {@link #startCorrectionRound1()} )} otherwise.
	 */
	void loadAgain();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>Deletes local project.
	 *
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
	 * <li>You want to have called {@link #getBegunSubmissionsProjectNames()},
	 * first!
	 *
	 */
	void setAssessedSubmissionByProjectName(String projectName);

	/**
	 * <B>ASSESSMENT</B><br/>
	 *
	 * @return whether a new assessment was started or not, depending on whether
	 *         there was a submission available.
	 */
	boolean startCorrectionRound1();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>Like {@link #startCorrectionRound1()}, but with correction round 2 as a
	 * parameter.
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
	 * @return whether download was successful or not
	 */
	boolean downloadExerciseAndSubmission(Course course, Exercise exercise, Submission submission, IProjectFileNamingStrategy projectNaming);

	boolean isAssessmentStarted();

	Optional<Exercise> getSelectedExercise();

	ExerciseStats getStats();

	Path getCurrentProjectPath();

}
