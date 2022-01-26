package edu.kit.kastel.sdq.eclipse.grading.api.client.websocket;

public interface IWebsocketClient {
	void connect(WebsocketCallback callback, String token) throws ArtemisWebsocketException;
}
