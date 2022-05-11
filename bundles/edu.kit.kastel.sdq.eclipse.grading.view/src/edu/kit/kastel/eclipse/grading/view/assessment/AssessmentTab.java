/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.assessment;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabFolder;

import edu.kit.kastel.eclipse.common.view.utilities.UIUtilities;

public class AssessmentTab extends AssessmentTabComposite {

	public AssessmentTab(TabFolder tabFolder) {
		super(UIUtilities.createTabWithScrolledComposite(tabFolder, "Assessment"), SWT.NONE);
		UIUtilities.initializeTabAfterFilling(this.getParent(), this);
	}

	public void resetCombos() {
		this.comboCourse.removeAll();
		this.comboExam.removeAll();
		this.comboExercise.removeAll();
	}

	public void setAssessmentInProgress(boolean courseSelected, boolean examSelected, boolean exerciseSelected, boolean assessmentStarted,
			boolean secondCorrectionRoundEnabled) {
		comboCourse.setEnabled(!assessmentStarted);
		comboExam.setEnabled(courseSelected && !assessmentStarted);
		comboExercise.setEnabled(examSelected && !assessmentStarted);

		btnReload.setEnabled(assessmentStarted);
		btnSave.setEnabled(assessmentStarted);
		btnSubmit.setEnabled(assessmentStarted);

		btnStartRoundOne.setEnabled(exerciseSelected && !assessmentStarted);
		btnStartRoundTwo.setEnabled(exerciseSelected && !assessmentStarted && secondCorrectionRoundEnabled);

		btnResetPluginState.setEnabled(!assessmentStarted);
	}

}
