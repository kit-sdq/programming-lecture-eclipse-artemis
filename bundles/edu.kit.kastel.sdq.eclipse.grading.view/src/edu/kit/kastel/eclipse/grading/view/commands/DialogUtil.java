/* Licensed under EPL-2.0 2023. */
package edu.kit.kastel.eclipse.grading.view.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;

public final class DialogUtil {
	private static final ILog LOG = Platform.getLog(DialogUtil.class);
	private static final int DIALOG_OFFSET_X = 10; // px

	private DialogUtil() {

	}

	public static Point getInEditorDialogPosition() {
		var position = getCaretPosition();
		position.x += DIALOG_OFFSET_X;
		position.y += getLineHeight();
		return position;
	}

	private static Point getCaretPosition() {
		var viewer = getActiveSourceViewer();
		int caret = viewer.getTextWidget().getCaretOffset();
		return viewer.getTextWidget().toDisplay(viewer.getTextWidget().getLocationAtOffset(caret));
	}

	private static int getLineHeight() {
		var viewer = getActiveSourceViewer();
		return viewer.getTextWidget().getLineHeight();
	}

	@SuppressWarnings("java:S3011")
	private static ISourceViewer getActiveSourceViewer() {
		try {
			var method = AbstractTextEditor.class.getDeclaredMethod("getSourceViewer");
			method.setAccessible(true);
			return (ISourceViewer) method.invoke(AssessmentUtilities.getActiveEditor());
		} catch (ReflectiveOperationException e) {
			LOG.error("Failed to obtain the source viewer", e);
			return null;
		}
	}

	public static void suppressKeyEvent(ExecutionEvent event) {
		if (event.getTrigger() != null) {
			// Windows and GTK behave differently in regard to doit
			if (SWT.getPlatform().equals("gtk")) {
				// Found to work by reverse-engineering Eclipse; cancels the event in
				// StyledText::traverse
				((Event) event.getTrigger()).detail = SWT.TRAVERSE_NONE;
				// doit needs to be true; otherwise Eclipse would do the normal action which is
				// inserting a new line
				((Event) event.getTrigger()).doit = true;
			} else {
				// Works under Windows, untested on Mac (Cocoa)
				((Event) event.getTrigger()).doit = false;
			}
		}
	}
}
