/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.assessment.complaints;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabFolder;

import edu.kit.kastel.eclipse.common.view.utilities.UIUtilities;

public class ComplaintTab extends ComplaintComposite {

	public ComplaintTab(TabFolder tabFolder) {
		super(UIUtilities.createTabWithScrolledComposite(tabFolder, "Complaints"), SWT.NONE);
		UIUtilities.initializeTabAfterFilling(this.getParent(), this);
	}

}
