/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.student.view.preferences;

import java.util.Objects;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.kit.kastel.eclipse.common.view.utilities.ResourceBundleProvider;
import edu.kit.kastel.eclipse.student.view.activator.Activator;
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

public class ArtemisStudentPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private ComboFieldEditor languageSelector;

	public ArtemisStudentPreferencesPage() {
		super(FieldEditorPreferencePage.GRID);
		this.setPreferenceStore(Activator.getDefault().getPreferenceStore());
		this.setDescription(ResourceBundleProvider.getResourceBundle().getString("settings.student.description"));
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		StringFieldEditor artemisUrl = new StringFieldEditor(PreferenceConstants.ARTEMIS_URL,
				ResourceBundleProvider.getResourceBundle().getString("settings.url") + " ", this.getFieldEditorParent());
		StringFieldEditor artemisUser = new StringFieldEditor(PreferenceConstants.ARTEMIS_USER,
				ResourceBundleProvider.getResourceBundle().getString("settings.username") + " ", this.getFieldEditorParent());
		StringFieldEditor artemisPassword = new StringFieldEditor(PreferenceConstants.ARTEMIS_PASSWORD,
				ResourceBundleProvider.getResourceBundle().getString("settings.password") + " ", this.getFieldEditorParent());
		artemisPassword.getTextControl(this.getFieldEditorParent()).setEchoChar('*');

		// Load value from common view
		this.getPreferenceStore().setValue(PreferenceConstants.PREFERRED_LANGUAGE_PATH, edu.kit.kastel.eclipse.common.view.activator.Activator.getDefault()
				.getPreferenceStore().getString(PreferenceConstants.PREFERRED_LANGUAGE_PATH));
		this.languageSelector = new ComboFieldEditor(PreferenceConstants.PREFERRED_LANGUAGE_PATH,
				ResourceBundleProvider.getResourceBundle().getString("settings.language"), new String[][] { { "Deutsch", "de_DE" }, { "Englisch", "en_US" } },
				this.getFieldEditorParent());

		this.addField(artemisUrl);
		this.addField(artemisUser);
		this.addField(artemisPassword);

		this.addField(languageSelector);

		Label hint = this.createDescriptionLabel(this.getFieldEditorParent());
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		hint.setLayoutData(gd);
		hint.setText(ResourceBundleProvider.getResourceBundle().getString("settings.language.hint"));
	}

	@Override
	public void init(IWorkbench workbench) {
		// NOP
	}

	@Override
	protected void initialize() {
		super.initialize();

		this.languageSelector.setPropertyChangeListener(event -> {
			if (event.getProperty().equals(FieldEditor.VALUE)) {
				// The value of the FieldEditor changed
				String newLanguage = (String) Objects.requireNonNullElse(event.getNewValue(), "en_US");
				// This makes sure the common-view will always contain the newest value (hence
				// technically student and grading have own settings)
				edu.kit.kastel.eclipse.common.view.activator.Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.PREFERRED_LANGUAGE_PATH,
						newLanguage);
				ResourceBundleProvider.updateResourceBundle();
			}
		});
	}

}