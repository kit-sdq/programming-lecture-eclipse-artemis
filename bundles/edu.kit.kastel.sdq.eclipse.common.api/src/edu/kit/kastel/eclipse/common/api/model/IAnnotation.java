/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.model;

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
	 * The relative path to the class, formatted like
	 * {@code src/edu/kit/informatik/BubbleSort} (as that is what Artemis requires)
	 */
	String getClassFilePath();

	Optional<String> getCustomMessage();

	Optional<Double> getCustomPenalty();

	int getStartLine();

	int getEndLine();

	/**
	 *
	 * @return the type of mistake that this annotation is an occurance of.
	 */
	IMistakeType getMistakeType();

	String getUUID();

	static String createUUID() {
		// TODO Better UUID Generation ..
		return String.valueOf(UUID.randomUUID());
	}

}
