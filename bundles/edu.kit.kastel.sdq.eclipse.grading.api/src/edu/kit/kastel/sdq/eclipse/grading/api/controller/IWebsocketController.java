package edu.kit.kastel.sdq.eclipse.grading.api.controller;

import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.WebsocketCallback;

public interface IWebsocketController {
    boolean connectToWebsocket(WebsocketCallback callback);
}
