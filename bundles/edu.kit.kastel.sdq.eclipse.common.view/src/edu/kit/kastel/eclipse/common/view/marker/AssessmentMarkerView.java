/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.view.marker;

import java.lang.reflect.Field;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.ui.internal.views.markers.ExtendedMarkersView;
import org.eclipse.ui.internal.views.markers.MarkersTreeViewer;
import org.eclipse.ui.views.markers.MarkerSupportView;

import edu.kit.kastel.eclipse.common.view.utilities.AssessmentUtilities;

/**
 * Class of the marker view
 *
 * @see MarkerSupportView
 *
 */
@SuppressWarnings("restriction")
public class AssessmentMarkerView extends MarkerSupportView {

	private static final ILog log = Platform.getLog(AssessmentMarkerView.class);

	public AssessmentMarkerView() {
		super(AssessmentUtilities.MARKER_VIEW_ID);
	}

	public void addDoubleClickListener(IDoubleClickListener doubleClickListener) {
		try {
			Field viewerField = ExtendedMarkersView.class.getDeclaredField("viewer");
			viewerField.setAccessible(true);
			MarkersTreeViewer viewer = (MarkersTreeViewer) viewerField.get(this);
			viewer.addDoubleClickListener(doubleClickListener);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			log.error("Could not attach DoubleClickListener to AssessmentMarkerView", e);
		}
	}

}
