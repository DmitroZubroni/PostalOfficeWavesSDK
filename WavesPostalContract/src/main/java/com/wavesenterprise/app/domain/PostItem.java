package com.wavesenterprise.app.domain;
 // почтовое отправление
public class PostItem {

    private int trackNumber; // трек номер (должен формироваться автоматически в начале RR)
    private String sender; // отправитель
    private String recipient; // получатель
    private String type; //  тип (письмо, бандероль, посылка)
    private byte departureClass; // класс отправления
    private int deliveryTime; // срок доставки (определяется в зависимости от класса отправления)
    private int price; // стоимость доставки (зависит от класса и веса)
    private int weight; // вес (не больше 10кг)
    private int declaredValue; // объявленная ценность по дефолту 0
    private int totalCost; // итоговая стоимость
    private String destinationAddress; // адрес назначения (Индекс, город, улицу, дом.)
    private String departureAddress; //адрес отправления (Индекс, город, улицу, дом.)

     public PostItem(int trackNumber, String sender, String recipient, String type, byte departureClass, int deliveryTime, int price, int weight, int declaredValue, int totalCost, String destinationAddress, String departureAddress) {
         this.trackNumber = trackNumber;
         this.sender = sender;
         this.recipient = recipient;
         this.type = type;
         this.departureClass = departureClass;
         this.deliveryTime = deliveryTime;
         this.price = price;
         this.weight = weight;
         this.totalCost = totalCost;
         this.destinationAddress = destinationAddress;
         this.departureAddress = departureAddress;
     }

     public PostItem() {}

     public int getTrackNumber() {
         return trackNumber;
     }

     public String getSender() {
         return sender;
     }

     public String getRecipient() {
         return recipient;
     }

     public String getType() {
         return type;
     }

     public byte getDepartureClass() {
         return departureClass;
     }

     public int getDeliveryTime() {
         return deliveryTime;
     }

     public int getPrice() {
         return price;
     }

     public int getWeight() {
         return weight;
     }

     public int getDeclaredValue() {
         return declaredValue;
     }

     public int getTotalCost() {
         return totalCost;
     }

     public String getDestinationAddress() {
         return destinationAddress;
     }

     public String getDepartureAddress() {
         return departureAddress;
     }

     public void setTrackNumber(int trackNumber) {
         this.trackNumber = trackNumber;
     }

     public void setSender(String sender) {
         this.sender = sender;
     }

     public void setRecipient(String recipient) {
         this.recipient = recipient;
     }

     public void setType(String type) {
         this.type = type;
     }

     public void setDepartureClass(byte departureClass) {
         this.departureClass = departureClass;
     }

     public void setPrice(int price) {
         this.price = price;
     }

     public void setWeight(int weight) {
         this.weight = weight;
     }

     public void setDeliveryTime(int deliveryTime) {
         this.deliveryTime = deliveryTime;
     }

     public void setDeclaredValue(int declaredValue) {
         this.declaredValue = declaredValue;
     }

     public void setTotalCost(int totalCost) {
         this.totalCost = totalCost;
     }

     public void setDestinationAddress(String destinationAddress) {
         this.destinationAddress = destinationAddress;
     }

     public void setDepartureAddress(String departureAddress) {
         this.departureAddress = departureAddress;
     }
 }
