/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.activator;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.kit.kastel.eclipse.common.api.controller.IGradingSystemwideController;
import edu.kit.kastel.eclipse.common.core.GradingSystemwideController;
import edu.kit.kastel.eclipse.common.view.activator.CommonActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "edu.kit.kastel.sdq.eclipse.artemis.grading-ui";

	// The shared instance
	private static Activator plugin;

	private IGradingSystemwideController systemwideController;

	/**
	 * The constructor
	 */
	public Activator() {
		// NOP
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
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
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Creates a new instance of the SystemWideController
	 */
	public IGradingSystemwideController createNewSystemwideController() {
		this.systemwideController = new GradingSystemwideController(CommonActivator.getDefault().getPreferenceStore());
		return this.systemwideController;
	}

	public IGradingSystemwideController getSystemwideController() {
		return this.systemwideController;
	}

}
