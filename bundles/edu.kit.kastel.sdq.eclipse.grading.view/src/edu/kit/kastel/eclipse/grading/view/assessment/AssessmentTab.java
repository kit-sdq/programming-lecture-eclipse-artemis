/* Licensed under EPL-2.0 2022-2023. */
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
		this.comboBacklogSubmission.removeAll();
	}

	public void setAssessmentInProgress(boolean courseSelected, boolean examSelected, boolean exerciseSelected, boolean assessmentStarted,
			boolean secondCorrectionRoundEnabled) {
		comboCourse.setEnabled(!assessmentStarted);
		comboExam.setEnabled(courseSelected && !assessmentStarted);
		comboExercise.setEnabled(examSelected && !assessmentStarted);

		btnReload.setEnabled(assessmentStarted);
		btnSave.setEnabled(assessmentStarted);
		btnSubmit.setEnabled(assessmentStarted);
		btnCloseAssessment.setEnabled(assessmentStarted);

		btnRerunAutograder.setEnabled(AutograderUtil.isAutograderEnabled() && assessmentStarted);

		btnStartRoundOne.setEnabled(exerciseSelected && !assessmentStarted);
		btnStartRoundTwo.setEnabled(exerciseSelected && !assessmentStarted && secondCorrectionRoundEnabled);

		btnResetPluginState.setEnabled(!assessmentStarted);

		// Backlog
		comboBacklogSubmission.setEnabled(exerciseSelected && !assessmentStarted);
		btnBacklogRefreshSubmissions.setEnabled(exerciseSelected && !assessmentStarted);
		btnBacklogLoadSubmission.setEnabled(exerciseSelected && !assessmentStarted);

	}

}
