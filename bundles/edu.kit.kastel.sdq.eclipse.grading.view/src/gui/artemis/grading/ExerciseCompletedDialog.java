package gui.artemis.grading;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ExerciseCompletedDialog extends Dialog {

	protected ExerciseCompletedDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Exercise completed!");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite comp = (Composite) super.createDialogArea(parent);
		final Label customMessageLabel = new Label(comp, SWT.RIGHT);
		customMessageLabel.setText("No further assessment for the current selected exercise.");

		return comp;
	}

}
