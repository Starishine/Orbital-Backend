package com.nus.oribital.modal;

public class SharedExpense extends Expense {

    private String budgetId; // ID of the budget this expense belongs tp

    public SharedExpense(String name, String category, String currency, double amount, String date) {
        super(amount, category, currency, name);
    }

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

}
