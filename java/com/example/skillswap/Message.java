package com.example.skillswap;

public class Message {
    private String sender;
    private String message;

    // Empty constructor required for Firebase
    public Message() {}

    // Correct constructor for sending messages
    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    // Getters for Firebase to read data
    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
