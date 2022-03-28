/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.api.client.websocket;

public class ArtemisWebsocketException extends Exception {
	private static final long serialVersionUID = -5452824453517740560L;

	public ArtemisWebsocketException(String msg) {
		super(msg);
	}

	public ArtemisWebsocketException(String message, Throwable cause) {
		super(message, cause);
	}

}
