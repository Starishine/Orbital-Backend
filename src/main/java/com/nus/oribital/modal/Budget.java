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
    private double amountAllocated;
    private double amount;
    private String interval;
    private String dateCreated;
    private String expiryDate;

    public Budget() {
    }

    public Budget(String category, String username, String currency, double amount, String interval, String dateCreated, String expiryDate) {
        this.category = category;
        this.username = username;
        this.currency = currency;
        this.amount = amount;
        this.interval = interval;
        this.dateCreated = dateCreated;
        this.expiryDate = expiryDate;
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

    public double getAmountAllocated() {
        return amountAllocated;
    }

    public void setAmountAllocated(double amountAllocated) {
        this.amountAllocated = amountAllocated;
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

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
