package edu.kit.kastel.sdq.eclipse.grading.api.client.websocket;

public interface WebsocketCallback {
	void handleResult(Object payload);

	void handleSubmission(Object payload);

	void handleException(Throwable e);

	void handleTransportError(Throwable e);
}
