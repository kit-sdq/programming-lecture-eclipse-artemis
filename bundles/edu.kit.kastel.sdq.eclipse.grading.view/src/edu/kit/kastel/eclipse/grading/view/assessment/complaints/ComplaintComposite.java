/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.assessment.complaints;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ComplaintComposite extends Composite {
	private Combo complaintCombo;
	private Button btnOpenComplaint;
	private Button btnLockComplaint;
	private Text complaintText;
	private Button btnAcceptComplaint;
	private Button btnRejectComplaint;

	public ComplaintComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		Composite complaintManagement = new Composite(this, SWT.NONE);
		complaintManagement.setLayout(new GridLayout(2, false));
		complaintManagement.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		complaintCombo = new Combo(complaintManagement, SWT.NONE);
		complaintCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Composite upperButtons = new Composite(complaintManagement, SWT.NONE);
		upperButtons.setLayout(new GridLayout(2, true));
		upperButtons.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false, 2, 1));

		btnOpenComplaint = new Button(upperButtons, SWT.NONE);
		btnOpenComplaint.setBounds(0, 0, 90, 30);
		btnOpenComplaint.setText("Open Complaint (RO)");

		btnLockComplaint = new Button(upperButtons, SWT.NONE);
		btnLockComplaint.setText("Lock Complaint (RW)");

		Composite textComposite = new Composite(this, SWT.NONE);
		textComposite.setLayout(new GridLayout(1, false));
		textComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		ScrolledComposite scrolledComposite = new ScrolledComposite(textComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		complaintText = new Text(scrolledComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		scrolledComposite.setContent(complaintText);
		scrolledComposite.setMinSize(complaintText.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Composite decisionComposite = new Composite(this, SWT.NONE);
		decisionComposite.setLayout(new GridLayout(2, true));
		decisionComposite.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1));

		btnAcceptComplaint = new Button(decisionComposite, SWT.NONE);
		btnAcceptComplaint.setText("Accept Complaint");

		btnRejectComplaint = new Button(decisionComposite, SWT.NONE);
		btnRejectComplaint.setText("Reject Complaint");

	}

	@Override
	protected final void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
