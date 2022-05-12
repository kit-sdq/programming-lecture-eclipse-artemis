/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.languages;

public interface I18N {
	//
	// Translations
	//

	public default String settingsLargeTextBox() {
		return "Use larger multiline text box for custom penalties";
	}

	public default String settingsTextWrapping() {
		return "Allow text-wrapping for multiline text boxes";
	}

	public default String settingsDescription() {
		return "Set preferences for Artemis Grading";
	}

	public default String settingsAdvancedDescription() {
		return "Set advanced settings for Artemis Grading";
	}

	public default String settingsLanguage() {
		return "Language: ";
	}

	public default String settingsLanguageHint() {
		return "Note: Changing the language might require a restart of the IDE!";
	}

	public default String settingsPassword() {
		return "Artemis Password: ";
	}

	public default String settingsUrl() {
		return "Artemis URL: ";
	}

	public default String settingsUsername() {
		return "Artemis Username: ";
	}

	public default String settingsConfigPath() {
		return "Grading Config Path: ";
	}

	public default String settingsGitToken() {
		return "Git Token (optional): ";
	}

	public default String settingsTweakEclipsePreferences() {
		return "Tweak Eclipse Preferences on startup";
	}

	public default String settingsAmountOfGradingButtonsInOneRow() {
		return "Amount of Grading Buttons in one row";
	}

	public default String tabAssessment() {
		return "Assessment";
	}

	public default String tabAssessmentStartCorrectionRound(int i) {
		return String.format("Start Correction Round %d", i);
	}

	public default String tabAssessmentStartedSubmitted(int started, int submitted) {
		return String.format("Started submissions: %d  Submitted: %d", started, submitted);
	}

	public default String tabBacklog() {
		return "Backlog";
	}

	public default String tabBacklogFilter() {
		return "Filter Selection";
	}

	public default String tabBacklogRefresh() {
		return "Refresh Submissions";
	}

	public default String tabGrading() {
		return "Grading";
	}

	public default String tabResults() {
		return "Test Results";
	}

	public default String tabResultsDescription() {
		return "Summary of the results of the currently selected exercise";
	}

	public default String tabResultsDetailedText() {
		return "Detailed Text";
	}

	public default String tabResultsLatest() {
		return "Latest Results";
	}

	public default String tabResultsLatestResultsFromArtemis() {
		return "Latest Results from Artemis";
	}

	public default String tabResultsSummary() {
		return "Summary of all visible tests";
	}

	public default String tabResultsTutorComment() {
		return "Tutor Comment";
	}

	public default String tabStudentClean(String name) {
		return String.format("Clean: %s", name);
	}

	public default String tabStudentCleanLastChanges() {
		return "Clean your last changes";
	}

	public default String tabStudentCleanImpossible() {
		return "The exercise can not be cleaned!";
	}

	public default String tabStudentEndExamInArtemis() {
		return "<a>Click Here to access Artemis to end your Exam</a>";
	}

	public default String tabStudentNoSelection() {
		return "*NOTHING SELECTED*";
	}

	public default String tabStudentReset(String name) {
		return String.format("Reset: %s", name);
	}

	public default String tabStudentResetToRemote() {
		return "Reset exercise to remote state";
	}

	public default String tabStudentStartExercise() {
		return "Start exercise";
	}

	public default String tabStudentStartExercises() {
		return "Start exercises";
	}

	public default String tabStudentSubmit(String name) {
		return String.format("Submit: %s", name);
	}

	public default String tabStudentSubmitSolution() {
		return "Submit your solution";
	}

	public default String tabStudent() {
		return "Exercise";
	}

	public default String tabStudentExerciseExpired() {
		return "The exercise is expired and can therefore not be submitted!";
	}

	public default String tabStudentExerciseStartTime() {
		return "Starts at:";
	}

	public default String tabStudentExerciseEndTime() {
		return "Due to:";
	}

	public default String none() {
		return "None";
	}

	public default String ended() {
		return "ended";
	}

	public default String notEnded() {
		return "not ended";
	}

	public default String finished() {
		return "finished";
	}

	public default String course() {
		return "Course";
	}

	public default String exam() {
		return "Exam";
	}

	public default String exercise() {
		return "Exercise";
	}

	public default String reload() {
		return "Reload";
	}

	public default String save() {
		return "Save";
	}

	public default String submit() {
		return "Submit";
	}

	public default String submissions() {
		return "Submissions";
	}

	public default String credits() {
		return "Credits";
	}

	public default String name() {
		return "Name";
	}

	public default String points() {
		return "Points";
	}

	public default String score() {
		return "Score";
	}

	public default String success() {
		return "Success";
	}

	public default String successful() {
		return "Successful";
	}

	public default String unsuccessful() {
		return "Unsuccessful";
	}

	public default String unknownTask() {
		return "Unknown Task";
	}

	public default String tests() {
		return "Test(s)";
	}

	public default String detailText() {
		return "Detail Text";
	}

	//
	// internal
	//

	public default String languageDisplayName() {
		return "English";
	}

	/**
	 * Used to determine which language should be used as the default. Only
	 * {@link DefaultLanguage} is allowed to return <code>true</code>
	 * 
	 * @return true, iff the current language is the default.
	 */
	boolean isDefault();

}
