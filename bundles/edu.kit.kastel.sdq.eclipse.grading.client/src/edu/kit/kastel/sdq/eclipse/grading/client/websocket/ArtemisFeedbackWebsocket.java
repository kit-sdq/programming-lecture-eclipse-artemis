package edu.kit.kastel.sdq.eclipse.grading.client.websocket;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
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
	private static final String WEBSOCKET_PATH = "/websocket/tracker";
	private static final String TOKEN_QUERY_PATH = "access_token";
	
	private String baseUrl = "";
	private String token = "";
	private String stompUrl = "";

	public ArtemisFeedbackWebsocket(String baseUrl) {
		this.baseUrl = baseUrl;
		this.stompUrl = buildStompUrl();
	}
	
	@Override
	public void connect(WebsocketCallback callback, String token) throws ArtemisWebsocketException {
		stompUrl = stompUrl+token;
		StandardWebSocketClient simpleWebSocketClient = configureStandartWebsocketClientWithSSl();
		SockJsClient sockJsClient = configureSockJsClient(simpleWebSocketClient);
		WebSocketStompClient stompClient = configureStompClient(sockJsClient);
		try {
			stompClient.connect(stompUrl, new ArtemisSockJsSessionHandler(callback)).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new ArtemisWebsocketException("Error can not connect to websocket", e);
		}
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

	private StandardWebSocketClient configureStandartWebsocketClientWithSSl() throws ArtemisWebsocketException {
		StandardWebSocketClient simpleWebSocketClient = new StandardWebSocketClient();

		Map<String, Object> properties = new HashMap<>();
		try {
			properties.put("org.apache.tomcat.websocket.SSL_CONTEXT", configureSSLContext());
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			throw new ArtemisWebsocketException("Error can not configure SSL context for the websocket", e);
		}
		simpleWebSocketClient.setUserProperties(properties);
		return simpleWebSocketClient;
	}

	private SSLContext configureSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		}};

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
