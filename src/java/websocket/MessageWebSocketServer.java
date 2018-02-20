/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websocket;


import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

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
    public void open(Session session) {
        sessionHandler.addSession(session);
    }
    
    @OnClose
    public void close(Session session) {
        sessionHandler.removeSession(session);
    }
    
    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(MessageWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }
    
    @OnMessage
    public void handleMessage(String message, Session session) {
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();
            
            if ("message".equals(jsonMessage.getString("action"))) {
                sessionHandler.newMessage(jsonMessage, session);
            } else if ("whoOnline".equals(jsonMessage.getString("action"))) {
                sessionHandler.whoOnline(session);
            } else if ("login".equals(jsonMessage.getString("action"))) {
                sessionHandler.login(jsonMessage, session);
            }
        }
    }
}
