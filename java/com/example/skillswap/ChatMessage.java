package com.example.skillswap;

public class ChatMessage {
    private String senderEmail;
    private String receiverEmail;
    private String messageText;
    private long timestamp;

    // Empty constructor for Firebase
    public ChatMessage() {
    }

    public ChatMessage(String senderEmail, String receiverEmail, String messageText, long timestamp) {
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public String getMessageText() {
        return messageText;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
