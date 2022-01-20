package edu.kit.kastel.sdq.eclipse.grading.client.websocket;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

public class ArtemisFeedbackWebsocket {
	private static final String URL = "https://artemis-test.ipd.kit.edu/websocket/tracker?access_token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0LXN0dWRlbnQiLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNjQ1MzkzMDc2fQ.VfXXlBnRG39R8zqmDZn5tROzl6AuSaciTcqvuitJ6V3Xe-GVBCrbHsQToamWbkYRZhiAGnj-4zPwwwnCGZKV9Q";
	private StompSession session;

	public void connect() {
		StandardWebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
		 
		TrustManager[] trustAllCerts = new TrustManager[]{
		        new X509TrustManager() {
		            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		                return null;
		            }
		            public void checkClientTrusted(
		                java.security.cert.X509Certificate[] certs, String authType) {
		            }
		            public void checkServerTrusted(
		                java.security.cert.X509Certificate[] certs, String authType) {
		            }
		        }
		    };


        SSLContext sc;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		} catch (KeyManagementException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        Map<String, Object> properties = new HashMap<>();
        properties.put("org.apache.tomcat.websocket.SSL_CONTEXT", sc);
        simpleWebSocketClient.setUserProperties(properties);

        List<Transport> transports = new ArrayList();
        transports.add(new WebSocketTransport(simpleWebSocketClient));

        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MessageConverter() {
            @Override
            public Object fromMessage(Message<?> message, Class<?> aClass) {
                return new String((byte[])message.getPayload());
            }

            @Override
            public Message<?> toMessage(Object o, MessageHeaders messageHeaders) {
                return null;
            }
        });
		String stompUrl = URL;
		ArtemisSockJsSessionHandler handler = new ArtemisSockJsSessionHandler();
		try {
			// stompClient.connect(stompUrl, new LocalStompSessionHandler());
			session = stompClient.connect(stompUrl, handler).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(session.isConnected());
		new Scanner(System.in).nextLine(); // Don't close immediately.
	}
}
