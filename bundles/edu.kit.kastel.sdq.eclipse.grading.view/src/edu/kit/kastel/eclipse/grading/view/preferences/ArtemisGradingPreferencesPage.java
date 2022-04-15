/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.preferences;

import java.util.Objects;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.kit.kastel.eclipse.common.view.utilities.ResourceBundleProvider;
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
	private BooleanFieldEditor userPrefersLargePenaltyText;
	private BooleanFieldEditor userPrefersTextWrappingInPenaltyText;
	private ComboFieldEditor languageSelector;

	public ArtemisGradingPreferencesPage() {
		super(FieldEditorPreferencePage.GRID);
		this.setPreferenceStore(Activator.getDefault().getPreferenceStore());
		this.setDescription(ResourceBundleProvider.getResourceBundle().getString("settings.grading.description"));
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		this.absoluteConfigPath = new FileFieldEditor(PreferenceConstants.ABSOLUTE_CONFIG_PATH,
				ResourceBundleProvider.getResourceBundle().getString("settings.grading.absoluteConfigPath") + " ", this.getFieldEditorParent());

		this.isRelativeConfigPath = new BooleanFieldEditor(PreferenceConstants.IS_RELATIVE_CONFIG_PATH,
				ResourceBundleProvider.getResourceBundle().getString("settings.grading.useRelativeConfig"), this.getFieldEditorParent());

		this.relativeConfigPath = new StringFieldEditor(PreferenceConstants.RELATIVE_CONFIG_PATH,
				ResourceBundleProvider.getResourceBundle().getString("settings.grading.relativeConfigPath") + " ", this.getFieldEditorParent());

		StringFieldEditor artemisUrl = new StringFieldEditor(PreferenceConstants.ARTEMIS_URL,
				ResourceBundleProvider.getResourceBundle().getString("settings.url") + " ", this.getFieldEditorParent());

		StringFieldEditor artemisUser = new StringFieldEditor(PreferenceConstants.ARTEMIS_USER,
				ResourceBundleProvider.getResourceBundle().getString("settings.username") + " ", this.getFieldEditorParent());

		StringFieldEditor artemisPassword = new StringFieldEditor(PreferenceConstants.ARTEMIS_PASSWORD,
				ResourceBundleProvider.getResourceBundle().getString("settings.password") + " ", this.getFieldEditorParent());

		artemisPassword.getTextControl(this.getFieldEditorParent()).setEchoChar('*');

		this.userPrefersLargePenaltyText = new BooleanFieldEditor(PreferenceConstants.PREFERS_LARGE_PENALTY_TEXT_PATH,
				ResourceBundleProvider.getResourceBundle().getString("settings.grading.largeTextBox"), this.getFieldEditorParent());
		this.userPrefersTextWrappingInPenaltyText = new BooleanFieldEditor(PreferenceConstants.PREFERS_TEXT_WRAPPING_IN_PENALTY_TEXT_PATH,
				ResourceBundleProvider.getResourceBundle().getString("settings.grading.allowTextWrapping"), this.getFieldEditorParent());

		// Load value from common view
		this.getPreferenceStore().setValue(PreferenceConstants.PREFERRED_LANGUAGE_PATH, edu.kit.kastel.eclipse.common.view.activator.Activator.getDefault()
				.getPreferenceStore().getString(PreferenceConstants.PREFERRED_LANGUAGE_PATH));
		this.languageSelector = new ComboFieldEditor(PreferenceConstants.PREFERRED_LANGUAGE_PATH,
				ResourceBundleProvider.getResourceBundle().getString("settings.language"), new String[][] { { "Deutsch", "de_DE" }, { "Englisch", "en_US" } },
				this.getFieldEditorParent());

		this.addField(this.absoluteConfigPath);
		this.addField(this.relativeConfigPath);
		this.addField(this.isRelativeConfigPath);
		this.addField(artemisUrl);
		this.addField(artemisUser);
		this.addField(artemisPassword);
		this.addField(this.userPrefersLargePenaltyText);
		this.addField(this.userPrefersTextWrappingInPenaltyText);

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