/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.grading.view.commands;

import static edu.kit.kastel.eclipse.common.view.languages.LanguageSettings.I18N;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.Platform;

import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;
import edu.kit.kastel.eclipse.grading.view.activator.Activator;
import edu.kit.kastel.eclipse.grading.view.assessment.ArtemisGradingView;
import edu.kit.kastel.eclipse.grading.view.assessment.CustomButtonDialog;
import edu.kit.kastel.eclipse.grading.view.controllers.AssessmentViewController;

public class AddAnnotationCommandHandler extends AbstractHandler {

	private final AssessmentViewController controller;
	private final ArtemisGradingView view;

	public AddAnnotationCommandHandler(ArtemisGradingView view, AssessmentViewController controller) {
		this.controller = controller;
		this.view = view;
	}

	@Override
	public Object execute(ExecutionEvent event) {
		// this.controller().getAssessmentController() may return null even if we have a
		// current assessment
		var assessment = Activator.getDefault().getSystemwideController().getCurrentAssessmentController();
		if (assessment == null) {
			Platform.getLog(this.getClass()).warn("Not executing the Add Annotation command because the assessment is null");
			return null;
		}

		var dialog = new AddAnnotationDialog(AssessmentUtilities.getWindowsShell(), assessment);
		dialog.setBlockOnOpen(true);
		dialog.create();
		dialog.getShell().setLocation(DialogUtil.getInEditorDialogPosition());
		dialog.open();

		Optional<IMistakeType> selectedMistake = dialog.getSelectedMistake();
		if (selectedMistake.isPresent()) {
			if (selectedMistake.get().isCustomPenalty()) {
				CustomButtonDialog customDialog = new CustomButtonDialog(AssessmentUtilities.getWindowsShell(), assessment.isPositiveFeedbackAllowed(),
						controller, selectedMistake.get());
				customDialog.setBlockOnOpen(true);
				customDialog.create();
				customDialog.getShell().setLocation(DialogUtil.getInEditorDialogPosition());
				customDialog.open();
				// The dialog creates the annotation
			} else if (dialog.isCustomMessageWanted()) {
				CustomButtonDialog customDialog = new CustomButtonDialog(AssessmentUtilities.getWindowsShell(), assessment.isPositiveFeedbackAllowed(),
						controller, null);
				customDialog.setBlockOnOpen(true);
				customDialog.create();
				customDialog.getShell().setLocation(DialogUtil.getInEditorDialogPosition());
				customDialog.getShell().setText("Add custom message to penalty \"" + selectedMistake.get().getButtonText(I18N().key()) + "\"");
				customDialog.open();
				if (customDialog.isClosedByOk()) {
					AssessmentUtilities.createAssessmentAnnotation(assessment, selectedMistake.get(), customDialog.getCustomMessage(), null);
				}
			} else {
				AssessmentUtilities.createAssessmentAnnotation(assessment, selectedMistake.get(), null, null);
			}
			this.view.updatePenalties();
		}

		DialogUtil.suppressKeyEvent(event);
		AssessmentUtilities.getActiveEditor().setFocus();

		return null;
	}
}
