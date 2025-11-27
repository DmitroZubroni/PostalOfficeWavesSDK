package com.wavesenterprise.app.domain;
import com.wavesenterprise.app.api.IContract.PostalItemType.*;

import java.util.ArrayList;
import java.util.List;

public class PostItem {
    /** Уникальный трек-номер отправления (генерируется автоматически) */
    public String trackNumber;

    /** Адрес отправителя в блокчейн-системе */
    public String sender;

    /** Адрес получателя в блокчейн-системе */
    public String recipient;

    /** Тип отправления (MESSAGE, POSTAL, BANDEROL) */
    public String type;

    /** Класс отправления (1, 2, 3) */
    public byte classDeparture;

    /** Срок доставки в днях (рассчитывается автоматически по классу) */
    public int timeDeparture;

    /** Стоимость доставки в WEST (рассчитывается автоматически) */
    public double priceDeparture;

    /** Вес отправления в килограммах (от 1 до 10 кг) */
    public int weight;

    /** Объявленная ценность отправления (по умолчанию 0) */
    public int importantValue;

    /** Итоговая стоимость отправления в WEST (рассчитывается автоматически) */
    public double finalCost;

    /** Адрес назначения (индекс, город, улица, дом) */
    public String addressTo;

    /** Адрес отправления (индекс, город, улица, дом) */
    public String addressFrom;

    /** Статус отправления (ACTIVE, REFUSE, ACCEPTED) */
    public String status;

    /** История транзитов отправления через почтовые отделения */
    public List<TransitPoint> transitHistory;

    /** Временная метка создания отправления (в миллисекундах) */
    public long createdAt;

    /**
     * Основной конструктор почтового отправления
     * @param trackNumber Уникальный трек-номер
     * @param sender Адрес отправителя
     * @param recipient Адрес получателя
     * @param type Тип отправления
     * @param classDeparture Класс отправления
     * @param weight Вес отправления
     * @param importantValue Объявленная ценность
     * @param addressTo Адрес назначения
     * @param addressFrom Адрес отправления
     * @param status Статус отправления
     */
    public PostItem(String trackNumber, String sender, String recipient, String type, byte classDeparture,
                    int weight, int importantValue, String addressTo, String addressFrom, String status) {
        this.trackNumber = trackNumber;
        this.sender = sender;
        this.recipient = recipient;
        this.type = type;
        this.classDeparture = classDeparture;
        this.weight = weight;
        this.importantValue = importantValue;
        this.addressTo = addressTo;
        this.addressFrom = addressFrom;
        this.status = status;
        this.createdAt = System.currentTimeMillis();
        this.transitHistory = new ArrayList<>();

        // Автоматический расчет производных полей
        this.timeDeparture = calculateTimeDeparture(classDeparture);
        this.priceDeparture = calculatePriceDeparture(classDeparture, weight);
        this.finalCost = calculateFinalCost(priceDeparture, importantValue);

        // Добавление начальной точки транзита
        addInitialTransit(sender, addressFrom);
    }

    /** Конструктор по умолчанию для десериализации */
    public PostItem() {
        this.transitHistory = new ArrayList<>();
    }

    /**
     * Рассчитать срок доставки на основе класса отправления
     * @param classDeparture Класс отправления (1, 2, 3)
     * @return Срок доставки в днях
     */
    private int calculateTimeDeparture(byte classDeparture) {
        switch (classDeparture) {
            case 1: return 5;   // 1 класс - 5 дней
            case 2: return 10;  // 2 класс - 10 дней
            case 3: return 15;  // 3 класс - 15 дней
            default: return 15; // По умолчанию 3 класс
        }
    }

    /**
     * Рассчитать стоимость доставки на основе класса и веса
     * @param classDeparture Класс отправления (1, 2, 3)
     * @param weight Вес отправления в килограммах
     * @return Стоимость доставки в WEST
     */
    private double calculatePriceDeparture(byte classDeparture, int weight) {
        double pricePerKg = 0.1;
        switch (classDeparture) {
            case 1: pricePerKg = 0.5; break; // 1 класс - 0.5 WEST за кг
            case 2: pricePerKg = 0.3; break; // 2 класс - 0.3 WEST за кг
            case 3: pricePerKg = 0.1; break; // 3 класс - 0.1 WEST за кг
        }
        return pricePerKg * weight;
    }

    /**
     * Рассчитать итоговую стоимость отправления
     * @param priceDeparture Стоимость доставки
     * @param importantValue Объявленная ценность
     * @return Итоговая стоимость в WEST
     */
    private double calculateFinalCost(double priceDeparture, int importantValue) {
        return priceDeparture + (importantValue * 0.1);
    }

    /**
     * Добавить начальную точку транзита при создании отправления
     * @param employeeAddress Адрес сотрудника, принявшего отправление
     * @param postOfficeId Идентификатор почтового отделения отправления
     */
    private void addInitialTransit(String employeeAddress, String postOfficeId) {
        TransitPoint initialTransit = new TransitPoint(postOfficeId, employeeAddress, System.currentTimeMillis(), weight);
        transitHistory.add(initialTransit);
    }

    /**
     * Добавить точку транзита в историю движения отправления
     * @param postOfficeId Идентификатор почтового отделения
     * @param employeeAddress Адрес сотрудника, обработавшего отправление
     * @param weight Вес отправления в данной точке транзита
     */
    public void addTransitPoint(String postOfficeId, String employeeAddress, int weight) {
        TransitPoint transit = new TransitPoint(postOfficeId, employeeAddress, System.currentTimeMillis(), weight);
        transitHistory.add(transit);
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(String trackNumber) {
        this.trackNumber = trackNumber;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte getClassDeparture() {
        return classDeparture;
    }

    public void setClassDeparture(byte classDeparture) {
        this.classDeparture = classDeparture;
    }

    public int getTimeDeparture() {
        return timeDeparture;
    }

    public void setTimeDeparture(int timeDeparture) {
        this.timeDeparture = timeDeparture;
    }

    public double getPriceDeparture() {
        return priceDeparture;
    }

    public void setPriceDeparture(double priceDeparture) {
        this.priceDeparture = priceDeparture;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getImportantValue() {
        return importantValue;
    }

    public void setImportantValue(int importantValue) {
        this.importantValue = importantValue;
    }

    public double getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(double finalCost) {
        this.finalCost = finalCost;
    }

    public String getAddressTo() {
        return addressTo;
    }

    public void setAddressTo(String addressTo) {
        this.addressTo = addressTo;
    }

    public String getAddressFrom() {
        return addressFrom;
    }

    public void setAddressFrom(String addressFrom) {
        this.addressFrom = addressFrom;
    }
}
