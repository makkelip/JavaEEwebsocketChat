/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import model.Message;
import model.User;
/**
 *
 * @author Markus
 */

@ApplicationScoped
public class MessageSessionHandler {
    
    private int userId = 0;
    private final Set<Session> sessions = new HashSet<>();
    private final Set<Message> messages = new HashSet<>();
    private final Set<User> users = new HashSet<>();
    private final Map<Session,User> usersOnline = new HashMap<>();
    
    public void addSession(Session session) {
        sessions.add(session);
    }
    
    public void removeSession(Session session) {
        sessions.remove(session);
    }
    
    private void sendToAllConnectedSessions(JsonObject message) {
        for (Session session : sessions) {
            sendToSession(session, message);
        }
    }

    private void sendToSession(Session session, JsonObject message) {
            try {
                session.getBasicRemote().sendText(message.toString());
            } catch (IOException ex) {
                sessions.remove(session);
                Logger.getLogger(MessageSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    private boolean userOnline(String name) {
        for (User u : usersOnline.values()) {
            if (name.equals(u.getName())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean userExists(String name) {
        for (User u : users) {
            if (name.equals(u.getName())) {
                return true;
            }
        }
        return false;
    }
    
    public void newMessage(JsonObject messageJson, Session session) {
        Message m = new Message(messageJson.getString("message"), usersOnline.get(session).getId());
        messages.add(m);
        sendToAllConnectedSessions(messageJson);
    }
    
    public void whoOnline(Session session) {
        JsonArrayBuilder whoOnline = JsonProvider.provider().createArrayBuilder();
        for(User u : usersOnline.values()) {
            whoOnline.add(u.getName());
        }
        JsonObjectBuilder response = JsonProvider.provider().createObjectBuilder();
        response.add("action", "whoOnlineResponse")
                .add("users", whoOnline.build());
        
        sendToSession(session, response.build());
    }
    
    public void login(JsonObject info, Session session) {
        JsonObjectBuilder loginResponse = JsonProvider.provider().createObjectBuilder();
        
        if(userOnline(info.getString("name"))) {
            loginResponse.add("action", "loginFailed")
                .add("note", "Username already in use");
        } else if (userExists(info.getString("name"))) {
            loginResponse.add("action", "loginSuccess")
                .add("note","Username found logging in");
        } else if (info.getString("name").isEmpty()){
            loginResponse.add("action", "loginFailed")
                    .add("note", "nameEmpty");
        }else {
            users.add(new User(info.getString("name"),userId));
            userId++;
            loginResponse.add("action", "loginSuccess")
                    .add("note", "Account created logging in");
        }
        
        sendToSession(session, loginResponse.build());
    }
}
