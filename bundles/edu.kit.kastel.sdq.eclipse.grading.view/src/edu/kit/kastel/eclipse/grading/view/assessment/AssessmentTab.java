/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.assessment;

import static edu.kit.kastel.eclipse.common.view.languages.LanguageSettings.I18N;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabFolder;

import edu.kit.kastel.eclipse.common.view.utilities.UIUtilities;

public class AssessmentTab extends AssessmentTabComposite {

	public AssessmentTab(TabFolder tabFolder) {
		super(UIUtilities.createTabWithScrolledComposite(tabFolder, I18N().tabAssessment()), SWT.NONE);
		UIUtilities.initializeTabAfterFilling(this.getParent(), this);
	}

	public void resetCombos() {
		this.comboCourse.removeAll();
		this.comboExam.removeAll();
		this.comboExercise.removeAll();
	}
}
