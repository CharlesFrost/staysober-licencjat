package com.example.stay_sober_android.models;

public class Request {
    private String sender;
    private String receiver;
    private String senderName;

    public Request() {
    }

    public Request(String sender, String receiver, String senderName) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderName = senderName;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

}
