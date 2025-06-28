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

import com.nus.oribital.modal.ServiceResponse;
import com.nus.oribital.modal.SharedBudget;
import com.nus.oribital.repository.SharedBudgetRepository;

@RestController
@RequestMapping("/shared-budget")
@CrossOrigin(origins = "http://localhost:8081") // Allow cross-origin requests from the specified origin

public class SharedBudgetController {

    @Autowired
    private SharedBudgetRepository sharedBudgetRepository;
    // private EmailService emailService;

    @PostMapping("/create")
    public ServiceResponse createSharedBudget(@RequestBody SharedBudget sharedBudget, @RequestHeader("Authorization") String authHeader) {
        ServiceResponse response = null;

        if (sharedBudget.getAmount() <= 0 || sharedBudget.getBudgetName() == null || sharedBudget.getCurrency() == null || sharedBudget.getInvites() == null) {
            response = new ServiceResponse(400, "ERROR", null, "Invalid shared budget data");
            return response;
        }

        // Save the shared budget to MongoDB
        sharedBudgetRepository.save(sharedBudget);

        // // Send email notification to users
        // for (String user : sharedBudget.getUsers()) {
        //     emailService.sendEmail(user, "Shared Budget Created", "A new shared budget has been created: " + sharedBudget.getBudgetName());
        // }
        response = new ServiceResponse(200, "SUCCESS", sharedBudget, "Shared budget created successfully");
        return response;
    }

    @GetMapping("/get")
    public List<SharedBudget> getSharedBudgets(@RequestHeader("Authorization") String authHeader) {
        return sharedBudgetRepository.findAll();
    }

}
