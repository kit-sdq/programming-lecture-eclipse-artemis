/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.commands;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import edu.kit.kastel.eclipse.common.api.model.IMistakeType;
import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;
import edu.kit.kastel.eclipse.grading.view.activator.Activator;
import edu.kit.kastel.eclipse.grading.view.assessment.ArtemisGradingView;
import edu.kit.kastel.eclipse.grading.view.assessment.CustomButtonDialog;
import edu.kit.kastel.eclipse.grading.view.controllers.AssessmentViewController;

public class AddAnnotationCommandHandler extends AbstractHandler {
	private static final int DIALOG_OFFSET_X = 10; // px

	private final AssessmentViewController controller;
	private final ArtemisGradingView view;

	public AddAnnotationCommandHandler(ArtemisGradingView view, AssessmentViewController controller) {
		this.controller = controller;
		this.view = view;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
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

		var dialogPosition = getCaretPosition();
		dialogPosition.x += DIALOG_OFFSET_X;
		dialogPosition.y += getLineHeight();
		dialog.getShell().setLocation(dialogPosition);
		dialog.open();

		Optional<IMistakeType> selectedMistake = dialog.getSelectedMistake();
		if (selectedMistake.isPresent()) {
			if (selectedMistake.get().isCustomPenalty()) {
				CustomButtonDialog customDialog = new CustomButtonDialog(AssessmentUtilities.getWindowsShell(), assessment.isPositiveFeedbackAllowed(),
						controller, selectedMistake.get());
				customDialog.setBlockOnOpen(true);
				customDialog.create();
				customDialog.getShell().setLocation(dialogPosition);
				customDialog.open();
				// The dialog creates the annotation
			} else if (dialog.isCustomMessageWanted()) {
				CustomButtonDialog customDialog = new CustomButtonDialog(AssessmentUtilities.getWindowsShell(), assessment.isPositiveFeedbackAllowed(),
						controller, null);
				customDialog.setBlockOnOpen(true);
				customDialog.create();
				customDialog.getShell().setLocation(dialogPosition);
				customDialog.getShell().setText("Add custom message to penalty \"" + selectedMistake.get().getButtonText() + "\"");
				customDialog.open();
				if (customDialog.isClosedByOk()) {
					AssessmentUtilities.createAssessmentAnnotation(assessment, selectedMistake.get(), customDialog.getCustomMessage(), null);
				}
			} else {
				AssessmentUtilities.createAssessmentAnnotation(assessment, selectedMistake.get(), null, null);
			}
			this.view.updatePenalties();
		}

		// Prevent insertion of a new line because the default keybinding is alt+enter
		if (event.getTrigger() != null) {
			((Event) event.getTrigger()).doit = false;
		}

		// Return the focus to the editor
		AssessmentUtilities.getActiveEditor().setFocus();

		return null;
	}

	private ISourceViewer getActiveSourceViewer() {
		try {
			var method = AbstractTextEditor.class.getDeclaredMethod("getSourceViewer");
			method.setAccessible(true);
			return (ISourceViewer) method.invoke(AssessmentUtilities.getActiveEditor());
		} catch (ReflectiveOperationException e) {
			Platform.getLog(this.getClass()).error("Failed to obtain the source viewer", e);
			return null;
		}
	}

	private Point getCaretPosition() {
		var viewer = getActiveSourceViewer();
		int caret = viewer.getTextWidget().getCaretOffset();
		return viewer.getTextWidget().toDisplay(viewer.getTextWidget().getLocationAtOffset(caret));
	}

	private int getLineHeight() {
		var viewer = getActiveSourceViewer();
		return viewer.getTextWidget().getLineHeight();
	}
}
