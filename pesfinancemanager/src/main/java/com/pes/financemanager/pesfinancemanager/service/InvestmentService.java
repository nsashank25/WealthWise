package com.pes.financemanager.pesfinancemanager.service;

import com.pes.financemanager.pesfinancemanager.model.FinancialProfile;
import com.pes.financemanager.pesfinancemanager.model.InvestmentSuggestion;
import com.pes.financemanager.pesfinancemanager.model.User;
import com.pes.financemanager.pesfinancemanager.repository.FinancialProfileRepository;
import com.pes.financemanager.pesfinancemanager.repository.InvestmentSuggestionRepository;
import com.pes.financemanager.pesfinancemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvestmentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinancialProfileRepository financialProfileRepository;

    @Autowired
    private InvestmentSuggestionRepository investmentSuggestionRepository;

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

    public List<InvestmentSuggestion> getSuggestedInvestments(Long userId) {
        FinancialProfile profile = getOrCreateFinancialProfile(userId);

        // Calculate investment capacity based on income, expenses and savings
        double monthlyDisposableIncome = profile.getMonthlyDisposableIncome();
        double totalInvestmentCapacity = profile.getSavingsAmount() + (monthlyDisposableIncome * 3); // 3 months of disposable income

        System.out.println("User ID: " + userId);
        System.out.println("Risk Tolerance: " + profile.getRiskTolerance());
        System.out.println("Timeframe (months): " + profile.getInvestmentTimeframeMonths());
        System.out.println("Total Investment Capacity: " + totalInvestmentCapacity);

        // Get suggestions based on risk tolerance, timeframe and investment capacity
        List<InvestmentSuggestion> suggestions = investmentSuggestionRepository
                .findByRiskLevelAndDurationMonthsLessThanEqual(
                        profile.getRiskTolerance(),
                        profile.getInvestmentTimeframeMonths()
                );

        System.out.println("Suggestions returned: " + suggestions.size());

        // Filter by investment amount
        return suggestions.stream()
                .filter(s -> s.getMinimumInvestmentAmount() <= totalInvestmentCapacity)
                .collect(Collectors.toList());
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

        // Risk education based on profile
        advice.append("Investment Risk Education:\n");
        switch (profile.getRiskTolerance()) {
            case "LOW":
                advice.append("Low-risk investments typically offer modest returns but higher security. " +
                        "These include government bonds, certificates of deposit, and high-quality corporate bonds. " +
                        "While these investments won't generate dramatic growth, they provide stability and income.\n\n");
                break;
            case "MEDIUM":
                advice.append("Medium-risk investments balance growth potential with reasonable security. " +
                        "These include dividend-paying stocks, balanced mutual funds, and REITs. " +
                        "They offer better growth potential than low-risk options, but with moderate volatility.\n\n");
                break;
            case "HIGH":
                advice.append("High-risk investments offer the greatest growth potential but with significant volatility. " +
                        "These include growth stocks, specialized sector funds, and certain alternative investments. " +
                        "While they can generate substantial returns, they may also experience sharp declines.\n\n");
                break;
        }

        // Timeframe guidance
        advice.append("Investment Timeframe Guidance:\n");
        if (profile.getInvestmentTimeframeMonths() < 24) {
            advice.append("For short-term goals (less than 2 years), focus on liquidity and capital preservation. " +
                    "Consider high-yield savings accounts, money market funds, or short-term bond funds.\n");
        } else if (profile.getInvestmentTimeframeMonths() < 60) {
            advice.append("For medium-term goals (2-5 years), a balanced approach is appropriate. " +
                    "Consider balanced funds, bond ladders, or conservative allocation strategies.\n");
        } else {
            advice.append("For long-term goals (5+ years), you can afford to take more risk for higher returns. " +
                    "Consider stock-heavy portfolios, index funds, or growth-oriented strategies.\n");
        }

        return advice.toString();
    }
}
