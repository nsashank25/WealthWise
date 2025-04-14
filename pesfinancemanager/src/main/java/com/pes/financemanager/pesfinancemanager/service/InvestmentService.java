package com.pes.financemanager.pesfinancemanager.service;

import com.pes.financemanager.pesfinancemanager.model.FinancialProfile;
import com.pes.financemanager.pesfinancemanager.model.User;
import com.pes.financemanager.pesfinancemanager.repository.FinancialProfileRepository;
import com.pes.financemanager.pesfinancemanager.repository.UserRepository;
import com.pes.financemanager.pesfinancemanager.strategy.InvestmentStrategy;
import com.pes.financemanager.pesfinancemanager.strategy.InvestmentStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class InvestmentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinancialProfileRepository financialProfileRepository;

    @Autowired
    private InvestmentStrategyFactory strategyFactory;

    public FinancialProfile getOrCreateFinancialProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return financialProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    // Create a basic profile if none exists
                    FinancialProfile newProfile = new FinancialProfile();
                    newProfile.setUser(user);
                    newProfile.setMonthlyIncome(user.getIncome());
                    newProfile.setMonthlyExpenses(0.0); // Default value, to be updated
                    newProfile.setSavingsAmount(0.0); // Default value, to be updated
                    newProfile.setRiskTolerance("MEDIUM"); // Default value, to be updated
                    newProfile.setInvestmentTimeframeMonths(12); // Default value, to be updated
                    return financialProfileRepository.save(newProfile);
                });
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

        double savingsRatio = profile.getSavingsAmount() / profile.getMonthlyIncome();
        double expenseRatio = profile.getMonthlyExpenses() / profile.getMonthlyIncome();

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

        // Get the requested strategy
        InvestmentStrategy strategy = strategyFactory.getStrategy(strategyType);

        // Basic financial health assessment
        StringBuilder advice = new StringBuilder();
        double savingsRatio = profile.getSavingsAmount() / profile.getMonthlyIncome();
        double expenseRatio = profile.getMonthlyExpenses() / profile.getMonthlyIncome();

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