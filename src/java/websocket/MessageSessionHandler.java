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
import javax.json.JsonObject;
import javax.websocket.Session;
import model.Message;
import model.User;
/**
 *
 * @author Markus
 */

@ApplicationScoped
public class MessageSessionHandler {
    
    
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
        if(usersOnline.containsKey(session)) {
            try {
                session.getBasicRemote().sendText(message.toString());
            } catch (IOException ex) {
                sessions.remove(session);
                Logger.getLogger(MessageSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
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
    
    public void login(JsonObject info, Session session) {
        if(userOnline(info.getString("name"))) {
        
        } else if () {
            
        } else {
            User newUser = new User
        }
    }
}
