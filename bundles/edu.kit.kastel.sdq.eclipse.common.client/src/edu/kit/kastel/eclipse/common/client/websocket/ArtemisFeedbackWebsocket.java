/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client.websocket;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import edu.kit.kastel.eclipse.common.api.client.websocket.ArtemisWebsocketException;
import edu.kit.kastel.eclipse.common.api.client.websocket.IWebsocketClient;
import edu.kit.kastel.eclipse.common.api.client.websocket.WebsocketCallback;
import edu.kit.kastel.eclipse.common.api.messages.Messages;

public class ArtemisFeedbackWebsocket implements IWebsocketClient {
	private static final ILog log = Platform.getLog(ArtemisFeedbackWebsocket.class);

	private static final String WEBSOCKET_PATH = "/websocket";
	private static final String TOKEN_COOKIE_NAME = "jwt";

	private String baseUrl;

	public ArtemisFeedbackWebsocket(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public void connect(WebsocketCallback callback, String token) throws ArtemisWebsocketException {
		if (this.nullOrEmpty(this.baseUrl) || this.nullOrEmpty(token)) {
			throw new ArtemisWebsocketException(Messages.CLIENT_NO_BASE_URL);
		}
		String stompUrl = this.baseUrl + WEBSOCKET_PATH;
		var jettyClient = new org.eclipse.jetty.websocket.client.WebSocketClient();
		jettyClient.setIdleTimeout(Duration.ZERO);
		WebSocketClient simpleWebSocketClient = new JettyWebSocketClient(jettyClient);
		SockJsClient sockJsClient = this.configureSockJsClient(simpleWebSocketClient);
		WebSocketStompClient stompClient = this.configureStompClient(sockJsClient);
		try {
			WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
			headers.add(WebSocketHttpHeaders.COOKIE, TOKEN_COOKIE_NAME + "=" + token);
			stompClient.start();
			stompClient.connectAsync(stompUrl, headers, new ArtemisSockJsSessionHandler(callback)).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new ArtemisWebsocketException(Messages.CLIENT_NO_WEBSOCKET, e);
		}
		log.info("Successfully connected to websocket");
	}

	private SockJsClient configureSockJsClient(WebSocketClient simpleWebSocketClient) {
		List<Transport> transports = new ArrayList<>();
		transports.add(new WebSocketTransport(simpleWebSocketClient));

		SockJsClient sockJsClient = new SockJsClient(transports);
		sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());
		return sockJsClient;
	}

	private WebSocketStompClient configureStompClient(SockJsClient sockJsClient) {
		WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		return stompClient;
	}

	private boolean nullOrEmpty(String str) {
		return str == null || str.isBlank();
	}
}
