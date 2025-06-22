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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nus.oribital.modal.Budget;
import com.nus.oribital.modal.ServiceResponse;
import com.nus.oribital.repository.BudgetRepository;
import com.nus.oribital.util.JWTTokenUtil;

@CrossOrigin(origins = "http://localhost:8081") // Allow cross-origin requests from the specified origin
@RestController
@RequestMapping("/budget")
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    @PostMapping("/create")
    public ServiceResponse createBudget(@RequestBody Budget budget, @RequestHeader("Authorization") String authHeader) {
        ServiceResponse response = null;
        // Extract token from header
        String token = authHeader.replace("Bearer ", "");
        String userFromToken = new JWTTokenUtil().getUser(token);

        if (userFromToken == null || budget.getAmount() <= 0) {
            response = new ServiceResponse(400, "ERROR", null, "Username and amount must be provided");
            return response;
        }
        budget.setUsername(userFromToken); // Set the username from the token
        boolean exists = budgetRepository.findByUsernameAndCategory(userFromToken, budget.getCategory()).isPresent();
        if (exists) {
            response = new ServiceResponse(400, "ERROR", null, "Budget for this category already exists for the user");
            return response;
        }

        budgetRepository.save(budget); // Save the budget to MongoDB
        response = new ServiceResponse(200, "SUCCESS", budget, "Budget created successfully");
        return response;

    }

    @DeleteMapping("/delete/{id}")
    public ServiceResponse deleteBudget(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
        ServiceResponse response = null;
        // Extract token from header
        String token = authHeader.replace("Bearer ", "");
        String userFromToken = new JWTTokenUtil().getUser(token);
        Budget existing = budgetRepository.findById(id).orElse(null);
        if (existing == null) {
            response = new ServiceResponse(404, "ERROR", null, "Budget not found");
            return response;
        }
        if (!userFromToken.equals(existing.getUsername())) {
            response = new ServiceResponse(403, "ERROR", null, "Unauthorized Access: User does not match token");
            return response;
        }
        budgetRepository.deleteById(id); // Delete the budget by ID from MongoDB
        response = new ServiceResponse(200, "SUCCESS", null, "Budget deleted successfully");
        return response;
    }

    @GetMapping("/get")
    public List<Budget> getBudget(@RequestHeader("Authorization") String authHeader) {
        // Extract token from header (format: "Bearer <token>")
        String token = authHeader.replace("Bearer ", "");
        String user = new JWTTokenUtil().getUser(token);

        // check if userFromToken matches the username param
        if (user == null) {
            throw new RuntimeException("Invalid or expired token");
        }
        return budgetRepository.findByUsername(user); // Retrieve all budgets from MongoDB by username
    }

    @PutMapping("/update")
    public ServiceResponse updateBudget(@RequestBody Budget updatedBudget, @RequestHeader("Authorization") String authHeader) {
        // Extract token from header
        String token = authHeader.replace("Bearer ", "");
        String userFromToken = new JWTTokenUtil().getUser(token);

        Budget existing = budgetRepository.findById(updatedBudget.getId()).orElse(null);
        if (existing == null) {
            return new ServiceResponse(404, "ERROR", null, "Budget not found");
        }
        if (!userFromToken.equals(existing.getUsername())) {
            return new ServiceResponse(403, "ERROR", null, "Unauthorized Access: User does not match token");
        }
        existing.setAmount(updatedBudget.getAmount());
        existing.setCurrency(updatedBudget.getCurrency());
        budgetRepository.save(existing);
        return new ServiceResponse(200, "SUCCESS", existing, "Budget updated successfully");
    }

}
