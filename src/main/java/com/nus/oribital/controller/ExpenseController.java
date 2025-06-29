package com.nus.oribital.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.nus.oribital.modal.Budget;
import com.nus.oribital.modal.Expense;
import com.nus.oribital.modal.ServiceResponse;
import com.nus.oribital.repository.BudgetRepository;
import com.nus.oribital.repository.ExpenseRepository;
import com.nus.oribital.util.JWTTokenUtil;

@CrossOrigin(origins = "http://localhost:8081") // Allow cross-origin requests from the specified origin
@RestController
@RequestMapping("/expense")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @PostMapping("/create")
    public ServiceResponse createExpense(@RequestBody Expense expense, @RequestHeader("Authorization") String authHeader) {
        ServiceResponse response = null;
        // Extract token from header
        String token = authHeader.replace("Bearer ", "");
        String userFromToken = new JWTTokenUtil().getUser(token);

        if (expense.getAmount() <= 0 || expense.getCategory() == null || userFromToken == null) {
            response = new ServiceResponse(400, "ERROR", null, "Invalid expense data");
            return response;
        }
        expense.setUsername(userFromToken); // Set the username from the token

        // Check if the budget exists for the user and category
        Budget budget = budgetRepository.findByUsernameAndCategory(userFromToken, expense.getCategory())
                .orElse(null);

        if (budget == null) {
            Budget newBudget = new Budget();
            newBudget.setUsername(userFromToken);
            newBudget.setCategory(expense.getCategory());
            newBudget.setCurrency(expense.getCurrency());
            newBudget.setAmount(0.00 - expense.getAmount()); // Initialize budget amount to 0 if no budget exists
            budgetRepository.save(newBudget); // Save the new budget to MongoDB
            response = new ServiceResponse(200, "SUCCESS", expense, "No budget found for this category, expense recorded without budget check");
            expenseRepository.save(expense); // Save the expense to MongoDB without budget check
            return response;
        }

        // //Check if the expense exceeds the budget
        // if (expense.getAmount() > budget.getAmount()) {
        //     response = new ServiceResponse(400, "ERROR", null, "Expense exceeds the budget for this category");
        //     return response;
        // }
        double expenseAmount = expense.getAmount();
        // Convert the expense amount to the budget's currency if necessary
        if (!expense.getCurrency().equalsIgnoreCase(budget.getCurrency())) {
            String apiURL = "https://api.exchangerate-api.com/v4/latest/" + expense.getCurrency();
            RestTemplate restTemplate = new RestTemplate();
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseMap = restTemplate.getForObject(apiURL, Map.class);
                Map<String, Double> rates = (Map<String, Double>) responseMap.get("rates");
                Double exchangeRate = rates.get(budget.getCurrency());
                if (exchangeRate != null) {
                    expenseAmount = expense.getAmount() * exchangeRate; // Convert to budget's currency
                } else {
                    response = new ServiceResponse(400, "ERROR", null, "Currency conversion rate not found");
                    return response;
                }
            } catch (Exception e) {
                response = new ServiceResponse(500, "ERROR", null, "Error fetching exchange rate: " + e.getMessage());
                return response;
            }
        }

        // Deduct the expense amount from the budget
        budget.setAmount(budget.getAmount() - expenseAmount);
        budgetRepository.save(budget); // Save the updated budget to MongoDB

        // Save the expense
        expenseRepository.save(expense); // Save the expense to MongoDB
        response = new ServiceResponse(200, "SUCCESS", expense, "Expense created successfully");
        return response;
    }

    @GetMapping("/get")
    public List<Expense> getExpenses(@RequestHeader("Authorization") String authHeader) {
        // Extract token from header (format: "Bearer <token>")
        String token = authHeader.replace("Bearer ", "");
        String user = new JWTTokenUtil().getUser(token);

        // check if userFromToken matches the username param
        if (user == null) {
            throw new RuntimeException("Invalid or expired token");
        }
        return expenseRepository.findByUsername(user); // Retrieve all budgets from MongoDB by username
    }
}
