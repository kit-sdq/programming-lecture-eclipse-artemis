package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import java.io.IOException;

/**
 * Exception for when something is wrong with processing Artemis Data.
 *
 */
public final class ArtemisProcessingException extends IOException {
	private static final long serialVersionUID = 6431302083638366127L;

	public ArtemisProcessingException(String message) {
		super(message);
	}
}
