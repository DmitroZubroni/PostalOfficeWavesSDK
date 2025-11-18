package com.wavesenterprise.app.domain;
import com.wavesenterprise.app.api.IContract.PostalItemType.*;

public class PostItem {

    public String trackNumber; //Трек-номер
    public String sender; // отправитель
    public String recipient; // получатель
    public String type; //  письмо, бандероль, посылка.
    public byte classDeparture; // класс (1 2 3)
    public int timeDeparture; // срок доставки в зависимости от класса
    public double priceDeparture; // стоимость доставки (зависит от класса отправления и веса)
    public int weight; // вес (не больше 10 кг)
    public int importantValue = 0; // объявленная ценность (по умолчания 0)
    public double finalCost; // итоговая стоимость (ИСО = СО*В+ОЦ*0,1)
    public String addressTo; // адрес назначения
    public String addressFrom; // адрес отправления
    public String status; // активно принято отменено

    public PostItem ( String trackNumber, String sender, String recipient, String type, byte classDeparture, byte weight, String addressTo, String addressFrom, String status) {
        this.trackNumber = trackNumber;
        this.sender = sender;
         this.recipient = recipient;
         this.type = type;
         this.classDeparture = classDeparture;
         this.timeDeparture = calculateTimeDeparture(classDeparture);
         this.priceDeparture = calculatePriceDeparture(classDeparture, weight);
        this.weight = weight;
        this.finalCost = calculateFinalCost(classDeparture, weight);
        this.addressTo = addressTo;
        this.addressFrom = addressFrom;
        this.status = status;
    }

    public PostItem () {}

    // рассчитываем срок доставки
    public int calculateTimeDeparture( byte classDeparture) {
        int amount = 1;
        if (classDeparture == 1) {
            amount =  5;
        } else if (classDeparture == 2) {
            amount = 10;
        } else if (classDeparture == 3) {
            amount =  15;
        }
        return amount;
    };

    public double calculatePriceDeparture(byte classDeparture,byte weight) {
        double amount = 1;
        if (classDeparture == 1) {
            amount =  weight * 0.5;
        } else if (classDeparture == 2) {
            amount = weight * 0.3;
        } else if (classDeparture == 3) {
            amount = weight * 0.1;
        }
        return amount;
    };

    public double calculateFinalCost(byte classDeparture,byte weight) {
        double amount = calculatePriceDeparture(classDeparture, weight);
        return amount + this.importantValue * 0.1;
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
