package edu.kit.kastel.eclipse.common.view.utilities;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 *
 * This class includes only static methods (or constants) for the assessment
 * process. It also contains methods (or constant) for the view controller to
 * keep the class clean.
 */
public final class AssessmentUtilities {

	public static final String MARKER_NAME = "edu.kit.kastel.eclipse.grading.view.assessment.marker";
	public static final String MARKER_ATTRIBUTE_ANNOTATION_ID = "annotationID";
	public static final String MARKER_ATTRIBUTE_ERROR = "errorType";
	public static final String MARKER_ATTRIBUTE_ERROR_DESCRIPTION = "errorTypeDescription";
	public static final String MARKER_ATTRIBUTE_CLASS_NAME = "className";
	public static final String MARKER_ATTRIBUTE_RATING_GROUP = "ratingGroup";
	public static final String MARKER_ATTRIBUTE_CUSTOM_PENALTY = "customPenalty";
	public static final String MARKER_ATTRIBUTE_CUSTOM_MESSAGE = "customMessage";
	public static final String MARKER_ATTRIBUTE_START = "start";
	public static final String MARKER_ATTRIBUTE_END = "end";

	public static final String MARKER_VIEW_ID = "edu.kit.kastel.eclipse.grading.view.annotationMarkerGenerator";

	public static final int BACKLOG_COMBO_WIDTH = 300;

	/**
	 * Creates a tooltip for a marker with the given parameters
	 *
	 * @param startLine
	 * @param endline
	 * @param errorTypeName
	 * @param ratingGroupName
	 * @param message
	 * @param classPath
	 * @return the tooltip for the marker as string
	 */
	public static String createMarkerTooltip(int startLine, int endline, String errorTypeName, String ratingGroupName, String message, String classPath) {
		return String.format("[%s,%s], %s, %s, %s, %s", startLine, endline, classPath == null ? getClassNameForAnnotation() : classPath, ratingGroupName,
				errorTypeName, message);
	}

	/**
	 * Creates a tooltip for the custom button
	 *
	 * @param startLine
	 * @param endline
	 * @param customMessage
	 * @param customPenalty
	 * @return tooltip of custom button as String
	 */
	public static String createMarkerTooltipForCustomButton(int startLine, int endline, String customMessage, Double customPenalty) {
		return String.format("[%s,%s], %s, %s", startLine, endline, customMessage, customPenalty);
	}

	/**
	 * @param path        (of the file)
	 * @param projectName (of the currently downloaded project)
	 * @return An IFile instance of the file determined by the path
	 */
	public static IFile getFile(String path, String projectName) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).getFile("assignment/" + path);
	}

	/**
	 * @return the class name of the currently active file in the editor
	 */
	public static String getClassNameForAnnotation() {
		final IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		return workbenchPart.getSite().getPage().getActiveEditor().getEditorInput().getName();
	}

	/**
	 * Gets the current open file
	 *
	 * @return IFile instance of the current open file in the editor
	 */
	public static IFile getCurrentlyOpenFile() {
		final IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		return workbenchPart.getSite().getPage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
	}

	/**
	 * @return the path of the currently open file
	 */
	public static String getPathForAnnotation() {
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		final IWorkbench workbench = PlatformUI.getWorkbench();
		final IWorkbenchWindow wb = workbench == null ? null : workbench.getActiveWorkbenchWindow();
		final IWorkbenchPage activePage = wb == null ? null : window.getActivePage();

		final IEditorPart editor = activePage == null ? null : activePage.getActiveEditor();

		final IEditorInput input = editor == null ? null : editor.getEditorInput();
		final IPath path = input instanceof FileEditorInput ? ((FileEditorInput) input).getPath() : null;
		if (path != null) {

			int srcIndex = 0;
			for (int i = 0; i < path.segments().length; i++) {
				if (path.segments()[i].equals("src")) {
					srcIndex = i;
					break;
				}
			}
			StringBuilder result = new StringBuilder().append("src");
			for (int j = srcIndex + 1; j < path.segments().length; j++) {
				result.append("/");
				result.append(path.segments()[j]);
			}
			return result.toString();
		}
		return "";
	}

	/**
	 * @return the currently selected text
	 */
	public static ITextSelection getTextSelection() {
		final IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part instanceof ITextEditor) {
			final ITextEditor editor = (ITextEditor) part;
			final ISelection selection = editor.getSelectionProvider().getSelection();
			return (ITextSelection) selection;
		}
		return null;
	}

	public static Shell getWindowsShell() {
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		return window.getShell();
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	private AssessmentUtilities() {
		throw new IllegalAccessError();
	}

}
