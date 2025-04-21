package com.pes.financemanager.pesfinancemanager.service;

import com.pes.financemanager.pesfinancemanager.model.FinancialProfile;
import com.pes.financemanager.pesfinancemanager.model.User;
import com.pes.financemanager.pesfinancemanager.repository.FinancialProfileRepository;
import com.pes.financemanager.pesfinancemanager.repository.UserRepository;
import com.pes.financemanager.pesfinancemanager.repository.ExpenseRepository;
import com.pes.financemanager.pesfinancemanager.strategy.InvestmentStrategy;
import com.pes.financemanager.pesfinancemanager.strategy.InvestmentStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvestmentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinancialProfileRepository financialProfileRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private InvestmentStrategyFactory strategyFactory;

    public FinancialProfile getOrCreateFinancialProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FinancialProfile profile = financialProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    // Create a new profile with default values
                    FinancialProfile newProfile = new FinancialProfile();
                    newProfile.setUser(user);
                    newProfile.setRiskTolerance("MEDIUM");
                    newProfile.setInvestmentTimeframeMonths(12);
                    return newProfile;
                });

        // Always update the financial metrics whether it's a new or existing profile
        Double totalExpenses = expenseRepository.getTotalExpensesByUserId(userId);
        Double annualIncome = user.getIncome() * 12;
        Double savings = annualIncome - totalExpenses;

        profile.setMonthlyIncome(annualIncome);
        profile.setMonthlyExpenses(totalExpenses);
        profile.setSavingsAmount(savings);

        // Save the updated profile
        return financialProfileRepository.save(profile);
    }

    public FinancialProfile updateFinancialProfile(Long userId, FinancialProfile profileData) {
        FinancialProfile profile = getOrCreateFinancialProfile(userId);

        profile.setMonthlyIncome(profileData.getMonthlyIncome());
        profile.setMonthlyExpenses(profileData.getMonthlyExpenses());
        profile.setSavingsAmount(profileData.getSavingsAmount());
        profile.setRiskTolerance(profileData.getRiskTolerance());
        profile.setInvestmentTimeframeMonths(profileData.getInvestmentTimeframeMonths());

        return financialProfileRepository.save(profile);
    }

    public String generateInvestmentAdvice(Long userId) {
        FinancialProfile profile = getOrCreateFinancialProfile(userId);
        User user = profile.getUser();

        // Calculate ratios based on monthly income (not annual)
        double monthlyIncome = user.getIncome();
        double savingsRatio = profile.getSavingsAmount() / monthlyIncome;
        double expenseRatio = profile.getMonthlyExpenses() / monthlyIncome;

        StringBuilder advice = new StringBuilder();

        // Basic financial health assessment
        advice.append("Financial Health Assessment:\n");

        if (expenseRatio > 0.7) {
            advice.append("Your expenses are relatively high compared to your income. Consider reducing non-essential expenses before investing heavily.\n\n");
        } else if (expenseRatio < 0.5) {
            advice.append("You have good expense control, which gives you more flexibility for investments.\n\n");
        }

        if (savingsRatio < 3) { // Less than 3 months of income saved
            advice.append("Consider building an emergency fund of 3-6 months' expenses before making significant investments.\n\n");
        }

        // Get the appropriate strategy based on user's risk tolerance
        InvestmentStrategy strategy = strategyFactory.getStrategy(profile.getRiskTolerance());

        // Get strategy-specific advice
        advice.append(strategy.generateAdvice(profile));

        return advice.toString();
    }

    // Generate advice for a specific strategy
    public String generateInvestmentAdviceWithStrategy(Long userId, String strategyType) {
        FinancialProfile profile = getOrCreateFinancialProfile(userId);
        User user = profile.getUser();

        // Calculate ratios based on monthly income (not annual)
        double monthlyIncome = user.getIncome();
        double savingsRatio = profile.getSavingsAmount() / monthlyIncome;
        double expenseRatio = profile.getMonthlyExpenses() / monthlyIncome;

        // Get the requested strategy
        InvestmentStrategy strategy = strategyFactory.getStrategy(strategyType);

        // Basic financial health assessment
        StringBuilder advice = new StringBuilder();

        advice.append("Financial Health Assessment:\n");
        if (expenseRatio > 0.7) {
            advice.append("Your expenses are relatively high compared to your income. Consider reducing non-essential expenses before investing heavily.\n\n");
        } else if (expenseRatio < 0.5) {
            advice.append("You have good expense control, which gives you more flexibility for investments.\n\n");
        }

        if (savingsRatio < 3) {
            advice.append("Consider building an emergency fund of 3-6 months' expenses before making significant investments.\n\n");
        }

        // Get strategy-specific advice
        advice.append(strategy.generateAdvice(profile));

        return advice.toString();
    }
}