/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.kit.kastel.eclipse.common.view.activator.CommonActivator;
import edu.kit.kastel.sdq.eclipse.common.api.PreferenceConstants;

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

		var gitToken = new StringFieldEditor(PreferenceConstants.GIT_TOKEN, "Git Token (optional): ", parent);
		gitToken.getTextControl(this.getFieldEditorParent()).setEchoChar('*');

		var absoluteConfigPath = new FileFieldEditor(PreferenceConstants.ABSOLUTE_CONFIG_PATH, "Grading Config Path: ", parent);

		var userPrefersLargePenaltyText = new BooleanFieldEditor(PreferenceConstants.PREFERS_LARGE_PENALTY_TEXT_PATH,
				"Use larger multi-line-text-box for custom penaltys", parent);
		var userPrefersTextWrappingInPenaltyText = new BooleanFieldEditor(PreferenceConstants.PREFERS_TEXT_WRAPPING_IN_PENALTY_TEXT_PATH,
				"Allow text-wrapping in multiline-text-box", parent);

		var overrideDefaultPreferences = new BooleanFieldEditor(PreferenceConstants.OVERRIDE_DEFAULT_PREFERENCES, "Tweak Eclipse Preferences on startup",
				parent);

		var columnsForGradingButtons = new IntegerFieldEditor(PreferenceConstants.GRADING_BUTTONS_IN_COLUMN, "Amount of Grading Buttons in one row", parent);
		columnsForGradingButtons.setEmptyStringAllowed(false);
		columnsForGradingButtons.setValidRange(1, 10);

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