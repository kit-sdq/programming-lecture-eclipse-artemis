package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import java.io.IOException;

/**
 * Exception for when something is wrong with processing Artemis Data.
 *
 */
public class ArtemisProcessingException extends IOException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	public ArtemisProcessingException(String message) {
		super(message);
	}
}
