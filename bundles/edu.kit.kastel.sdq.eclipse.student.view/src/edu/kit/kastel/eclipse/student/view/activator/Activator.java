/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.student.view.activator;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.kit.kastel.sdq.eclipse.common.api.controller.IStudentSystemwideController;
import edu.kit.kastel.sdq.eclipse.common.core.StudentSystemwideController;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "edu.kit.kastel.sdq.eclipse.artemis.student-ui";

	// The shared instance
	private static Activator plugin;

	private IStudentSystemwideController systemwideController;

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
	public IStudentSystemwideController createSystemWideController() {
		this.systemwideController = new StudentSystemwideController(this.getPreferenceStore());
		return this.systemwideController;
	}

	public IStudentSystemwideController getSystemwideController() {
		return this.systemwideController;
	}

}
