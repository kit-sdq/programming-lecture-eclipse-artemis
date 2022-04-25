/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.marker;

import org.eclipse.ui.views.markers.MarkerField;
import org.eclipse.ui.views.markers.MarkerItem;

/**
 * Class of the position field in the marker view. Converted to
 * [startLine,endLine]
 *
 * @See {@link MarkerField}
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

}
