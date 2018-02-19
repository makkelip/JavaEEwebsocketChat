/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websocket;

import java.io.StringReader;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Markus
 */

@ApplicationScoped
@ServerEndpoint("/actions")
public class MessageWebSocketServer {
    
    @Inject
    private MessageSessionHandler sessionHandler;
    
    @OnOpen
    public void onOpen(Session session) {
        sessionHandler.addSession(session);
    }
    
    @OnMessage
    public void handleMessage(String message, Session session) {
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();
            
            if ("message".equals(jsonMessage.getString("action"))) {
                
            } else if ("whoOnline".equals(jsonMessage.getString("action"))) {
                
            } else if ("login".equals(jsonMessage.getString("action"))) {
                sessionHandler.login(jsonMessage, session);
            }
        }
    }
}
