package edu.kit.kastel.eclipse.common.view.utilities;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;

/**
 * This class includes only static methods (or constants) for the assessment
 * process. It also contains methods (or constant) for the view controller to
 * keep the class clean.
 */
public final class AssessmentUtilities {

	public static final String MARKER_NAME = "edu.kit.kastel.eclipse.common.view.assessment.marker";
	public static final String MARKER_ATTRIBUTE_ANNOTATION_ID = "annotationID";
	public static final String MARKER_ATTRIBUTE_ERROR = "errorType";
	public static final String MARKER_ATTRIBUTE_ERROR_DESCRIPTION = "errorTypeDescription";
	public static final String MARKER_ATTRIBUTE_CLASS_NAME = "className";
	public static final String MARKER_ATTRIBUTE_RATING_GROUP = "ratingGroup";
	public static final String MARKER_ATTRIBUTE_CUSTOM_PENALTY = "customPenalty";
	public static final String MARKER_ATTRIBUTE_CUSTOM_MESSAGE = "customMessage";
	public static final String MARKER_ATTRIBUTE_START = "start";
	public static final String MARKER_ATTRIBUTE_END = "end";

	public static final String MARKER_VIEW_ID = "edu.kit.kastel.eclipse.common.view.annotationMarkerGenerator";

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

	public static void createMarkerForAnnotation(IAnnotation annotation, String currentProjectName) throws ArtemisClientException {

		int startLine = annotation.getStartLine();
		int endLine = annotation.getEndLine();
		IMistakeType mistake = annotation.getMistakeType();
		String customMessage = annotation.getCustomMessage().orElse(null);
		String customPenalty = annotation.getCustomPenalty().map(String::valueOf).orElse(null);
		try {
			IMarker marker = AssessmentUtilities.getFile(annotation.getClassFilePath(), currentProjectName).createMarker(AssessmentUtilities.MARKER_NAME);
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ANNOTATION_ID, annotation.getUUID());
			marker.setAttribute(IMarker.CHAR_START, annotation.getMarkerCharStart());
			marker.setAttribute(IMarker.CHAR_END, annotation.getMarkerCharEnd());

			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_START, startLine);
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_END, endLine);
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_CLASS_NAME, annotation.getClassFilePath());
			if (customMessage != null) {
				marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_CUSTOM_MESSAGE, customMessage);
			}
			if (customPenalty != null) {
				marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_CUSTOM_PENALTY, customPenalty);
			}
			if (mistake != null) {
				marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ERROR_DESCRIPTION, mistake.getMessage());
				marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ERROR, mistake.getButtonText());
				marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_RATING_GROUP, mistake.getRatingGroup().getDisplayName());
				if (!mistake.isCustomPenalty()) {
					marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities.createMarkerTooltip(startLine, endLine, mistake.getButtonText(),
							mistake.getRatingGroup().getDisplayName(), formatCustomPenaltyMessage(mistake, customMessage), annotation.getClassFilePath()));
				} else {
					marker.setAttribute(IMarker.MESSAGE,
							AssessmentUtilities.createMarkerTooltipForCustomButton(startLine, endLine, customMessage, Double.parseDouble(customPenalty)));
				}
			}

		} catch (Exception e) {
			throw new ArtemisClientException(e.getMessage());
		}
	}

	/**
	 * Checks whether the given annotation is present in the currently opened
	 * project (An annotation is identified by its UUID)
	 * 
	 * @param annotation the annotation to check
	 * @return true if the annotation is present, false if not
	 */
	public static boolean isAnnotationPresent(IAnnotation annotation, String currentProjectName) {
		try {
			IMarker[] markers = AssessmentUtilities.getFile(annotation.getClassFilePath(), currentProjectName).findMarkers(null, false, 100);
			for (IMarker marker : markers) {
				if (annotation.getUUID().equals(marker.getAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ANNOTATION_ID))) {
					return true;
				}
			}
			return false;
		} catch (CoreException e) {
			// If the project (or file) can not be loaded the annotation is definitely not
			// present
			return false;
		}
	}

	/**
	 * Formats a custom penalty message. It will always use the message of the
	 * mistake, however iff the provided customMessage is not null, it will append a
	 * \n and this custom message.
	 * 
	 * @param mistake       the mistake to load the message from
	 * @param customMessage the custom message to append (can be null)
	 * @return the formatted message
	 */
	public static String formatCustomPenaltyMessage(IMistakeType mistake, String customMessage) {
		if (customMessage != null) {
			return mistake.getMessage() + "\n" + customMessage;
		} else {
			return mistake.getMessage();
		}
	}

}
