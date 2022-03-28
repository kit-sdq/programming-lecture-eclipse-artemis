/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.api.controller;

import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.WebsocketCallback;

public interface IWebsocketController {

	/**
	 * Connect to websocket and subscribe to topic for submission and result.
	 * 
	 * @param callback
	 * @return true if successful.
	 */
	boolean connectToWebsocket(WebsocketCallback callback);
}
