/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.controller;

import edu.kit.kastel.eclipse.common.api.client.websocket.WebsocketCallback;

public interface IWebsocketController {

	/**
	 * Connect to websocket and subscribe to topic for submission and result.
	 * 
	 * @param callback
	 * @return true if successful.
	 */
	boolean connectToWebsocket(WebsocketCallback callback);
}
