package com.wavesenterprise.app.domain;

public class TransferItem {

    public String transferId;
    public String sender; // отправитель
    public String recipient; // получатель
    public int amount; // сумма
    public int timeLive; // время жизни перевода
    public String status; // статус перевода


    public TransferItem( String transferId, String sender, String recipient, int amount, int timeLive, String status) {
        this.transferId = transferId;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timeLive = timeLive;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public TransferItem() {}

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getTimeLive() {
        return timeLive;
    }

    public void setTimeLive(int timeLive) {
        this.timeLive = timeLive;
    }
}
