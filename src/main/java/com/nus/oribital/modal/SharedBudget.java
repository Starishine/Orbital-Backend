package com.nus.oribital.modal;

import java.util.List;

public class SharedBudget extends Budget {

    private String inviter;
    private String inviterEmail;
    private List<Invitee> invites;

    public SharedBudget(String category, double amountAllocated, double amount, String currency, List<Invitee> invites, String inviter, String inviterEmail) {
        super(category, null, currency, amount, amountAllocated, null, null, null);
        this.invites = invites;
        this.inviter = inviter;
        this.inviterEmail = inviterEmail;
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
