package com.pes.financemanager.pesfinancemanager.service;

import com.pes.financemanager.pesfinancemanager.model.User;
import com.pes.financemanager.pesfinancemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(User user) {
        // Hash the password before saving
        System.out.println("Saving user: " + user.getUsername() + " with email: " + user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getIncome() <= 0) {
            user.setIncome(0.00);
        }

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username); // Fetch user by username
    }

    public boolean authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        return passwordEncoder.matches(password, user.getPassword());
    }
}
