/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.grading.view.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import edu.kit.kastel.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;
import edu.kit.kastel.eclipse.grading.view.activator.Activator;
import edu.kit.kastel.eclipse.grading.view.assessment.ArtemisGradingView;
import edu.kit.kastel.eclipse.grading.view.controllers.AssessmentViewController;

public class DeleteAnnotationCommandHandler extends AbstractHandler {
	private static final ILog LOG = Platform.getLog(DeleteAnnotationCommandHandler.class);

	private final AssessmentViewController controller;
	private final ArtemisGradingView view;

	public DeleteAnnotationCommandHandler(ArtemisGradingView view, AssessmentViewController controller) {
		this.controller = controller;
		this.view = view;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// this.controller().getAssessmentController() may return null even if we have a
		// current assessment
		var assessment = Activator.getDefault().getSystemwideController().getCurrentAssessmentController();
		if (assessment == null) {
			Platform.getLog(this.getClass()).warn("Not executing the Delete Annotation command because the assessment is null");
			return null;
		}

		String path = AssessmentUtilities.getPathForAnnotation();
		int line = AssessmentUtilities.getTextSelection().getStartLine();
		List<IAnnotation> annotationsAtLine = assessment.getAnnotations().stream()
				.filter(a -> a.getClassFilePath().equals(path) && a.getStartLine() <= line && a.getEndLine() >= line).toList();

		if (annotationsAtLine.isEmpty()) {
			DialogUtil.suppressKeyEvent(event);
			return null;
		}

		var dialog = new DeleteAnnotationDialog(AssessmentUtilities.getWindowsShell(), assessment, view, path, line);
		dialog.setBlockOnOpen(true);
		dialog.create();

		dialog.getShell().setLocation(DialogUtil.getInEditorDialogPosition());
		dialog.open();

		DialogUtil.suppressKeyEvent(event);
		AssessmentUtilities.getActiveEditor().setFocus();

		return null;
	}
}
