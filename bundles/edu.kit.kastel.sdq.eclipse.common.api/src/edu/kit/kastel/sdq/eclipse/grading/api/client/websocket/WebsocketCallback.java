/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.api.client.websocket;

/**
 * Callback object used to define handling for incoming websocket events.
 */
public interface WebsocketCallback {

	/**
	 * Handles events send over result topic.
	 * 
	 * @param send payload via topic
	 */
	void handleResult(Object payload);

	/**
	 * Handles events send over submission topic.
	 * 
	 * @param send payload via topic
	 */
	void handleSubmission(Object payload);

	/**
	 * Handles general websocket errors.
	 * 
	 * @param the thrown exception.
	 */
	void handleException(Throwable e);

	/**
	 * Handles transport error.
	 * 
	 * @param the thrown exception.
	 */
	void handleTransportError(Throwable e);
}
