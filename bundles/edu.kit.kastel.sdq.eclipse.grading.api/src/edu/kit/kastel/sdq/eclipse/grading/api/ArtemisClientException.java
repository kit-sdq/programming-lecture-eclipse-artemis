package edu.kit.kastel.sdq.eclipse.grading.api;

public class ArtemisClientException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public ArtemisClientException(String message) {
		super(message);
	}

	public ArtemisClientException(String message, Throwable cause) {
		super(message, cause);
	}
}
