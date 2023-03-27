/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.view.preferences;

import static edu.kit.kastel.eclipse.common.view.languages.LanguageSettings.I18N;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.kit.kastel.eclipse.common.api.PreferenceConstants;
import edu.kit.kastel.eclipse.common.view.activator.CommonActivator;

public class AdvancedPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public AdvancedPreferences() {
		super(GRID);
		setPreferenceStore(CommonActivator.getDefault().getPreferenceStore());
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		var parent = this.getFieldEditorParent();

		var artemisUser = new StringFieldEditor(PreferenceConstants.GENERAL_ADVANCED_ARTEMIS_USER, I18N().settingsUsername(), parent);
		var artemisPassword = new StringFieldEditor(PreferenceConstants.GENERAL_ADVANCED_ARTEMIS_PASSWORD, I18N().settingsPassword(), parent);
		artemisPassword.getTextControl(this.getFieldEditorParent()).setEchoChar('*');

		var gitToken = new StringFieldEditor(PreferenceConstants.GENERAL_ADVANCED_GIT_TOKEN, I18N().settingsGitToken(), parent);
		gitToken.getTextControl(this.getFieldEditorParent()).setEchoChar('*');

		var userPrefersLargePenaltyText = new BooleanFieldEditor(PreferenceConstants.GRADING_VIEW_PREFERS_LARGE_PENALTY_TEXT_PATH,
				I18N().settingsLargeTextBox(), parent);
		var userPrefersTextWrappingInPenaltyText = new BooleanFieldEditor(PreferenceConstants.GRADING_VIEW_PREFERS_TEXT_WRAPPING_IN_PENALTY_TEXT_PATH,
				I18N().settingsTextWrapping(), parent);

		var overrideDefaultPreferences = new BooleanFieldEditor(PreferenceConstants.GENERAL_OVERRIDE_DEFAULT_PREFERENCES,
				I18N().settingsTweakEclipsePreferences(), parent);

		var searchInMistakeMessages = new BooleanFieldEditor(PreferenceConstants.SEARCH_IN_MISTAKE_MESSAGES, I18N().settingsSearchInMistakeMessages(), parent);

		var columnsForGradingButtons = new IntegerFieldEditor(PreferenceConstants.GRADING_VIEW_BUTTONS_IN_COLUMN,
				I18N().settingsAmountOfGradingButtonsInOneRow(), parent);
		columnsForGradingButtons.setEmptyStringAllowed(false);
		columnsForGradingButtons.setValidRange(1, 10);

		var openFiles = new ComboFieldEditor(PreferenceConstants.OPEN_FILES_ON_ASSESSMENT_START, I18N().settingsOpenFilesOnAssessmentStart(),
				new String[][] { { I18N().settingsOpenFilesOnAssessmentStartNone(), PreferenceConstants.OPEN_FILES_ON_ASSESSMENT_START_NONE },
						{ I18N().settingsOpenFilesOnAssessmentStartMain(), PreferenceConstants.OPEN_FILES_ON_ASSESSMENT_START_MAIN },
						{ I18N().settingsOpenFilesOnAssessmentStartAll(), PreferenceConstants.OPEN_FILES_ON_ASSESSMENT_START_ALL } },
				parent);

		var autograderJarPath = new FileFieldEditor(PreferenceConstants.AUTOGRADER_JAR_PATH, "Autograder JAR file", parent);
		var autograderConfigPath = new FileFieldEditor(PreferenceConstants.AUTOGRADER_CONFIG_PATH, "Autograder config file", parent);

		this.addField(artemisUser);
		this.addField(artemisPassword);
		this.addField(gitToken);

		this.addField(columnsForGradingButtons);
		this.addField(userPrefersLargePenaltyText);
		this.addField(userPrefersTextWrappingInPenaltyText);
		this.addField(overrideDefaultPreferences);
		this.addField(searchInMistakeMessages);
		this.addField(openFiles);

		this.addField(autograderJarPath);
		this.addField(autograderConfigPath);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
	}

}