package com.wavesenterprise.app.domain;

import static com.wavesenterprise.app.api.IContract.Role.USER;

public class User {
    /** Полное имя пользователя */
    public String name;

    /** Домашний адрес пользователя */
    public String homeAddress;

    /** Адрес пользователя в блокчейн-системе */
    public String blockchainAddress;

    /** Баланс пользователя в токенах WEST */
    public int balance;

    /** Роль пользователя в системе (ADMIN, EMPLOYEE, USER) */
    public String role;

    /** Идентификатор почтового отделения для сотрудников (формат: RR + индекс) */
    public String postId;

    /**
     * Конструктор для создания нового пользователя
     * @param name Полное имя пользователя
     * @param homeAddress Домашний адрес пользователя
     * @param blockchainAddress Адрес в блокчейн-системе
     * @param balance Начальный баланс в токенах WEST
     * @param role Роль пользователя
     * @param postId Идентификатор почтового отделения (только для сотрудников)
     */
    public User(String name, String homeAddress, String blockchainAddress, int balance, String role, String postId) {
        this.name = name;
        this.homeAddress = homeAddress;
        this.blockchainAddress = blockchainAddress;
        this.balance = balance;
        this.role = role;
        this.postId = postId;
    }

    /** Конструктор по умолчанию для десериализации */
    public User() {}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getBlockchainAddress() {
        return blockchainAddress;
    }

    public void setBlockchainAddress(String blockchainAddress) {
        this.blockchainAddress = blockchainAddress;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
