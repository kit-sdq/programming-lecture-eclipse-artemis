package edu.kit.kastel.sdq.eclipse.grading.api.model;

import java.util.Optional;
import java.util.UUID;

/**
 * An annotation is one specific occurrence of a IMistakeType. There might be
 * multiple Annotations.
 *
 * Note that penalty calculation is done collectively:
 * {@link IMistakeType#calculatePenalty(java.util.List)} You may define a custom
 * penalty which might be used by some MistakeType (more precise: by its
 * PenaltyRule). Also, you may define a custom message.
 */
public interface IAnnotation {

	/**
	 *
	 * @return the relative path to the class, formatted like
	 *         "src/edu/kit/informatik/BubbleSort" (as that is what artemis
	 *         requires)
	 */
	String getClassFilePath();

	Optional<String> getCustomMessage();

	Optional<Double> getCustomPenalty();

	/**
	 *
	 *
	 * @return the start line starting at TODO 0 or 1.
	 */
	int getStartLine();

	/**
	 *
	 *
	 * @return the start line starting at TODO 0 or 1.
	 */
	int getEndLine();

	/**
	 *
	 * @return additional encoding of the start (counts from file start, eclipse GUI
	 *         requires this)
	 */
	int getMarkerCharEnd();

	/**
	 *
	 * @return additional encoding of the start (counts from file start, eclipse GUI
	 *         requires this)
	 */
	int getMarkerCharStart();

	/**
	 *
	 * @return the type of mistake that this annotation is an occurance of.
	 */
	IMistakeType getMistakeType();

	String getUUID();

	static String createUUID() {
		// TODO Better UUID Generation ..
		return UUID.randomUUID().toString();
	}

}
