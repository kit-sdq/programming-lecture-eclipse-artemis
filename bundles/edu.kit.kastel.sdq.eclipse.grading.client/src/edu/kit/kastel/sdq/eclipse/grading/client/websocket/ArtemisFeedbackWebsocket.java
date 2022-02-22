package edu.kit.kastel.sdq.eclipse.grading.client.websocket;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.websocket.WebSocketContainer;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.container.grizzly.client.GrizzlyClientContainer;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.ArtemisWebsocketException;
import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.IWebsocketClient;
import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.WebsocketCallback;

public class ArtemisFeedbackWebsocket implements IWebsocketClient {
	private static final ILog log = Platform.getLog(ArtemisFeedbackWebsocket.class);

	private static final String WEBSOCKET_PATH = "/websocket/tracker";
	private static final String TOKEN_QUERY_PATH = "access_token";

	private String baseUrl;
	private String token;
	private String stompUrl;

	public ArtemisFeedbackWebsocket(String baseUrl) {
		this.baseUrl = baseUrl;
		this.stompUrl = buildStompUrl();
	}

	@Override
	public void connect(WebsocketCallback callback, String token) throws ArtemisWebsocketException {
		stompUrl = stompUrl + token;
		StandardWebSocketClient simpleWebSocketClient = configureStandartWebsocketClientWithSSL();
		SockJsClient sockJsClient = configureSockJsClient(simpleWebSocketClient);
		WebSocketStompClient stompClient = configureStompClient(sockJsClient);
		try {
			stompClient.connect(stompUrl, new ArtemisSockJsSessionHandler(callback)).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new ArtemisWebsocketException("Error can not connect to websocket", e);
		}
		log.info("Successfully connected to websocket");
	}

	private SockJsClient configureSockJsClient(StandardWebSocketClient simpleWebSocketClient) {
		List<Transport> transports = new ArrayList<Transport>();
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

	private StandardWebSocketClient configureStandartWebsocketClientWithSSL() throws ArtemisWebsocketException {
		WebSocketContainer webSocketContainer = ClientManager.createClient(GrizzlyClientContainer.class.getName());
		StandardWebSocketClient simpleWebSocketClient = new StandardWebSocketClient(webSocketContainer);

		Map<String, Object> properties = new HashMap<>();
		try {
			properties.put("org.apache.tomcat.websocket.SSL_CONTEXT", configureSSLContext());
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new ArtemisWebsocketException("Error can not configure SSL context for the websocket", e);
		}
		simpleWebSocketClient.setUserProperties(properties);
		return simpleWebSocketClient;
	}

	private SSLContext configureSSLContext()
			throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init((KeyStore) null);
		TrustManager[] trustAllCerts = trustManagerFactory.getTrustManagers();

		SSLContext sc;
		sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());

		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		return sc;
	}

	private String buildStompUrl() {
		return baseUrl + WEBSOCKET_PATH + "?" + TOKEN_QUERY_PATH + "=" + token;
	}
}
