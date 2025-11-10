package com.wavesenterprise.app.domain;

public class Transfer {

    private String sender; // отправитель
    private String recipient; // получатель
    private int value; // сумма
    private int liveTime; // время жизни перевода (в днях)

    public Transfer(String sender, String recipient, int value, int liveTime) {
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
        this.liveTime = liveTime;
    }

    public Transfer() {}

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public int getValue() {
        return value;
    }

    public int getLiveTime() {
        return liveTime;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setLiveTime(int liveTime) {
        this.liveTime = liveTime;
    }
}
