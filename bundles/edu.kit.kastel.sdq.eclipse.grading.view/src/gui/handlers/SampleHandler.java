package gui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class SampleHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		ITextSelection textSelection = getTextSelectionChecked(event);
		Integer startLine = textSelection.getStartLine();
		Integer endLine = textSelection.getEndLine();
		Integer offset = textSelection.getOffset();
		String selectedText = textSelection.getText();
		int lenght = textSelection.getLength();

		IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActivePart();
		String name = workbenchPart.getSite().getPage().getActiveEditor().getEditorInput().getName();

		String workSpaceRootpath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + name;

		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow wb = workbench == null ? null : workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = wb == null ? null : window.getActivePage();

		IEditorPart editor = activePage == null ? null : activePage.getActiveEditor();

		IEditorInput input = editor == null ? null : editor.getEditorInput();
		IPath path = input instanceof FileEditorInput ? ((FileEditorInput) input).getPath() : null;
		if (path != null) {
			// Do something with path.
		}
		int lineOffset = 0;
		ITextEditor editor2 = editor.getAdapter(ITextEditor.class);
		if (editor != null) {
			IDocumentProvider provider = editor2.getDocumentProvider();
			IDocument document = provider.getDocument(editor2.getEditorInput());
			try {
				lineOffset = document.getLineOffset(startLine);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		IFile file = workbenchPart.getSite().getPage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
		IMarker marker = null;
		try {
			marker = file.createMarker("gui.assessment.marker");
			marker.setAttribute(IMarker.CHAR_START, lineOffset);
			marker.setAttribute(IMarker.CHAR_END, lineOffset + lenght + 1);
			marker.setAttribute(IMarker.MESSAGE, "this is a test tooltip");
			marker.setAttribute("errorTypeDescription", "This is a test description");
			marker.setAttribute("start", startLine + 1);
			marker.setAttribute("end", endLine + 1);
			marker.delete();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		MessageDialog.openInformation(window.getShell(), "Output for Selection", "startline: " + startLine + "\n"
				+ "endLine: " + offset + "\n" + "class: " + name + "\n" + path + "\n" + marker.toString());

		return null;
	}

	protected static ITextSelection getTextSelectionChecked(ExecutionEvent event) throws ExecutionException {
		ISelectionProvider selectionProvider = HandlerUtil.getActiveSiteChecked(event).getSelectionProvider();
		if (selectionProvider == null) {
			throw new ExecutionException("No selection provider found while executing " + event.getCommand().getId());
		}
		ISelection selection = selectionProvider.getSelection();
		if (!(selection instanceof ITextSelection)) {
			throw new ExecutionException(
					"Incorrect type for selection found while executing" + event.getCommand().getId());

		}
		return (ITextSelection) selection;
	}
}