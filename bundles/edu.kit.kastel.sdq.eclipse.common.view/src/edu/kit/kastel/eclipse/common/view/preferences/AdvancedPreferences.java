/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.kit.kastel.eclipse.common.api.PreferenceConstants;
import edu.kit.kastel.eclipse.common.view.activator.CommonActivator;

import static edu.kit.kastel.eclipse.common.view.languages.LanguageSettings.I18N;

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

		var absoluteConfigPath = new FileFieldEditor(PreferenceConstants.GRADING_ABSOLUTE_CONFIG_PATH, I18N().settingsConfigPath(), parent);

		var userPrefersLargePenaltyText = new BooleanFieldEditor(PreferenceConstants.GRADING_VIEW_PREFERS_LARGE_PENALTY_TEXT_PATH,
				I18N().settingsLargeTextBox(), parent);
		var userPrefersTextWrappingInPenaltyText = new BooleanFieldEditor(PreferenceConstants.GRADING_VIEW_PREFERS_TEXT_WRAPPING_IN_PENALTY_TEXT_PATH,
				I18N().settingsTextWrapping(), parent);

		var overrideDefaultPreferences = new BooleanFieldEditor(PreferenceConstants.GENERAL_OVERRIDE_DEFAULT_PREFERENCES,
				I18N().settingsTweakEclipsePreferences(), parent);

		var columnsForGradingButtons = new IntegerFieldEditor(PreferenceConstants.GRADING_VIEW_BUTTONS_IN_COLUMN,
				I18N().settingsAmountOfGradingButtonsInOneRow(), parent);
		columnsForGradingButtons.setEmptyStringAllowed(false);
		columnsForGradingButtons.setValidRange(1, 10);

		this.addField(artemisUser);
		this.addField(artemisPassword);
		this.addField(gitToken);

		this.addField(absoluteConfigPath);
		this.addField(columnsForGradingButtons);
		this.addField(userPrefersLargePenaltyText);
		this.addField(userPrefersTextWrappingInPenaltyText);
		this.addField(overrideDefaultPreferences);

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