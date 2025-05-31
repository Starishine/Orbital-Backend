package com.nus.oribital.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nus.oribital.modal.Budget;
import com.nus.oribital.modal.ServiceResponse;
import com.nus.oribital.repository.BudgetRepository;


@CrossOrigin(origins = "http://localhost:8081") // Allow cross-origin requests from the specified origin
@RestController
@RequestMapping("/budget")
public class BudgetController {
    @Autowired
    private BudgetRepository budgetRepository;

    @PostMapping("/create")
    public ServiceResponse createBudget(@RequestBody Budget budget) {
        ServiceResponse response = null;
        
        if (budget.getUsername() == null || budget.getAmount() <= 0) {
            response = new ServiceResponse(400, "ERROR", null, "Username and amount must be provided");
            return response;
        }

        boolean exists = budgetRepository.findByUsernameAndCategory(budget.getUsername(), budget.getCategory()).isPresent();
        if (exists) {
            response = new ServiceResponse(400, "ERROR", null, "Budget for this category already exists for the user");
            return response;
        }

        budgetRepository.save(budget); // Save the budget to MongoDB
        response = new ServiceResponse(200, "SUCCESS", budget, "Budget created successfully");
        return response;

    }

    @DeleteMapping("/delete/{id}")
    public ServiceResponse deleteBudget(@PathVariable String id) {
        ServiceResponse response = null;
        if (!budgetRepository.existsById(id)) {
            response = new ServiceResponse(404, "ERROR", null, "Budget not found");
            return response;
        }
        budgetRepository.deleteById(id); // Delete the budget by ID from MongoDB
        response = new ServiceResponse(200, "SUCCESS", null, "Budget deleted successfully");
        return response;
    }

    @GetMapping("/get")
    public List<Budget> getBudgetsByUsername(@RequestParam String username) {
        return budgetRepository.findByUsername(username); // Retrieve all budgets from MongoDB by username
    }

    @PutMapping("/update")
    public ServiceResponse updateBudget(@RequestBody Budget updatedBudget) {
    Budget existing = budgetRepository.findById(updatedBudget.getId()).orElse(null);
    if (existing == null) {
        return new ServiceResponse(404, "ERROR", null, "Budget not found");
    }
    existing.setAmount(updatedBudget.getAmount());
    existing.setCurrency(updatedBudget.getCurrency());
    budgetRepository.save(existing);
    return new ServiceResponse(200, "SUCCESS", existing, "Budget updated successfully");
}



    // Add methods to handle budget-related requests here, e.g., create, update, delete budgets
    // Example: @PostMapping("/create") to create a new budget
    // Example: @GetMapping("/get") to retrieve all budgets
    // Example: @PutMapping("/update/{id}") to update a specific budget by ID
    // Example: @DeleteMapping("/delete/{id}") to delete a specific budget by ID

}