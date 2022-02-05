package edu.kit.kastel.eclipse.common.view.activator;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.kit.kastel.sdq.eclipse.grading.api.controller.IGradingSystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.core.GradingSystemwideController;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "plugin_common_gui"; //$NON-NLS-1$

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
	public void createSystemWideController() {
		this.systemwideController = new GradingSystemwideController(this.getPreferenceStore());
	}

	public IGradingSystemwideController getSystemwideController() {
		return this.systemwideController;
	}

}
