/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.websocket;

import java.lang.reflect.Type;
import java.util.List;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import edu.kit.kastel.eclipse.common.api.client.websocket.WebsocketCallback;

public class ArtemisSockJsSessionHandler extends StompSessionHandlerAdapter {
	private static final ILog log = Platform.getLog(ArtemisSockJsSessionHandler.class);

	private static final String TOPIC_NEW_SUBMISSION = "/user/topic/newSubmissions";
	private static final String TOPIC_NEW_RESULT = "/user/topic/newResults";

	private WebsocketCallback callback;

	public ArtemisSockJsSessionHandler(WebsocketCallback callback) {
		this.callback = callback;
	}

	@Override
	public Type getPayloadType(StompHeaders headers) {
		return Object.class;
	}

	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		List<String> topics = headers.get("destination");
		if (!topics.isEmpty() && topics.get(0).equals(TOPIC_NEW_RESULT)) {
			log.info("Websocket - new result received!");
			this.callback.handleResult(payload);
		} else if (!topics.isEmpty() && topics.get(0).equals(TOPIC_NEW_SUBMISSION)) {
			log.info("Websocket - new submission received!");
			this.callback.handleSubmission(payload);
		} else {
			log.error("Websocket - received unknown frame");
		}
	}

	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		session.subscribe(TOPIC_NEW_SUBMISSION, this);
		session.subscribe(TOPIC_NEW_RESULT, this);
	}

	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
		log.error("WebsocketError", exception);
		this.callback.handleException(exception);
	}

	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		log.error("WebsocketError", exception);
		this.callback.handleException(exception);
	}
}