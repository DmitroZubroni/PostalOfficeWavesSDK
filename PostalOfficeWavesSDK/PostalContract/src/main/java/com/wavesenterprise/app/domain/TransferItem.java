package com.wavesenterprise.app.domain;

public class TransferItem {
    /** Уникальный идентификатор перевода */
    public String transferId;

    /** Адрес отправителя перевода в блокчейн-системе */
    public String sender;

    /** Адрес получателя перевода в блокчейн-системе */
    public String recipient;

    /** Сумма перевода в токенах WEST */
    public int amount;

    /** Время жизни перевода в днях */
    public int timeLive;

    /** Статус перевода (ACTIVE, REFUSE, ACCEPTED, CANCELLED) */
    public String status;

    /** Временная метка создания перевода (в миллисекундах) */
    public long createdAt;

    /**
     * Конструктор денежного перевода
     * @param transferId Уникальный идентификатор перевода
     * @param sender Адрес отправителя
     * @param recipient Адрес получателя
     * @param amount Сумма перевода в WEST
     * @param timeLive Время жизни перевода в днях
     * @param status Статус перевода
     */
    public TransferItem(String transferId, String sender, String recipient, int amount, int timeLive, String status) {
        this.transferId = transferId;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timeLive = timeLive;
        this.status = status;
        this.createdAt = System.currentTimeMillis();
    }

    /** Конструктор по умолчанию для десериализации */
    public TransferItem() {}

    /**
     * Проверить, истекло ли время жизни перевода
     * @return true если время жизни истекло, false если еще действует
     */
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        long createdTime = this.createdAt;
        // 1 день в задании = 5 секунд реального времени
        long daysPassed = (currentTime - createdTime) / (1000 * 5);
        return daysPassed >= timeLive;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
