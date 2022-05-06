/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.kit.kastel.eclipse.common.view.activator.Activator;
import edu.kit.kastel.sdq.eclipse.common.api.PreferenceConstants;

public class AdvancedPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public AdvancedPreferences() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
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

		this.addField(absoluteConfigPath);
		this.addField(gitToken);
		this.addField(userPrefersLargePenaltyText);
		this.addField(userPrefersTextWrappingInPenaltyText);

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