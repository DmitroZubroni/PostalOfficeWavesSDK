package com.wavesenterprise.app.domain;

public class TransitPoint {
    /**
     * Идентификатор почтового отделения (формат: RR + индекс)
     */
    public String postOfficeId;

    /**
     * Адрес сотрудника, обработавшего отправление в блокчейн-системе
     */
    public String employeeAddress;

    /**
     * Временная метка обработки отправления (в миллисекундах)
     */
    public long timestamp;



    /**
     * Конструктор точки транзита
     *
     * @param postOfficeId    Идентификатор почтового отделения
     * @param employeeAddress Адрес сотрудника в блокчейн-системе
     * @param timestamp       Временная метка обработки
     * @param weight          Вес отправления в килограммах
     */
    public TransitPoint(String postOfficeId, String employeeAddress, long timestamp, int weight) {
        this.postOfficeId = postOfficeId;
        this.employeeAddress = employeeAddress;
        this.timestamp = timestamp;
    }

    /**
     * Конструктор по умолчанию для десериализации
     */
    public TransitPoint() {
    }

    public String getPostOfficeId() {
        return postOfficeId;
    }

    public void setPostOfficeId(String postOfficeId) {
        this.postOfficeId = postOfficeId;
    }

    public String getEmployeeAddress() {
        return employeeAddress;
    }

    public void setEmployeeAddress(String employeeAddress) {
        this.employeeAddress = employeeAddress;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

