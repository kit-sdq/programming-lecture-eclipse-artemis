/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.student.view.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TestDetailsDialog extends Dialog {
	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;

	private String testName;
	private String testDetails;

	public TestDetailsDialog(Shell parent, String testName, String testDetails) {
		super(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.ON_TOP);
		this.testName = testName;
		this.testDetails = testDetails;
	}

	public void open() {
		Shell shell = new Shell(this.getParent(), this.getStyle());
		shell.setText(this.testName);
		shell.setSize(WIDTH, HEIGHT);

		// Set to mid
		Monitor mon = Display.getDefault().getMonitors()[0];
		int newLeftPos = (mon.getBounds().width - WIDTH) / 2;
		int newTopPos = (mon.getBounds().height - HEIGHT) / 2;
		shell.setLocation(newLeftPos, newTopPos);

		this.createContents(shell);
		// shell.pack();
		shell.open();
		Display display = this.getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void createContents(Shell shell) {
		shell.setLayout(new GridLayout(1, true));
		Text details = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		details.setEditable(false);
		details.setText(this.testDetails.replace("<br />", "\n"));
		details.setLayoutData(new GridData(GridData.FILL_BOTH));
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		ok.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

		shell.setDefaultButton(ok);
	}

}
