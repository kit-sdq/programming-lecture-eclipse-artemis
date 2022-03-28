/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.view.marker;

import org.eclipse.ui.views.markers.MarkerField;
import org.eclipse.ui.views.markers.MarkerItem;

/**
 * Class of the custom penalty field in the marker view
 * 
 * @See {@link MarkerField}
 *
 */
public class AssessmentMarkerCustomPenalyField extends MarkerField {

	@Override
	public String getValue(MarkerItem item) {
		return item.getAttributeValue("customPenalty", "");
	}

}
