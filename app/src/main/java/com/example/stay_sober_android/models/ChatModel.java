package com.example.stay_sober_android.models;

public class ChatModel {
    private String firstPerson;
    private String secondPerson;
    private String chatId;

    public ChatModel() {
    }

    public ChatModel(String firstPerson, String secondPerson, String chatId) {
        this.firstPerson = firstPerson;
        this.secondPerson = secondPerson;
        this.chatId = chatId;
    }

    public String getFirstPerson() {
        return firstPerson;
    }

    public void setFirstPerson(String firstPerson) {
        this.firstPerson = firstPerson;
    }

    public String getSecondPerson() {
        return secondPerson;
    }

    public void setSecondPerson(String secondPerson) {
        this.secondPerson = secondPerson;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
