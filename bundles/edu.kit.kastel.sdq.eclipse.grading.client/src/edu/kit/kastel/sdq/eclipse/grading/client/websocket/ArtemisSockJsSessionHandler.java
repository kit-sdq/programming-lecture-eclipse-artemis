package edu.kit.kastel.sdq.eclipse.grading.client.websocket;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

public class ArtemisSockJsSessionHandler implements StompSessionHandler  {
	
	private static final String topic1 = "/user/topic/newSubmissions";
	private static final String topic2 = "/user/topic/newResults";

	@Override
	public Type getPayloadType(StompHeaders headers) {
		// TODO Auto-generated method stub
		System.out.println("--------payload---------");
		return Object.class;
	}
	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		// TODO Auto-generated method stub
		System.out.println("----------frame-------");
		System.out.println(payload.toString());
	}
	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		System.out.println("---------afterConnected--------");
		session.subscribe(topic1, this);
		session.subscribe(topic2, this);
	}
	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
			Throwable exception) {
		// TODO Auto-generated method stub
		exception.printStackTrace();
	}
	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		// TODO Auto-generated method stub
		exception.printStackTrace();
	}
}