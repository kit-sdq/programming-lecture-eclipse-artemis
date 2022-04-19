/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.kit.kastel.eclipse.grading.view.activator.Activator;
import edu.kit.kastel.sdq.eclipse.common.api.PreferenceConstants;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 * <p>
 */

public class ArtemisGradingPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private BooleanFieldEditor isRelativeConfigPath;
	private StringFieldEditor relativeConfigPath;
	private FileFieldEditor absoluteConfigPath;

	public ArtemisGradingPreferencesPage() {
		super(FieldEditorPreferencePage.GRID);
		this.setPreferenceStore(Activator.getDefault().getPreferenceStore());
		this.setDescription("Set preferences for the Artemis Grading");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		var parent = this.getFieldEditorParent();

		absoluteConfigPath = new FileFieldEditor(PreferenceConstants.ABSOLUTE_CONFIG_PATH, "Absolute config path: ", parent);

		isRelativeConfigPath = new BooleanFieldEditor(PreferenceConstants.IS_RELATIVE_CONFIG_PATH, "Use relative config path", parent);

		relativeConfigPath = new StringFieldEditor(PreferenceConstants.RELATIVE_CONFIG_PATH, "Relative config path: ", parent);

		var artemisUrl = new StringFieldEditor(PreferenceConstants.ARTEMIS_URL, "Artemis URL: ", parent);
		var artemisUser = new StringFieldEditor(PreferenceConstants.ARTEMIS_USER, "Artemis username: ", parent);

		var artemisPassword = new StringFieldEditor(PreferenceConstants.ARTEMIS_PASSWORD, "Artemis password: ", parent);
		artemisPassword.getTextControl(this.getFieldEditorParent()).setEchoChar('*');

		var gitToken = new StringFieldEditor(PreferenceConstants.GIT_TOKEN, "Git Token (optional): ", parent);
		gitToken.getTextControl(this.getFieldEditorParent()).setEchoChar('*');

		var userPrefersLargePenaltyText = new BooleanFieldEditor(PreferenceConstants.PREFERS_LARGE_PENALTY_TEXT_PATH,
				"Use larger multi-line-text-box for custom penaltys", parent);
		var userPrefersTextWrappingInPenaltyText = new BooleanFieldEditor(PreferenceConstants.PREFERS_TEXT_WRAPPING_IN_PENALTY_TEXT_PATH,
				"Allow text-wrapping in multiline-text-box", parent);

		this.addField(absoluteConfigPath);
		this.addField(relativeConfigPath);
		this.addField(isRelativeConfigPath);
		this.addField(artemisUrl);
		this.addField(artemisUser);
		this.addField(artemisPassword);
		this.addField(gitToken);
		this.addField(userPrefersLargePenaltyText);
		this.addField(userPrefersTextWrappingInPenaltyText);

	}

	@Override
	public void init(IWorkbench workbench) {
		// NOP
	}

	@Override
	protected void initialize() {
		super.initialize();
		this.isRelativeConfigPath.setPropertyChangeListener(event -> {
			final boolean isRelative = (Boolean) event.getNewValue();
			if (isRelative) {
				this.relativeConfigPath.setEnabled(true, this.getFieldEditorParent());
				this.absoluteConfigPath.setEnabled(false, this.getFieldEditorParent());
			} else {
				this.relativeConfigPath.setEnabled(false, this.getFieldEditorParent());
				this.absoluteConfigPath.setEnabled(true, this.getFieldEditorParent());
			}
		});

		final boolean isRelativeSelected = this.isRelativeConfigPath.getBooleanValue();
		if (isRelativeSelected) {
			this.relativeConfigPath.setEnabled(true, this.getFieldEditorParent());
			this.absoluteConfigPath.setEnabled(false, this.getFieldEditorParent());
		} else {
			this.relativeConfigPath.setEnabled(false, this.getFieldEditorParent());
			this.absoluteConfigPath.setEnabled(true, this.getFieldEditorParent());
		}

	}

}