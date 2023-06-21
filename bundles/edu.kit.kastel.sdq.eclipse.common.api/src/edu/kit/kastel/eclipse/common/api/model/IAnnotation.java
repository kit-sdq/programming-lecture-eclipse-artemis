/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.api.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * An annotation is one specific occurrence of a IMistakeType. There might be
 * multiple Annotations.<br>
 *
 * Note that penalty calculation is done collectively:
 * {@link IMistakeType#calculate(List)}} You may define a custom penalty which
 * might be used by some MistakeType (more precise: by its PenaltyRule). Also,
 * you may define a custom message.
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

	IMistakeType getMistakeType();

	String getUUID();

	static String createID() {
		return String.format("%d_%s", System.currentTimeMillis(), UUID.randomUUID());
	}

}
