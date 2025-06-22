package com.nus.oribital.modal;

public class Expense {

    private String id;
    private String username;
    private String category;
    private String currency;
    private double amount;
    private String date;
    private String name;

    public Expense(double amount, String category, String currency, String name) {
        this.amount = amount;
        this.category = category;
        this.currency = currency;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNotes() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
