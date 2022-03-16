package edu.kit.kastel.sdq.eclipse.grading.api.client.websocket;

/**
 * Client to connect to Artemis websocket. It uses STOMP and SockJS. It
 * subscribes to the result and submission topic. It is secured via SSL.
 */
public interface IWebsocketClient {
	/**
	 * Connect to websocket and subscribe to result and submission topic. For SSL
	 * token is used to authenticate. The callback defines handling for incoming
	 * events or errors.
	 * 
	 * @param callback it is called after an incoming event.
	 * @param token
	 * @throws ArtemisWebsocketException
	 */
	void connect(WebsocketCallback callback, String token) throws ArtemisWebsocketException;
}
