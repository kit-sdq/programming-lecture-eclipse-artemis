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
		return "Language:";
	}

	public default String settingsLanguageHint() {
		return "Note: Changing the language might require a restart of the IDE!";
	}

	public default String settingsPassword() {
		return "Artemis password:";
	}

	public default String settingsUrl() {
		return "Artemis URL";
	}

	public default String settingsUsername() {
		return "Artemis username";
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

	public default String success() {
		return "Success";
	}

	public default String successful() {
		return "Successful";
	}

	public default String unsuccessful() {
		return "Unsuccessful";
	}

	//
	// internal
	//

	public default String languageDisplayName() {
		return "English";
	}

	boolean isDefault();

}
