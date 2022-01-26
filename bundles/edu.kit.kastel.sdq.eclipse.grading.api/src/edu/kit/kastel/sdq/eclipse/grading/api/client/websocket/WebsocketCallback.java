package edu.kit.kastel.sdq.eclipse.grading.api.client.websocket;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;

public interface WebsocketCallback {
	void handleFrame(Object payload);
	void handleFrame(ResultsDTO result);
	void handleException(Throwable e);
	void handleTransportError(Throwable e);
}
