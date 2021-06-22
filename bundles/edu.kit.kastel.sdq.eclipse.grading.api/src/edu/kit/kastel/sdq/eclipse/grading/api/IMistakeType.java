package edu.kit.kastel.sdq.eclipse.grading.api;

import java.util.List;

/**
 * Represents one type of mistakes from a rating group.
 *
 */
public interface IMistakeType {

	/**
	 * Calculate penalty using only the given annotations.
	 * @param annotations
	 * @return
	 */
	double calculatePenalty(List<IAnnotation> annotations);

	String getButtonName();

	String getMessage();

	IRatingGroup getRatingGroup();

	String getRatingGroupName();
}
