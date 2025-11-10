package com.wavesenterprise.app.domain;

import static com.wavesenterprise.app.api.IContract.Role.USER;

public class User {
    private String name; // имя
    private String homeAddress; // домашний адрес
    private String blockchainAddress; //блокчейн адрес
    private double balance; // баланс
    private String role; // роль
    private String postId; // айди почтового отделения (в начале RR)

    public User(String name, String homeAddress, String blockchainAddress, double balance) {
        this.name = name;
        this.homeAddress = homeAddress;
        this.blockchainAddress = blockchainAddress;
        this.balance = balance;
        this.role = USER;
    }

    public User() {}

    public String getName() { return name; }
    public String getHomeAddress() { return homeAddress; }
    public String getBlockchainAddress() { return blockchainAddress; }
    public double getBalance() { return balance; }
    public String getRole() { return role; }
    public String getPostId() { return postId; }

    public void setName(String name) { this.name = name; }
    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }
    public void setBlockchainAddress(String blockchainAddress) { this.blockchainAddress = blockchainAddress; }
    public void setBalance(double balance) { this.balance = balance; }
    public void setRole(String role) { this.role = role; }
    public void setPostId(String postId) { this.postId = "RR" + postId; }

}
