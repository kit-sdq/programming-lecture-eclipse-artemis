/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.view.controllers;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;

import edu.kit.kastel.eclipse.common.api.controller.IViewInteraction;
import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;

/**
 * this class displays all messages from the backend in the view. It always
 * opens a dialog. An info, error or warning can be displayed.
 *
 */
public final class SWTViewHandler implements IViewInteraction {

	private static final ILog log = Platform.getLog(SWTViewHandler.class);

	@Override
	public void error(String errorMsg) {
		log.error(errorMsg);
		MessageDialog.openError(AssessmentUtilities.getWindowsShell(), "Error", errorMsg);
	}

	@Override
	public void error(String errorMsg, Throwable cause) {
		log.error(errorMsg, cause);
		MessageDialog.openError(AssessmentUtilities.getWindowsShell(), "Error", errorMsg);
	}

	@Override
	public void info(String infoMsg) {
		log.info(infoMsg);
		MessageDialog.openInformation(AssessmentUtilities.getWindowsShell(), "Info", infoMsg);
	}

	@Override
	public void warn(String warningMsg) {
		log.warn(warningMsg);
		MessageDialog.openWarning(AssessmentUtilities.getWindowsShell(), "Warning", warningMsg);
	}
}
