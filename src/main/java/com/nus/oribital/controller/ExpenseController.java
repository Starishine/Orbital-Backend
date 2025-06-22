package com.nus.oribital.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            response = new ServiceResponse(400, "ERROR", null, "No budget found for this category");
            return response;
        }

        //Check if the expense exceeds the budget
        if (expense.getAmount() > budget.getAmount()) {
            response = new ServiceResponse(400, "ERROR", null, "Expense exceeds the budget for this category");
            return response;
        }

        //Deduct the expense amount from the budget
        budget.setAmount(budget.getAmount() - expense.getAmount());
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
