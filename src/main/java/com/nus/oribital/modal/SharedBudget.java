package com.nus.oribital.modal;

import java.util.List;

public class SharedBudget {

    private String id;
    private String budgetName;
    private double amountAllocated; // amount allocated to the shared budget
    private double amount; // remaining amount in the shared budget
    private String currency;
    private String inviter;
    private String inviterEmail;
    private List<Invitee> invites;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmountAllocated() {
        return amountAllocated;
    }

    public void setAmountAllocated(double amountAllocated) {
        this.amountAllocated = amountAllocated;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<Invitee> getInvites() {
        return invites;
    }

    public void setInvites(List<Invitee> invites) {
        this.invites = invites;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    public String getInviterEmail() {
        return inviterEmail;
    }

    public void setInviterEmail(String inviterEmail) {
        this.inviterEmail = inviterEmail;
    }
}
