package com.pes.financemanager.pesfinancemanager.controller;

import com.pes.financemanager.pesfinancemanager.model.User;
import com.pes.financemanager.pesfinancemanager.repository.UserRepository;
import com.pes.financemanager.pesfinancemanager.service.TaxEstimationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/tax")
public class TaxEstimationController {

    @Autowired
    private TaxEstimationService taxEstimationService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getTaxEstimate(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        double income = user.getIncome();
        double annualIncome = income * 12;

        Map<String, Double> userDeductions = new HashMap<>();
        userDeductions.put("80C", 100000.0);
        userDeductions.put("80D", 10000.0);

        Map<String, Object> taxEstimate = taxEstimationService.calculateTaxEstimate(annualIncome, userDeductions);
        return ResponseEntity.ok(taxEstimate);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> calculateCustomTaxEstimate(
            @PathVariable Long userId,
            @RequestBody Map<String, Double> userDeductions) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        double income = user.getIncome();
        double annualIncome = income * 12;

        Map<String, Object> taxEstimate = taxEstimationService.calculateTaxEstimate(annualIncome, userDeductions);
        return ResponseEntity.ok(taxEstimate);
    }
}
