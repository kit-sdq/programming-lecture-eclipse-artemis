/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.assessment;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabFolder;

import edu.kit.kastel.eclipse.common.view.utilities.ResourceBundleProvider;
import edu.kit.kastel.eclipse.common.view.utilities.UIUtilities;

public class AssessmentTab extends AssessmentTabComposite {

	public AssessmentTab(TabFolder tabFolder) {
		super(UIUtilities.createTabWithScrolledComposite(tabFolder, ResourceBundleProvider.getResourceBundle().getString("tabs.assessment")), SWT.NONE);
		UIUtilities.initializeTabAfterFilling(this.getParent(), this);
	}

	public void resetCombos() {
		this.comboCourse.removeAll();
		this.comboExam.removeAll();
		this.comboExercise.removeAll();
	}
}
