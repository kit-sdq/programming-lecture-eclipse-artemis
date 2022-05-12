/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.preferences;

import static edu.kit.kastel.eclipse.common.view.languages.LanguageSettings.I18N;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.kit.kastel.eclipse.common.view.activator.CommonActivator;
import edu.kit.kastel.eclipse.common.view.languages.LanguageSettings;
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

	private ComboFieldEditor languageSelector;

	public ArtemisPreferencesPage() {
		super(FieldEditorPreferencePage.GRID);
		this.setPreferenceStore(CommonActivator.getDefault().getPreferenceStore());
		this.setDescription(I18N().settingsDescription());
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		var parent = this.getFieldEditorParent();

		var artemisUrl = new StringFieldEditor(I18N().settingsUrl(), I18N().settingsUrl(), parent);
		var artemisUser = new StringFieldEditor(I18N().settingsUsername(), I18N().settingsUsername(), parent);
		var artemisPassword = new StringFieldEditor(I18N().settingsPassword(), I18N().settingsPassword(), parent);

		artemisPassword.getTextControl(this.getFieldEditorParent()).setEchoChar('*');

		this.languageSelector = new ComboFieldEditor(PreferenceConstants.PREFERRED_LANGUAGE_PATH, I18N().settingsLanguage(),
				LanguageSettings.getAvailableLocalesForComboField(), this.getFieldEditorParent());

		this.addField(artemisUrl);
		this.addField(artemisUser);
		this.addField(artemisPassword);

		this.addField(languageSelector);

		Label hint = this.createDescriptionLabel(this.getFieldEditorParent());
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		hint.setLayoutData(gd);
		hint.setText(I18N().settingsLanguageHint());
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
				LanguageSettings.updateI18N();
			}
		});
	}

}