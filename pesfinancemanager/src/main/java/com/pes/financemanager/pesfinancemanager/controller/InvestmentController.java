package com.pes.financemanager.pesfinancemanager.controller;

import com.pes.financemanager.pesfinancemanager.model.FinancialProfile;
import com.pes.financemanager.pesfinancemanager.model.InvestmentSuggestion;
import com.pes.financemanager.pesfinancemanager.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/investments")
public class InvestmentController {

    @Autowired
    private InvestmentService investmentService;

    @GetMapping("/profile/{userId}")
    public ResponseEntity<FinancialProfile> getFinancialProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(investmentService.getOrCreateFinancialProfile(userId));
    }

    @PostMapping("/profile/{userId}")
    public ResponseEntity<FinancialProfile> updateFinancialProfile(
            @PathVariable Long userId,
            @RequestBody FinancialProfile profileData) {
        return ResponseEntity.ok(investmentService.updateFinancialProfile(userId, profileData));
    }

    @GetMapping("/suggestions/{userId}")
    public ResponseEntity<List<InvestmentSuggestion>> getInvestmentSuggestions(@PathVariable Long userId) {
        return ResponseEntity.ok(investmentService.getSuggestedInvestments(userId));
    }

    @GetMapping("/advice/{userId}")
    public ResponseEntity<Map<String, String>> getInvestmentAdvice(@PathVariable Long userId) {
        Map<String, String> response = new HashMap<>();
        response.put("advice", investmentService.generateInvestmentAdvice(userId));
        return ResponseEntity.ok(response);
    }
}

