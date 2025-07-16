package com.nus.oribital.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@CrossOrigin(origins = "http://localhost:8081") // Allow cross-origin requests from the specified origin
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
        SharedBudget budget = sharedBudgetRepository.findByUserAndBudgetName(userFromToken, expenseData.getCategory()).orElse(null);

        if (budget == null) {
            response = new ServiceResponse(404, "ERROR", null, "No budget found for this category");
            return response;
        }

        // Update the budget amount by deducting the expense amount
        budget.setAmount(budget.getAmount() - expenseData.getAmount());
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
