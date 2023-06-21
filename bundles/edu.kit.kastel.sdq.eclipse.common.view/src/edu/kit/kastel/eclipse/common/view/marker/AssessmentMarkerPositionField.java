/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.view.marker;

import org.eclipse.ui.views.markers.MarkerField;
import org.eclipse.ui.views.markers.MarkerItem;

/**
 * Class of the position field in the marker view. Converted to
 * [startLine,endLine]
 *
 * @see MarkerField
 *
 */
public class AssessmentMarkerPositionField extends MarkerField {

	@Override
	public String getValue(MarkerItem item) {
		// Lines are indexed starting 0.
		int startLine = item.getMarker().getAttribute("start", -1) + 1;
		int endLine = item.getMarker().getAttribute("end", -1) + 1;
		return "[" + startLine + "," + endLine + "]";
	}

	@Override
	public int compare(MarkerItem item1, MarkerItem item2) {
		int startLine1 = item1.getMarker().getAttribute("start", -1) + 1;
		int endLine1 = item1.getMarker().getAttribute("end", -1) + 1;
		int startLine2 = item2.getMarker().getAttribute("start", -1) + 1;
		int endLine2 = item2.getMarker().getAttribute("end", -1) + 1;

		if (startLine1 == startLine2) {
			return Integer.compare(endLine1, endLine2);
		}
		return Integer.compare(startLine1, startLine2);
	}

}
