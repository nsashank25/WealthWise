package com.pes.financemanager.pesfinancemanager.controller;

import com.pes.financemanager.pesfinancemanager.model.User;
import com.pes.financemanager.pesfinancemanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
@RestController
@RequestMapping("/api/users")
//@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Map<String, String> registerUser(@RequestBody User user) {
        System.out.println("Received user data: " + user.getUsername() + ", " + user.getEmail() + ", Income: " + user.getIncome());
        User registeredUser = userService.registerUser(user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> loginUser(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        User user = userService.findByUsername(username); // Fetch user from DB

        Map<String, Object> response = new HashMap<>();
        if (user != null && userService.authenticate(username, password)) {
            response.put("message", "Login successful");
            response.put("userId", user.getId()); // Include userId in response
        } else {
            response.put("message", "Invalid username or password");
        }
        return response;
    }

}
