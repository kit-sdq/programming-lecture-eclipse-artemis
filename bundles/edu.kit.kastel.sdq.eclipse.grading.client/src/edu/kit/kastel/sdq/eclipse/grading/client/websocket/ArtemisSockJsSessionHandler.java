package edu.kit.kastel.sdq.eclipse.grading.client.websocket;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.WebsocketCallback;

public class ArtemisSockJsSessionHandler implements StompSessionHandler  {
	
	private static final String TOPIC_NEW_SUBMISSION = "/user/topic/newSubmissions";
	private static final String TOPIC_NEW_RESULT = "/user/topic/newResults";
	
	private WebsocketCallback callback;
	
	public ArtemisSockJsSessionHandler(WebsocketCallback callback) {
		this.callback = callback;
	}

	@Override
	public Type getPayloadType(StompHeaders headers) {
		System.out.println("--------payload---------");
		return Object.class;
	}
	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		System.out.println("----------frame-------");
		callback.handleFrame(payload);
	}
	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		System.out.println("---------afterConnected--123------");
		session.subscribe(TOPIC_NEW_SUBMISSION, this);
		session.subscribe(TOPIC_NEW_RESULT, this);
	}
	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
			Throwable exception) {
		System.out.println("---------------------------");
		exception.printStackTrace();
		callback.handleException(exception);
	}
	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		System.out.println("---------------------------3");
		exception.printStackTrace();
		callback.handleTransportError(exception);
	}
}