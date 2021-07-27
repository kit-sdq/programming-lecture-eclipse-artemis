package observers;

import org.eclipse.jface.dialogs.MessageDialog;

import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObserver;
import gui.utilities.AssessmentUtilities;

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
