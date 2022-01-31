package edu.kit.kastel.sdq.eclipse.grading.client.websocket;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.WebsocketCallback;

public class ArtemisSockJsSessionHandler extends StompSessionHandlerAdapter  {
	
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
		if (!headers.get("destination").isEmpty() && headers.get("destination").get(0).equals(TOPIC_NEW_RESULT)) {
			callback.handleResult(payload);
		} else if(!headers.get("destination").isEmpty() && headers.get("destination").get(0).equals(TOPIC_NEW_SUBMISSION)) {
			callback.handleSubmission(payload);
		}
	}
	
	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		session.subscribe(TOPIC_NEW_SUBMISSION, this);
		session.subscribe(TOPIC_NEW_RESULT, this);
	}
	
	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
			Throwable exception) {
		callback.handleException(exception);
	}
	
	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		callback.handleTransportError(exception);
	}
}