package edu.kit.kastel.sdq.eclipse.grading.api;

import java.util.Optional;

/**
 * An annotation is one specific occurrence of a IMistakeType.
 * There might be multiple Annotation
 *
 * You may define a custom penalty which might be used by some MistakeType (more precise: by its PenaltyRule).
 * Also, you may define a custom message.
 */
public interface IAnnotation {

	Optional<String> getCustomMessage();

	Optional<Double> getCustomPenalty();

	/**
	 *
	 *
	 * @return the start line starting at TODO 0 or 1.
	 */
	int getEndLine();

	/**
	 *
	 * @return the "fully classified class name", formatted like "src/edu/kit/informatik/BubbleSort" (as that is what artemis requires)
	 */
	String getFullyClassifiedClassName();

	int getId();

	IMistakeType getMistakeType();

	/**
	 *
	 *
	 * @return the start line starting at TODO 0 or 1.
	 */
	int getStartLine();
}
