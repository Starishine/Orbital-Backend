package com.nus.oribital.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
        budgetRepository.save(budget); // Save the budget to MongoDB
        response = new ServiceResponse(200, "SUCCESS", budget, "Budget created successfully");
        return response; 
    }

    @DeleteMapping("/delete/{id}")
    public void deleteBudget(@RequestBody String id) {
        budgetRepository.deleteById(id); // Delete the budget by ID from MongoDB
    }

    @GetMapping("/get")
    public List<Budget> getBudgetsByUsername(@RequestParam String username) {
        return budgetRepository.findByUsername(username); // Retrieve all budgets from MongoDB by username
    }


    // Add methods to handle budget-related requests here, e.g., create, update, delete budgets
    // Example: @PostMapping("/create") to create a new budget
    // Example: @GetMapping("/get") to retrieve all budgets
    // Example: @PutMapping("/update/{id}") to update a specific budget by ID
    // Example: @DeleteMapping("/delete/{id}") to delete a specific budget by ID

}