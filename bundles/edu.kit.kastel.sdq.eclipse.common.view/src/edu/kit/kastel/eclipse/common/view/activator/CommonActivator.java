/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.activator;

import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.osgi.framework.BundleContext;

import edu.kit.kastel.eclipse.common.view.languages.LanguageSettings;
import edu.kit.kastel.sdq.eclipse.common.api.PreferenceConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class CommonActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "edu.kit.kastel.sdq.eclipse.artemis.common-ui";

	// The shared instance
	private static CommonActivator plugin;

	/**
	 * The constructor
	 */
	public CommonActivator() {
		// NOP
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		this.tweakPreferences();
		LanguageSettings.updateI18N();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CommonActivator getDefault() {
		return plugin;
	}

	private void tweakPreferences() {
		var preferences = this.getPreferenceStore();
		if (!preferences.getBoolean(PreferenceConstants.OVERRIDE_DEFAULT_PREFERENCES)) {
			return;
		}

		// Enable Line Numbers by default
		EditorsUI.getPreferenceStore().setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER, true);
	}

}
