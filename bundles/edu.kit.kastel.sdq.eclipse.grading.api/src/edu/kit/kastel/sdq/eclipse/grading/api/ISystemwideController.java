package edu.kit.kastel.sdq.eclipse.grading.api;

import java.io.File;
import java.util.Collection;

import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObservable;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

public interface ISystemwideController {

	IAlertObservable getAlertObservable();

	/**
	 *
	 * @return the one artemis gui controller.
	 */
	IArtemisGUIController getArtemisGUIController();

	/**
	 * <B>BACKLOG</B><br/>
	 * Get all submissions (their project names) which were sometime started by the calling tutor. Based on current exercise.
	 * ISystemwideController::setExerciseId() must have been called before!
	 * TODO see tolle Zustandsautomat-Grafik!
	 *
	 * @param filter determine which kinds of submissions should be filtered (= be in the result)
	 * @return the respective project Names (unique).
	 */
	Collection<String> getBegunSubmissionsProjectNames(ISubmission.Filter filter);

	/**
	 *
	 *  Get assessment controller for current state (courseID, exerciseID, submissionID, exerciseConfig).
	 */
	IAssessmentController getCurrentAssessmentController();

	/**
	 * <B>BACKLOG</B><br/>
	 * Loads an already assessed (started, saved or even submitted) submission for re-assessment.
	 *
	 * You need to select a submission via {@link #setAssessedSubmission(int)}, first!
	 * Has the same effect as {@link #startAssessment()} otherwise.
	 * TODO see tolle Zustandsautomat-Grafik!
	 */
	void loadAgain();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * Deletes local project. Renews the lock and downloads the submission project again.
	 * TODO see tolle Zustandsautomat-Grafik!
	 */
	void reloadAssessment();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * Saves the assessment to Artemis.
	 * TODO see tolle Zustandsautomat-Grafik!
	 */
	void saveAssessment();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li> current state (exerciseID, courseID) is used for call to artemis: nextAssessement
	 * <li> if an assessment is available, it is downloaded and locked.
	 * @return whether a new assessment was started or not, depending on whether there was a submission available.
	 * TODO see tolle Zustandsautomat-Grafik!
	 */
	boolean startAssessment();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * The same as {@link #startAssessment()}.
	 * @return whether a new assessment was started or not, depending on whether there was a submission available.
	 * TODO see tolle Zustandsautomat-Grafik!
	 */
	boolean startCorrectionRound1();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * Like {@link #startAssessment()}, but with correction round 2 as a parameter.
	 * @return whether a new assessment was started or not, depending on whether there was a submission available.
	 * TODO see tolle Zustandsautomat-Grafik!
	 */
	boolean startCorrectionRound2();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * Saves and submits the assessment to Artemis. Deletes project (in eclipse and on files system) thereafter.
	 * TODO see tolle Zustandsautomat-Grafik!
	 */
	void submitAssessment();

	/**
	 * <B>BACKLOG</B><br/>
	 * You want to have called {@link #getBegunSubmissions(ISubmission.Filter)}, first!
	 * TODO see tolle Zustandsautomat-Grafik!
	 *
	 */
	void setAssessedSubmissionByProjectName(String projectName);

	/**
	 * set the new annotation model config globally.
	 * @param newConfigFile
	 */
	void setConfigFile(File newConfigFile);

	/**
	 *
	 * <B>ASSESSMENT - STATE</B><br/>
	 * Set the current course for further assessment-related actions, such as {@link #setExerciseId(String)}
	 * TODO see tolle Zustandsautomat-Grafik!
	 *
	 * @param courseShortName unique short name
	 * @return all exercise short names. Can be used to call {@link #setExerciseId(String)}.
	 */
	Collection<String> setCourseIdAndGetExerciseShortNames(String courseShortName);

	/**
	 * <B>ASSESSMENT - STATE</B><br/>
	 * Set the current exercise for further assessment-related actions, such as {@link #startAssessment()}
	 * TODO see tolle Zustandsautomat-Grafik!
	 * @param exerciseShortName unique short name
	 */
	void setExerciseId(String exerciseShortName);
}
