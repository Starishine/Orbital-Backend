package com.nus.oribital.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nus.oribital.modal.Invitee;
import com.nus.oribital.modal.ServiceResponse;
import com.nus.oribital.modal.SharedBudget;
import com.nus.oribital.repository.SharedBudgetRepository;
import com.nus.oribital.util.EmailUtil;
import com.nus.oribital.util.JWTTokenUtil;

@RestController
@RequestMapping("/shared-budget")
@CrossOrigin(origins = "http://localhost:8081") // Allow cross-origin requests from the specified origin

public class SharedBudgetController {

    @Autowired
    private SharedBudgetRepository sharedBudgetRepository;

    @PostMapping("/create")
    public ServiceResponse createSharedBudget(@RequestBody SharedBudget sharedBudget, @RequestHeader("Authorization") String authHeader) {
        ServiceResponse response = null;

        if (sharedBudget.getInvites() != null) {
            sharedBudget.getInvites().forEach(inviteeObj -> {
                Invitee invitee = (Invitee) inviteeObj;
                invitee.setToken(UUID.randomUUID().toString()); // Generate a unique token for each invitee
                invitee.setStatus("invited"); // Set initial status to invited

                EmailUtil.sendMail(sharedBudget.getInviterEmail(), invitee.getEmail(),
                        "You're Invited to a Shared Budget!",
                        EmailUtil.renderInviteEmail(sharedBudget.getInviter(),
                                sharedBudget.getInviterEmail(),
                                invitee.getUsername(),
                                sharedBudget.getBudgetName(),
                                "http://localhost:8081/welcomeInvite?token=" + invitee.getToken()));
            });
        }

        sharedBudgetRepository.save(sharedBudget); // Save the shared budget to MongoDB
        return new ServiceResponse(200, "SUCCESS", sharedBudget, "Shared budget created successfully");

    }

    @PutMapping("/update")
    public ServiceResponse updateSharedBudget(@RequestBody SharedBudget sharedBudget, @RequestHeader("Authorization") String authHeader) {
        ServiceResponse response = null;

        if (sharedBudget.getId() == null || sharedBudget.getId().isEmpty()) {
            return new ServiceResponse(400, "ERROR", null, "Shared budget ID is required for update");
        }

        SharedBudget existingBudget = sharedBudgetRepository.findById(sharedBudget.getId()).orElse(null);
        if (existingBudget == null) {
            return new ServiceResponse(404, "ERROR", null, "Shared budget not found");
        }

        existingBudget.setBudgetName(sharedBudget.getBudgetName());
        existingBudget.setAmount(sharedBudget.getAmount());
        existingBudget.setAmountAllocated(sharedBudget.getAmountAllocated());
        existingBudget.setCurrency(sharedBudget.getCurrency());
        // existingBudget.setInvites(sharedBudget.getInvites());

        sharedBudgetRepository.save(existingBudget); // Save the updated shared budget to MongoDB
        return new ServiceResponse(200, "SUCCESS", existingBudget, "Shared budget updated successfully");
    }

    @GetMapping("/accept")
    public ServiceResponse acceptSharedBudget(@RequestParam("token") String token) {
        ServiceResponse response = null;

        SharedBudget budget = sharedBudgetRepository.findByInviteeToken(token);
        System.out.println("Budget found: " + budget);
        if (budget == null) {
            return new ServiceResponse(404, "ERROR", null, "Invalid or expired token");
        }
        Invitee invitee = budget.getInvites().stream()
                .filter(i -> token.equals(i.getToken()))
                .findFirst()
                .orElse(null);
        if (invitee == null) {
            return new ServiceResponse(404, "ERROR", null, "Invitee not found");
        }
        // Update status
        invitee.setStatus("accepted");
        System.out.println("Invitee status updated: " + invitee.getStatus());
        sharedBudgetRepository.save(budget);
        return new ServiceResponse(200, "SUCCESS", budget, "Invitation accepted");
    }

    @GetMapping("/get")
    public List<SharedBudget> getSharedBudgets(@RequestHeader("Authorization") String authHeader) {
        return sharedBudgetRepository.findAll();
    }

    @GetMapping("/get-by-user")
    public List<SharedBudget> getSharedBudgetsByUser(@RequestHeader("Authorization") String authHeader) {
        // Extract token from header
        String token = authHeader.replace("Bearer ", "");
        String userFromToken = new JWTTokenUtil().getUser(token);

        if (userFromToken == null) {
            throw new RuntimeException("Invalid or expired token");
        }

        // Retrieve all shared budgets for the user
        return sharedBudgetRepository.findAllByUser(userFromToken);
    }

}
