package edu.kit.kastel.eclipse.student.view.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import edu.kit.kastel.eclipse.common.view.marker.AssessmentMarkerView;
import edu.kit.kastel.eclipse.student.view.ui.ArtemisStudentView;

public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.2F, editorArea);
		left.addView(IPageLayout.ID_PROJECT_EXPLORER);

		IFolderLayout rightUp = layout.createFolder("rightUp", IPageLayout.RIGHT, 0.7F, editorArea);
		rightUp.addView(ArtemisStudentView.class.getName());

		IFolderLayout rightDown = layout.createFolder("rightDown", IPageLayout.BOTTOM, 0.7F, "rightUp");
		rightDown.addView(IPageLayout.ID_OUTLINE);

		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.6F, editorArea);
		bottom.addView(AssessmentMarkerView.class.getName());
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
	}
}
