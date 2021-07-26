package gui.artemis.grading;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import gui.controllers.AssessmentViewController;

public class CustomButtonDialog extends Dialog {

	private Text customMessageInputField;
	private Spinner customPenaltyInputField;
	private String customMessage;
	private Double customPenalty;
	private final AssessmentViewController viewController;
	private final String ratingGroupName;

	public CustomButtonDialog(Shell parentShell, AssessmentViewController viewController, String ratingGroupName,
			Group ratingGroupViewElement) {
		super(parentShell);
		this.viewController = viewController;
		this.ratingGroupName = ratingGroupName;
	}

	@Override
	protected void cancelPressed() {
		this.customMessageInputField.setText("");
		super.cancelPressed();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Create custom error");
	}

	// TODO: add validation for custom penalty
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite comp = (Composite) super.createDialogArea(parent);

		final GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = 2;

		final Label customMessageLabel = new Label(comp, SWT.RIGHT);
		customMessageLabel.setText("Custom Message: ");
		this.customMessageInputField = new Text(comp, SWT.SINGLE | SWT.BORDER);

		final Label customPenaltyLabel = new Label(comp, SWT.RIGHT);
		customPenaltyLabel.setText("Custom Penalty: ");
		this.customPenaltyInputField = new Spinner(comp, SWT.SINGLE | SWT.BORDER);
		this.customPenaltyInputField.setDigits(1);
		this.customPenaltyInputField.setIncrement(5);

		final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		this.customMessageInputField.setLayoutData(data);
		this.customPenaltyInputField.setLayoutData(data);

		return comp;
	}

	public String getCustomMessage() {
		return this.customMessage;
	}

	public Double getCustomPenalty() {
		return this.customPenalty;
	}

	@Override
	protected void okPressed() {
		this.customMessage = this.customMessageInputField.getText();
		this.customPenalty = Double.parseDouble(this.customPenaltyInputField.getText().replace(',', '.'));
		this.viewController.addAssessmentAnnotaion(null, this.customMessage, this.customPenalty, this.ratingGroupName);
		super.okPressed();
	}
}
