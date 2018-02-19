/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Markus
 */
public class Message {
    
    private final String message;
    private final int senderId;
    
    public Message(String message, int senderId) {
        this.message = message;
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public int getSenderId() {
        return senderId;
    }
    
}
