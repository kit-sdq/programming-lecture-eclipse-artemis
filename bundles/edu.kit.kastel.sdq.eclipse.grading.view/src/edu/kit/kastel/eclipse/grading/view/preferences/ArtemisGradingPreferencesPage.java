package edu.kit.kastel.eclipse.grading.view.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.kit.kastel.eclipse.grading.view.activator.Activator;
import edu.kit.kastel.sdq.eclipse.grading.api.PreferenceConstants;

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
	private BooleanFieldEditor userPreferresLargePenaltyText;

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

		this.absoluteConfigPath = new FileFieldEditor(PreferenceConstants.ABSOLUTE_CONFIG_PATH, "Absolute config path: ", this.getFieldEditorParent());

		this.isRelativeConfigPath = new BooleanFieldEditor(PreferenceConstants.IS_RELATIVE_CONFIG_PATH, "Use relative config path",
				this.getFieldEditorParent());

		this.relativeConfigPath = new StringFieldEditor(PreferenceConstants.RELATIVE_CONFIG_PATH, "Relative config path: ", this.getFieldEditorParent());

		StringFieldEditor artemisUrl = new StringFieldEditor(PreferenceConstants.ARTEMIS_URL, "Artemis URL: ", this.getFieldEditorParent());

		StringFieldEditor artemisUser = new StringFieldEditor(PreferenceConstants.ARTEMIS_USER, "Artemis username: ", this.getFieldEditorParent());

		StringFieldEditor artemisPassword = new StringFieldEditor(PreferenceConstants.ARTEMIS_PASSWORD, "Artemis password: ", this.getFieldEditorParent());

		artemisPassword.getTextControl(this.getFieldEditorParent()).setEchoChar('*');
		
		this.userPreferresLargePenaltyText = new BooleanFieldEditor(PreferenceConstants.PREFFERES_LARGE_PENALTY_TEXT_PATH, "Use large text-box for custom penaltys (note: when enabled, you need to click \"ok\". Pressing enter won't add the penalty.)", this.getFieldEditorParent());

		this.addField(this.absoluteConfigPath);
		this.addField(this.relativeConfigPath);
		this.addField(this.isRelativeConfigPath);
		this.addField(artemisUrl);
		this.addField(artemisUser);
		this.addField(artemisPassword);
		this.addField(this.userPreferresLargePenaltyText);

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