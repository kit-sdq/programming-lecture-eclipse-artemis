/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.view.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class TestDetailsDialog extends Dialog {
	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;

	private final String testName;
	private final String testDetails;

	public TestDetailsDialog(String testName, String testDetails) {
		super((Shell) null);
		this.setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MODELESS | SWT.ON_TOP);
		this.testName = testName;
		this.testDetails = testDetails;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(this.testName);
		newShell.setSize(WIDTH, HEIGHT);

		// Set to mid
		Monitor mon = Display.getDefault().getMonitors()[0];
		int newLeftPos = (mon.getBounds().width - WIDTH) / 2;
		int newTopPos = (mon.getBounds().height - HEIGHT) / 2;
		newShell.setLocation(newLeftPos, newTopPos);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite comp = (Composite) super.createDialogArea(parent);

		final GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = 1;

		Text details = new Text(comp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		details.setText(this.testDetails.replace("<br />", "\n"));
		details.setLayoutData(new GridData(GridData.FILL_BOTH));
		return comp;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

}
