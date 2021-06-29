package gui.marker;

import org.eclipse.ui.views.markers.MarkerField;
import org.eclipse.ui.views.markers.MarkerItem;

public class AssessmentMarkerPositionField extends MarkerField {

	@Override
	public String getValue(MarkerItem item) {
		int startLine = item.getMarker().getAttribute("start", -1);
		int endLine = item.getMarker().getAttribute("end", -1);
		return "[" + startLine + "," + endLine + "]";
	}

}
