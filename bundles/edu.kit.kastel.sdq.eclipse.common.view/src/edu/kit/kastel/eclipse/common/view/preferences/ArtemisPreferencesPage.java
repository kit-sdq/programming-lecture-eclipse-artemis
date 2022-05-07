/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.kit.kastel.eclipse.common.view.activator.CommonActivator;
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

public class ArtemisPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ArtemisPreferencesPage() {
		super(FieldEditorPreferencePage.GRID);
		this.setPreferenceStore(CommonActivator.getDefault().getPreferenceStore());
		this.setDescription("Set preferences for the Artemis");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		var parent = this.getFieldEditorParent();

		var artemisUrl = new StringFieldEditor(PreferenceConstants.ARTEMIS_URL, "Artemis URL: ", parent);
		var artemisUser = new StringFieldEditor(PreferenceConstants.ARTEMIS_USER, "Artemis Username: ", parent);

		var artemisPassword = new StringFieldEditor(PreferenceConstants.ARTEMIS_PASSWORD, "Artemis Password: ", parent);
		artemisPassword.getTextControl(this.getFieldEditorParent()).setEchoChar('*');

		this.addField(artemisUrl);
		this.addField(artemisUser);
		this.addField(artemisPassword);
	}

	@Override
	public void init(IWorkbench workbench) {
		// NOP
	}
}