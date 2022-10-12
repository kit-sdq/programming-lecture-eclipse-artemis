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
import edu.kit.kastel.eclipse.grading.view.assessment.CustomButtonDialog;
import edu.kit.kastel.eclipse.grading.view.controllers.AssessmentViewController;

public class AddAnnotationCommandHandler extends AbstractHandler {
	private final AssessmentViewController controller;
	
	public AddAnnotationCommandHandler(AssessmentViewController controller) {
		this.controller = controller;
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (this.controller.getAssessmentController() != null) {
			var dialog = new AddAnnotationDialog(AssessmentUtilities.getWindowsShell(), this.controller);
			//var dialog = new AddAnnotationFilteringDialog(AssessmentUtilities.getWindowsShell(), this.controller);
			dialog.setBlockOnOpen(true);
			dialog.create();
			
			var dialogPosition = getCaretPosition();
			dialogPosition.x += 10;
			dialog.getShell().setLocation(dialogPosition);
			dialog.open();
			
			Optional<IMistakeType> selectedMistake = dialog.getSelectedMistake();
			if (selectedMistake.isPresent()) {
				if (selectedMistake.get().isCustomPenalty()) {
					CustomButtonDialog customDialog = new CustomButtonDialog(
							AssessmentUtilities.getWindowsShell(), 
							controller.getAssessmentController().isPositiveFeedbackAllowed(), 
							controller, 
							selectedMistake.get());
					customDialog.setBlockOnOpen(true);
					customDialog.create();
					customDialog.getShell().setLocation(dialogPosition);
					customDialog.open();
					// The dialog creates the annotation
				} else if (dialog.isCustomMessageWanted()) {
					CustomButtonDialog customDialog = new CustomButtonDialog(
							AssessmentUtilities.getWindowsShell(), 
							controller.getAssessmentController().isPositiveFeedbackAllowed(), 
							controller, 
							null);
					customDialog.setBlockOnOpen(true);
					customDialog.create();
					customDialog.getShell().setLocation(dialogPosition);
					customDialog.getShell().setText("Add custom message to penalty \"" + selectedMistake.get().getButtonText() + "\"");
					customDialog.open();
					if (customDialog.isClosedByOk()) {
						AssessmentUtilities.createAssessmentAnnotation(controller.getAssessmentController(), selectedMistake.get(), customDialog.getCustomMessage(), null);
					}
				} else {
					AssessmentUtilities.createAssessmentAnnotation(controller.getAssessmentController(), selectedMistake.get(), null, null);
				}
			}
		}
		
		// Prevent insertion of a new line because the default keybinding is alt+enter
		// It took me ages to figure out how to do this
		if (event.getTrigger() != null) {
			((Event) event.getTrigger()).doit = false;
		}
		
		// Return the focus to the editor
		AssessmentUtilities.getActiveEditor().setFocus();
		
		return null;
	}
	
	private ISourceViewer getActiveSourceViewer() {
		try {
			// I'm sorry
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
}
