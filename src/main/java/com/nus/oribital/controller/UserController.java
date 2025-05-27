package com.nus.oribital.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nus.oribital.modal.ServiceResponse;
import com.nus.oribital.modal.User;
import com.nus.oribital.repository.UserRepository;

@CrossOrigin(origins = "http://localhost:8081") // Allow cross-origin requests from the specified origin
@RestController
@RequestMapping("/signup")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    
    @PostMapping("/register")
    public ServiceResponse registerUser(@RequestBody User user) {
        ServiceResponse response = null;
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            response = new ServiceResponse(400, "ERROR", null, "Username, password, and email are required");
            return response;
        }
        if (user.getUsername().length() < 3 || user.getPassword().length() < 6) {
            response = new ServiceResponse(400, "ERROR", null, "Username must be at least 3 characters and password at least 6 characters");
            return response;
        }
        if (userRepository.findByUsername(user.getUsername()) != null) {
            response = new ServiceResponse(400, "ERROR", null, "Username already exists");
            return response;
        }
        userRepository.save(user); // Save user to MongoDB
        response = new ServiceResponse(200, "SUCCESS", user, "User registered successfully");
        return response;
    }

    @GetMapping("/get")
    public List<User> getAllUsers() {
        return userRepository.findAll(); // Retrieve all users from MongoDB
    }

    @GetMapping("/getPassword")
    public String getPassword(@RequestBody String username) {
        List<User> users = userRepository.findAll();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                return users.get(i).getPassword();
            }
        }
        return "No user found with this username";
    }

    
}