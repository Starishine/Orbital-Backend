package com.nus.oribital.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "budgets")
public class Budget {
    @Id
    private String id;
    private String username; 
    private String category;
    private String currency; 
    private double amount;
    

    public Budget() {}

    public Budget(String category, String username,  String currency, double amount) {
        this.category = category;
        this.username = username;
        this.currency = currency;
        this.amount = amount;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}



