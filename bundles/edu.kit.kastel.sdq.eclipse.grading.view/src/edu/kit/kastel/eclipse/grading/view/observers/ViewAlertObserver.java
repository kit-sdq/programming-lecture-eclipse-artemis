package edu.kit.kastel.eclipse.grading.view.observers;

import org.eclipse.jface.dialogs.MessageDialog;

import edu.kit.kastel.eclipse.grading.view.utilities.AssessmentUtilities;
import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObserver;

/**
 * this class displays all messages from the backend in the view. It always
 * opens a dialog. An info, error or warning can be displayed.
 *
 */
public class ViewAlertObserver implements IAlertObserver {

	@Override
	public void error(String errorMsg, Throwable cause) {
		MessageDialog.openError(AssessmentUtilities.getWindowsShell(), "Error", errorMsg);
	}

	@Override
	public void info(String infoMsg) {
		MessageDialog.openInformation(AssessmentUtilities.getWindowsShell(), "Info", infoMsg);
	}

	@Override
	public void warn(String warningMsg) {
		MessageDialog.openWarning(AssessmentUtilities.getWindowsShell(), "Warning", warningMsg);
	}

}
