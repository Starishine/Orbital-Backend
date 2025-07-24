package com.nus.oribital.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nus.oribital.modal.ServiceResponse;
import com.nus.oribital.modal.SharedBudget;
import com.nus.oribital.modal.SharedExpense;
import com.nus.oribital.repository.SharedBudgetRepository;
import com.nus.oribital.repository.SharedExpenseRepository;
import com.nus.oribital.util.JWTTokenUtil;

@RestController
@RequestMapping("/shared-expense")
public class SharedExpenseController {

    @Autowired
    private SharedExpenseRepository sharedExpenseRepository;

    @Autowired
    private SharedBudgetRepository sharedBudgetRepository;

    @PostMapping("/create")
    public ServiceResponse createSharedExpense(@RequestBody SharedExpense expenseData, @RequestHeader("Authorization") String authHeader) {
        ServiceResponse response = null;
        // Extract token from header
        String token = authHeader.replace("Bearer ", "");
        String userFromToken = new JWTTokenUtil().getUser(token);

        if (expenseData.getAmount() <= 0 || expenseData.getCategory() == null || userFromToken == null) {
            response = new ServiceResponse(400, "ERROR", null, "Invalid expense data");
            return response;
        }
        // Check if the user has a budget for the specified category
        SharedBudget budget = sharedBudgetRepository.findByUserAndCategory(userFromToken, expenseData.getCategory()).orElse(null);

        if (budget == null) {
            response = new ServiceResponse(404, "ERROR", null, "No budget found for this category");
            return response;
        }

        double expenseAmount = expenseData.getAmount();
        // Convert the expense amount to the budget's currency if necessary
        if (!expenseData.getCurrency().equalsIgnoreCase(budget.getCurrency())) {
            String apiURL = "https://api.exchangerate-api.com/v4/latest/" + expenseData.getCurrency();
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            try {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> responseMap = restTemplate.getForObject(apiURL, java.util.Map.class);
                java.util.Map<String, Double> rates = (java.util.Map<String, Double>) responseMap.get("rates");
                Double exchangeRate = rates.get(budget.getCurrency());
                if (exchangeRate != null) {
                    expenseAmount = expenseData.getAmount() * exchangeRate; // Convert to budget's currency
                } else {
                    response = new ServiceResponse(400, "ERROR", null, "Currency conversion rate not found");
                    return response;
                }
            } catch (Exception e) {
                response = new ServiceResponse(500, "ERROR", null, "Error fetching exchange rate: " + e.getMessage());
                return response;
            }
        }
        // Update the budget amount by deducting the expense amount
        budget.setAmount(budget.getAmount() - expenseAmount);
        // Ensure that budget id = expense id for retrieval purposes
        expenseData.setBudgetId(budget.getId());
        sharedBudgetRepository.save(budget); // Save the updated budget to MongoDB

        // Save the shared expense
        expenseData.setUsername(userFromToken); // Set the username from the token
        sharedExpenseRepository.save(expenseData); // Save the expense to MongoDB
        response = new ServiceResponse(200, "SUCCESS", expenseData, "Expense created successfully");
        return response;
    }

    @GetMapping("/get")
    public List<SharedExpense> getAllSharedExpenses() {
        return sharedExpenseRepository.findAll(); // Retrieve all shared expenses from MongoDB
    }

    @GetMapping("/get-by-budget")
    public List<SharedExpense> getSharedExpensesByBudget(@RequestParam("budgetId") String budgetId) {
        return sharedExpenseRepository.findAllByBudgetId(budgetId);
    }

}
