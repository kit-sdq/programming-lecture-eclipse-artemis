/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.view.marker;

import org.eclipse.ui.views.markers.MarkerField;
import org.eclipse.ui.views.markers.MarkerItem;

/**
 * Class of the rating group field in the marker view
 * 
 * @see MarkerField
 *
 */
public class AssessmentRatingGroupField extends MarkerField {

	@Override
	public String getValue(MarkerItem item) {
		return item.getAttributeValue("ratingGroup", "");
	}

}
