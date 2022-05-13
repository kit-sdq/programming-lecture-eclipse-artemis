/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.languages;

/**
 * This interface contains a method for every string used in the UI. All methods
 * are defaulted to an English text and can be overwritten by sub-classes. If a
 * subclass does not redefine all strings, they will use English as fallback.
 */
public interface I18N {

	//
	// Translations
	//

	default String settingsLargeTextBox() {
		return "Use larger multiline text box for custom penalties";
	}

	default String settingsTextWrapping() {
		return "Allow text-wrapping for multiline text boxes";
	}

	default String settingsDescription() {
		return "Set preferences for Artemis";
	}

	default String settingsAdvancedDescription() {
		return "Set advanced settings for Artemis";
	}

	default String settingsLanguage() {
		return "Language: ";
	}

	default String settingsLanguageHint() {
		return "Note: Changing the language might require a restart of the IDE!";
	}

	default String settingsPassword() {
		return "Artemis Password: ";
	}

	default String settingsUrl() {
		return "Artemis URL: ";
	}

	default String settingsUsername() {
		return "Artemis Username: ";
	}

	default String settingsConfigPath() {
		return "Grading Config Path: ";
	}

	default String settingsGitToken() {
		return "Git Token (optional): ";
	}

	default String settingsTweakEclipsePreferences() {
		return "Tweak Eclipse Preferences on startup";
	}

	default String settingsAmountOfGradingButtonsInOneRow() {
		return "Amount of Grading Buttons in one row";
	}

	default String tabAssessment() {
		return "Assessment";
	}

	/**
	 * @param i the number of the correction round
	 * @return the correction-round message with the correct number
	 */
	default String tabAssessmentStartCorrectionRound(int i) {
		return String.format("Start Correction Round %d", i);
	}

	/**
	 * @param started   the amount of started submissions
	 * @param submitted the amount of submitted submissions
	 * @return the started/submitted message with the correct amounts
	 */
	default String tabAssessmentStartedSubmitted(int started, int submitted) {
		return String.format("Started submissions: %d  Submitted: %d", started, submitted);
	}

	default String tabBacklog() {
		return "Backlog";
	}

	default String tabBacklogFilter() {
		return "Filter Selection";
	}

	default String tabBacklogRefresh() {
		return "Refresh Submissions";
	}

	default String tabGrading() {
		return "Grading";
	}

	default String tabResults() {
		return "Test Results";
	}

	default String tabResultsDescription() {
		return "Summary of the results of the currently selected exercise";
	}

	default String tabResultsDetailedText() {
		return "Detailed Text";
	}

	default String tabResultsLatest() {
		return "Latest Results";
	}

	default String tabResultsLatestResultsFromArtemis() {
		return "Latest Results from Artemis";
	}

	default String tabResultsSummary() {
		return "Summary of all visible tests";
	}

	default String tabResultsTutorComment() {
		return "Tutor Comment";
	}

	/**
	 * @param name the name of the exercise
	 * @return the clean-message including the name of the exercise
	 */
	default String tabStudentClean(String name) {
		return String.format("Clean: %s", name);
	}

	default String tabStudentCleanLastChanges() {
		return "Clean your last changes";
	}

	default String tabStudentCleanImpossible() {
		return "The exercise can not be cleaned!";
	}

	default String tabStudentEndExamInArtemis() {
		return "<a>Click Here to access Artemis to end your Exam</a>";
	}

	default String tabStudentNoSelection() {
		return "*NOTHING SELECTED*";
	}

	/**
	 * @param name the name of the exercise
	 * @return the reset-message including the name of the exercise
	 */
	default String tabStudentReset(String name) {
		return String.format("Reset: %s", name);
	}

	default String tabStudentResetToRemote() {
		return "Reset exercise to remote state";
	}

	default String tabStudentStartExercise() {
		return "Start exercise";
	}

	default String tabStudentStartExercises() {
		return "Start exercises";
	}

	/**
	 * @param name the name of the exercise
	 * @return the submit-message including the name of the exercise
	 */
	default String tabStudentSubmit(String name) {
		return String.format("Submit: %s", name);
	}

	default String tabStudentSubmitSolution() {
		return "Submit your solution";
	}

	default String tabStudent() {
		return "Exercise";
	}

	default String tabStudentExerciseExpired() {
		return "The exercise is expired and can therefore not be submitted!";
	}

	default String tabStudentExerciseStartTime() {
		return "Starts at:";
	}

	default String tabStudentExerciseEndTime() {
		return "Due to:";
	}

	default String none() {
		return "None";
	}

	default String ended() {
		return "ended";
	}

	default String notEnded() {
		return "not ended";
	}

	default String finished() {
		return "finished";
	}

	default String course() {
		return "Course";
	}

	default String exam() {
		return "Exam";
	}

	default String exercise() {
		return "Exercise";
	}

	default String reload() {
		return "Reload";
	}

	default String save() {
		return "Save";
	}

	default String submit() {
		return "Submit";
	}

	default String submissions() {
		return "Submissions";
	}

	default String credits() {
		return "Credits";
	}

	default String name() {
		return "Name";
	}

	default String points() {
		return "Points";
	}

	default String score() {
		return "Score";
	}

	default String success() {
		return "Success";
	}

	default String successful() {
		return "Successful";
	}

	default String unsuccessful() {
		return "Unsuccessful";
	}

	default String unknownTask() {
		return "Unknown Task";
	}

	default String tests() {
		return "Test(s)";
	}

	default String detailText() {
		return "Detail Text";
	}

	default String metaInformation() {
		return "Meta Information";
	}

	default String statistics() {
		return "Statistics: ";
	}

	//
	// internal
	//

	/**
	 * This method defines the display-name for the current language (default:
	 * English)
	 *
	 * @return the display-name of the language
	 */
	default String languageDisplayName() {
		return "English";
	}
}
