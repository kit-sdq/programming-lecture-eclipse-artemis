/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.utilities;

import java.util.Optional;
import java.util.Scanner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.controller.IAssessmentController;
import edu.kit.kastel.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.eclipse.common.api.model.IMistakeType;

/**
 * This class includes only static methods (or constants) for the assessment
 * process. It also contains methods (or constant) for the view controller to
 * keep the class clean.
 */
public final class AssessmentUtilities {

	public static final String MARKER_CLASS_NAME = "edu.kit.kastel.eclipse.common.view.assessment.marker";
	public static final String MARKER_ATTRIBUTE_ANNOTATION_ID = "annotationID";
	public static final String MARKER_ATTRIBUTE_ERROR = "errorType";
	public static final String MARKER_ATTRIBUTE_ERROR_DESCRIPTION = "errorTypeDescription";
	public static final String MARKER_ATTRIBUTE_CLASS_NAME = "className";
	public static final String MARKER_ATTRIBUTE_RATING_GROUP = "ratingGroup";
	public static final String MARKER_ATTRIBUTE_CUSTOM_PENALTY = "customPenalty";
	public static final String MARKER_ATTRIBUTE_CUSTOM_MESSAGE = "customMessage";
	public static final String MARKER_ATTRIBUTE_START = "start";
	public static final String MARKER_ATTRIBUTE_END = "end";
	private static final String PROJECT_EXPLORER_ID = "org.eclipse.ui.navigator.ProjectExplorer";

	public static final String MARKER_VIEW_ID = "edu.kit.kastel.eclipse.common.view.annotationMarkerGenerator";

	public static final int BACKLOG_COMBO_WIDTH = 300;

	private static final ILog LOG = Platform.getLog(AssessmentUtilities.class);

	private AssessmentUtilities() {
		throw new IllegalAccessError();
	}

	/**
	 * Creates a tooltip for a marker with the given parameters
	 *
	 * @return the tooltip for the marker as string
	 */
	public static String createMarkerTooltip(int startLine, int endline, String errorTypeName, String ratingGroupName, String message, String classPath) {
		// Lines are indexed at 0 ..
		return String.format("[%s,%s], %s, %s, %s, %s", startLine + 1, endline + 1, classPath == null ? getClassNameForAnnotation() : classPath,
				ratingGroupName, errorTypeName, message);
	}

	/**
	 * Creates a tooltip for the custom button
	 *
	 * @return tooltip of custom button as String
	 */
	public static String createMarkerTooltipForCustomButton(int startLine, int endline, String customMessage, Double customPenalty) {
		// Lines are indexed at 0 ..
		return String.format("[%s,%s], %s, %s", startLine + 1, endline + 1, customMessage, customPenalty);
	}

	/**
	 * @param path        (of the file)
	 * @param projectName (of the currently downloaded project)
	 * @return An IFile instance of the file determined by the path
	 */
	public static IFile getFile(String path, String projectName, String srcDirectory) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).getFile(srcDirectory + path);
	}

	public static IEditorPart getActiveEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	/**
	 * @return the class name of the currently active file in the editor
	 */
	public static String getClassNameForAnnotation() {
		return getActiveEditor().getEditorInput().getName();
	}

	/**
	 * Gets the current open file
	 *
	 * @return IFile instance of the current open file in the editor
	 */
	public static IFile getCurrentlyOpenFile() {
		return getActiveEditor().getEditorInput().getAdapter(IFile.class);
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
		final IPath path = input instanceof FileEditorInput fileEditor ? fileEditor.getPath() : null;
		if (path != null) {

			int srcIndex = 0;
			for (int i = 0; i < path.segments().length; i++) {
				if ("src".equals(path.segments()[i])) {
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
		final IEditorPart part = getActiveEditor();
		if (part instanceof ITextEditor editor) {
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

	/**
	 * This method creates a marker for the annotation and add a new annotation to
	 * the backlog
	 *
	 * @param mistake         (the mistake type of the new annotation)
	 * @param customMessage   (for custom mistake type, else null)
	 * @param customPenalty   (for custom mistake, else null)
	 * @param ratingGroupName (the name of the rating group of the new annotation)
	 */
	public static void createAssessmentAnnotation(IAssessmentController assessmentController, IMistakeType mistake, String customMessage,
			Double customPenalty) {
		final ITextSelection textSelection = AssessmentUtilities.getTextSelection();
		if (textSelection == null) {
			assessmentController.getViewInteractionHandler().error("Text selection needed to add a new annotation", null);
			return;
		}
		// Lines are indexed starting at 0.
		final int startLine = textSelection.getStartLine();
		final int endLine = textSelection.getEndLine();
		final IFile file = AssessmentUtilities.getCurrentlyOpenFile();
		final String projectName = file.getProject().getName();
		final String srcPath = "assignment/src";
		final String className = file.getFullPath().makeRelative().toString().split("src", 2)[1];

		try {
			String id = IAnnotation.createID();
			IMarker marker = AssessmentUtilities.getCurrentlyOpenFile().createMarker(AssessmentUtilities.MARKER_CLASS_NAME);
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ANNOTATION_ID, id);
			AssessmentUtilities.setCharPositionsInMarkerByLine(marker, projectName, srcPath, className, startLine, endLine);

			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ERROR_DESCRIPTION, mistake.isCustomPenalty() ? "" : mistake.getMessage());
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ERROR, mistake.getButtonText());
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_START, startLine);
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_END, endLine);
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_CLASS_NAME, AssessmentUtilities.getClassNameForAnnotation());
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_RATING_GROUP, mistake.getRatingGroup().getDisplayName());
			if (customMessage != null) {
				marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_CUSTOM_MESSAGE, customMessage);
			}
			if (customPenalty != null) {
				marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_CUSTOM_PENALTY, customPenalty.toString());
			}
			if (!mistake.isCustomPenalty()) {
				marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities.createMarkerTooltip(startLine, endLine, mistake.getButtonText(),
						mistake.getRatingGroup().getDisplayName(), AssessmentUtilities.formatCustomPenaltyMessage(mistake, customMessage), null));
			} else {
				marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities.createMarkerTooltipForCustomButton(startLine, endLine, customMessage, customPenalty));
			}
			assessmentController.addAnnotation(id, mistake, startLine, endLine, AssessmentUtilities.getPathForAnnotation(), customMessage, customPenalty);
		} catch (Exception e) {

			/*
			 * Future Work: the error handling should be more specific (maybe for each
			 * setAttribute(...)) without getting a too messy code
			 */
			assessmentController.getViewInteractionHandler().error("Unable to create marker for annotation: " + e.getMessage(), e);
		}

	}

	public static void createMarkerByAnnotation(IAnnotation annotation, String currentProjectName, String srcDirectory) throws ArtemisClientException {

		int startLine = annotation.getStartLine();
		int endLine = annotation.getEndLine();
		IMistakeType mistake = annotation.getMistakeType();
		String customMessage = annotation.getCustomMessage().orElse(null);
		String customPenalty = annotation.getCustomPenalty().map(String::valueOf).orElse(null);
		try {
			IFile file = AssessmentUtilities.getFile(annotation.getClassFilePath(), currentProjectName, srcDirectory);
			IMarker marker = file.createMarker(AssessmentUtilities.MARKER_CLASS_NAME);
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ANNOTATION_ID, annotation.getUUID());
			setCharPositionsInMarkerByLine(marker, currentProjectName, srcDirectory, annotation.getClassFilePath(), startLine, endLine);

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
			} else if (customMessage != null) {
				marker.setAttribute(IMarker.MESSAGE, customMessage.replace("<br />", "\n"));
			}

		} catch (Exception e) {
			throw new ArtemisClientException(e.getMessage());
		}
	}

	public static void setCharPositionsInMarkerByLine(IMarker marker, String currentProjectName, String srcDirectory, String classFileInPath, int startLine,
			int endLine) throws ArtemisClientException {
		try {
			var contentStream = ResourcesPlugin.getWorkspace().getRoot().getProject(currentProjectName).getFile(srcDirectory + classFileInPath).getContents();
			Scanner s = new Scanner(contentStream).useDelimiter("\\A");
			String content = s.hasNext() ? s.next() : "";
			s.close();
			IDocument doc = new Document(content);
			var charOffsetStart = doc.getLineOffset(startLine);
			int charOffsetEnd;
			if (startLine == endLine) {
				charOffsetEnd = charOffsetStart + doc.getLineLength(startLine);
			} else {
				charOffsetEnd = doc.getLineOffset(endLine) + doc.getLineLength(endLine);
			}
			marker.setAttribute(IMarker.CHAR_START, charOffsetStart);
			marker.setAttribute(IMarker.CHAR_END, charOffsetEnd);
		} catch (Exception e) {
			throw new ArtemisClientException(e.getMessage(), e);
		}
	}

	public static void updateMarkerMessage(IMarker marker, String newMessage, Double newPenalty) throws ArtemisClientException {
		try {
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_CUSTOM_MESSAGE, newMessage);
			marker.setAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_CUSTOM_PENALTY, newPenalty == null ? null : String.valueOf(newPenalty));
			Integer startLine = (Integer) marker.getAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_START);
			Integer endLine = (Integer) marker.getAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_END);
			marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities.createMarkerTooltipForCustomButton(startLine, endLine, newMessage, newPenalty));
		} catch (Exception e) {
			throw new ArtemisClientException(e.getMessage(), e);
		}
	}

	/**
	 * Checks whether the given annotation is present in the currently opened
	 * project (An annotation is identified by its ID)
	 *
	 * @param annotation the annotation to check
	 * @return annotation if the annotation is present, null if not
	 */
	public static IMarker findPresentAnnotation(IAnnotation annotation, String currentProjectName, String srcDirectory) {
		try {
			IMarker[] markers = AssessmentUtilities.getFile(annotation.getClassFilePath(), currentProjectName, srcDirectory).findMarkers(null, false, 100);
			for (IMarker marker : markers) {
				if (annotation.getUUID().equals(marker.getAttribute(AssessmentUtilities.MARKER_ATTRIBUTE_ANNOTATION_ID))) {
					return marker;
				}
			}
			return null;
		} catch (CoreException e) {
			// If the project (or file) can not be loaded the annotation is definitely not
			// present
			return null;
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

	/**
	 * Opens the given Java element in a new editor as part of the given page.
	 *
	 * @param element the element to open
	 * @param page    the page as part of which the editor will be opened
	 */
	public static void openJavaElement(IJavaElement element, IWorkbenchPage page) {
		var path = element.getPath();
		Display.getDefault().asyncExec(() -> {
			try {
				IDE.openEditor(page, ResourcesPlugin.getWorkspace().getRoot().getFile(path));
			} catch (Exception e) {
				LOG.error("Failed to open the java code element", e);
			}
		});
	}

	/**
	 * Retrieves the global project explorer of Eclipse if it is open.
	 *
	 * @param page the page to search in
	 * @return the global project explorer or an empty optional if no project
	 *         explorer has been found
	 */
	public static Optional<ProjectExplorer> getProjectExplorer(IWorkbenchPage page) {
		return Optional.ofNullable((ProjectExplorer) page.findView(PROJECT_EXPLORER_ID));
	}
}
