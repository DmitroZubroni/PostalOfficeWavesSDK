package com.wavesenterprise.app.domain;

import static com.wavesenterprise.app.api.IContract.Role.USER;

public class User {

    public String name; //имя пользователя
    public String homeAddress; //  домашний адрес
    public String blockchainAddress; // блокчейн адрес
    public int balance; // баланс
    public String role; // роль пользователя
    public String postId; // айди почтового отделения

    public User (String name, String homeAddress, String blockchainAddress, int balance) {
        this.name = name;
        this.homeAddress = homeAddress;
        this.blockchainAddress = blockchainAddress;
        this.balance = balance;
        this.role = USER;
        this.postId = null;
    }

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
