/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websocket;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
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
    private final Stack<Message> messages = new Stack<>();
    private final Set<User> users = new HashSet<>();
    private final Map<Session,User> usersOnline = new HashMap<>();
    
    public void addSession(Session session) {
        sessions.add(session);
    }
    
    public void removeSession(Session session) {
        usersOnline.remove(session);
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
        if (users.stream().anyMatch((u) -> (name.equals(u.getName())))) {
            return true;
        }
        return false;
    }
    
    private User getUserById(int id) {
        for (User u : users) {
            if (id == u.getId()) {
                return u;
            }
        }
        return null;
    }
    
    private User getUserByName(String name) {
        for (User u : users) {
            if (name == u.getName()) {
                return u;
            }
        }
        return null;
    }
    
    public void newMessage(JsonObject messageJson, Session session) {
        Message msg = new Message(messageJson.getString("message"), usersOnline.get(session).getId());
        messages.add(msg);
        JsonObjectBuilder messageBuilder = JsonProvider.provider().createObjectBuilder();
        messageBuilder.add("action", "message")
                .add("message", messageJson.getString("message"))
                .add("name", getUserById(msg.getSenderId()).getName());
        sendToAllConnectedSessions(messageBuilder.build());
        
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
    
    public void sendAllMessages(Session session) {
        JsonArrayBuilder allMessages = JsonProvider.provider().createArrayBuilder();
        JsonObjectBuilder singleMessage = JsonProvider.provider().createObjectBuilder();
        for (Message m : messages) {
            singleMessage.add("sender", getUserById(m.getSenderId()).getName());
            singleMessage.add("text", m.getMessage());
            allMessages.add(singleMessage.build());
        }
        JsonObjectBuilder contained = JsonProvider.provider().createObjectBuilder()
                .add("action", "getMessages")
                .add("messages", allMessages.build());
        sendToSession(session, contained.build());
    }
    
    public void login(JsonObject info, Session session) {
        JsonObjectBuilder loginResponse = JsonProvider.provider().createObjectBuilder();
        
        if(userOnline(info.getString("name"))) {
            loginResponse.add("action", "loginFailed")
                .add("note", "Username already in use");
        } else if (info.getString("name").isEmpty()){
            loginResponse.add("action", "loginFailed")
                    .add("note", "nameEmpty");
        } else {
            if (userExists(info.getString("name"))) {
                loginResponse.add("action", "loginSuccess")
                    .add("note","Username found logging in");
            }else {
                users.add(new User(info.getString("name"),userId));
                userId++;
                loginResponse.add("action", "loginSuccess")
                        .add("note", "Account created logging in");
            }
            usersOnline.put(session, getUserByName(info.getString("name")));
        }
        sendToSession(session, loginResponse.build());
    }
}
