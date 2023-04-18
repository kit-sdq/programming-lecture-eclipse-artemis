/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.view.languages;

/**
 * This class provides German translations for all UI-Elements
 *
 * NOTE: This file MUST use UTF-8 encoding. Otherwise spotless will fail or
 * umlauts will break.
 *
 * @see I18N
 */
class GermanLanguage implements I18N {

	@Override
	public String settingsLargeTextBox() {
		return "Benutze mehrzeilige Textbox für benutzerdefinierte Abzüge";
	}

	@Override
	public String settingsTextWrapping() {
		return "Automatischen Zeilenumbruch in mehrzeiliger Textbox aktivieren";
	}

	@Override
	public String settingsDescription() {
		return "Einstellungen für Artemis";
	}

	@Override
	public String settingsAdvancedDescription() {
		return "Erweiterte Einstellungen für Artemis";
	}

	@Override
	public String settingsLanguage() {
		return "Sprache: ";
	}

	@Override
	public String settingsLanguageHint() {
		return "Hinweis: Die Änderung der Sprache benötigt einen Neustart der IDE!";
	}

	@Override
	public String settingsPassword() {
		return "Artemis Passwort: ";
	}

	@Override
	public String settingsUrl() {
		return "Artemis URL: ";
	}

	@Override
	public String settingsUsername() {
		return "Artemis Benutzername: ";
	}

	@Override
	public String settingsConfigPath() {
		return "Bewertungs-Konfigurations-Datei: ";
	}

	@Override
	public String settingsGitToken() {
		return "Git Token (optional): ";
	}

	@Override
	public String settingsTweakEclipsePreferences() {
		return "Eclipse-Einstellungen beim Start anpassen";
	}

	@Override
	public String settingsAmountOfGradingButtonsInOneRow() {
		return "Anzahl der Bewertungs-Knöpfe pro Zeile";
	}

	@Override
	public String tabAssessment() {
		return "Bewertung";
	}

	@Override
	public String tabAssessmentStartGradingRound(int i) {
		return String.format("Korrekturrunde %d beginnen", i);
	}

	@Override
	public String tabAssessmentStartedSubmitted(int totalAssessments, int totalSubmissions, int locked, int submittedByTutor) {
		return String.format("Einreichungen: %d/%d  Gesperrt: %d  Deine Bewertungen: %d", totalAssessments, totalSubmissions, locked, submittedByTutor);
	}

	@Override
	public String backlog() {
		return "Backlog";
	}

	@Override
	public String submission() {
		return "Abgabe";
	}

	@Override
	public String backlogRefresh() {
		return "Abgaben neu laden";
	}

	@Override
	public String closeAssessment() {
		return "Korrektur schließen";
	}

	@Override
	public String tabGrading() {
		return "Bewerten";
	}

	@Override
	public String tabResults() {
		return "Ergebnisse";
	}

	@Override
	public String tabResultsDescription() {
		return "Zusammenfassung der Ergebnisse der aktuell ausgewählten Aufgabe";
	}

	@Override
	public String tabResultsDetailedText() {
		return "Detailierter Text";
	}

	@Override
	public String tabResultsLatest() {
		return "Neuste Ergebnisse";
	}

	@Override
	public String tabResultsLatestResultsFromArtemis() {
		return "Neuste Ergebnisse von Artemis";
	}

	@Override
	public String tabResultsSummary() {
		return "Zusammenfassung aller sichtbaren Tests:";
	}

	@Override
	public String tabResultsTutorComment() {
		return "Tutor-Kommentar";
	}

	@Override
	public String none() {
		return "None";
	}

	@Override
	public String ended() {
		return "beendet";
	}

	@Override
	public String notEnded() {
		return "nicht beendet";
	}

	@Override
	public String finished() {
		return "fertig";
	}

	@Override
	public String course() {
		return "Kurs";
	}

	@Override
	public String exam() {
		return "Klausur";
	}

	@Override
	public String exercise() {
		return "Aufgabe";
	}

	@Override
	public String reloadAssessment() {
		return "Bewertung neuladen";
	}

	@Override
	public String saveAssessment() {
		return "Bewertung speichern";
	}

	@Override
	public String submitAssessment() {
		return "Bewertung abgeben";
	}

	@Override
	public String submissions() {
		return "Abgaben";
	}

	@Override
	public String credits() {
		return "Credits";
	}

	@Override
	public String name() {
		return "Name";
	}

	@Override
	public String points() {
		return "Punkte";
	}

	@Override
	public String score() {
		return "Punktestand";
	}

	@Override
	public String success() {
		return "Erfolg";
	}

	@Override
	public String successful() {
		return "erfolgreich";
	}

	@Override
	public String unsuccessful() {
		return "nicht erfolgreich";
	}

	@Override
	public String unknownTask() {
		return "Unbekannte Aufgabe";
	}

	@Override
	public String tests() {
		return "Test(s)";
	}

	@Override
	public String detailText() {
		return "Detailierter Text";
	}

	@Override
	public String metaInformation() {
		return "Meta-Informationen";
	}

	@Override
	public String statistics() {
		return "Statistiken";
	}

	@Override
	public String resetPluginState() {
		return "Plugin zurücksetzen";
	}

	@Override
	public String settingsSearchInMistakeMessages() {
		return "Button-Beschreibungen in die Suche einbeziehen";
	}

	@Override
	public String settingsOpenFilesOnAssessmentStart() {
		return "Dateien automatisch öffnen";
	}

	@Override
	public String settingsOpenFilesOnAssessmentStartNone() {
		return "Keine";
	}

	@Override
	public String settingsOpenFilesOnAssessmentStartMain() {
		return "Main-Klasse";
	}

	@Override
	public String settingsOpenFilesOnAssessmentStartAll() {
		return "Alle Typen";
	}

	@Override
	public String general() {
		return "Allgemein";
	}

	@Override
	public String assessment() {
		return "Bewertung";
	}

	@Override
	public String languageDisplayName() {
		return "Deutsch";
	}
}
