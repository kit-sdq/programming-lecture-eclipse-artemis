/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.assessment;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;

import edu.kit.kastel.eclipse.common.view.utilities.UIUtilities;

public class GradingTabComposite extends Composite {
	protected Label lblPluginVersion;
	protected ScrolledComposite gradingCompositeContainerScrollable;

	public GradingTabComposite(TabFolder tabFolder) {
		this(UIUtilities.createTabWithScrolledComposite(tabFolder, "Grading"), SWT.NONE);
		UIUtilities.initializeTabAfterFilling(this.getParent(), this);
	}

	/**
	 * Create the composite.
	 */
	public GradingTabComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Composite gradingComposite = new Composite(this, SWT.BORDER);
		gradingComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		gradingComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		gradingCompositeContainerScrollable = new ScrolledComposite(gradingComposite, SWT.H_SCROLL | SWT.V_SCROLL);
		gradingCompositeContainerScrollable.setExpandHorizontal(true);
		gradingCompositeContainerScrollable.setExpandVertical(true);

		lblPluginVersion = new Label(this, SWT.NONE);
		lblPluginVersion.setAlignment(SWT.RIGHT);
		lblPluginVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		lblPluginVersion.setText("PluginVersion");

	}

	@Override
	protected final void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
