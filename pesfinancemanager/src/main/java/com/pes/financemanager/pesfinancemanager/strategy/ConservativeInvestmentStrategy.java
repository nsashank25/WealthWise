package com.pes.financemanager.pesfinancemanager.strategy;

import com.pes.financemanager.pesfinancemanager.model.FinancialProfile;
import com.pes.financemanager.pesfinancemanager.model.InvestmentSuggestion;
import com.pes.financemanager.pesfinancemanager.repository.InvestmentSuggestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConservativeInvestmentStrategy implements InvestmentStrategy {

    @Autowired
    private InvestmentSuggestionRepository investmentSuggestionRepository;

    @Override
    public List<InvestmentSuggestion> suggestInvestments(FinancialProfile profile, double investmentCapacity) {
        // Get low-risk investments with appropriate timeframe
        List<InvestmentSuggestion> suggestions = investmentSuggestionRepository
                .findByRiskLevelAndDurationMonthsLessThanEqual("LOW", profile.getInvestmentTimeframeMonths());

        // Filter by investment amount and prioritize security over returns
        return suggestions.stream()
                .filter(s -> s.getMinimumInvestmentAmount() <= investmentCapacity)
                .sorted((s1, s2) -> {
                    // First sort by safety (lowest risk), then by return
                    if (s1.getRiskLevel().equals(s2.getRiskLevel())) {
                        return s2.getExpectedReturnPercentage().compareTo(s1.getExpectedReturnPercentage());
                    }
                    return s1.getRiskLevel().compareTo(s2.getRiskLevel()); // "LOW" comes before "MEDIUM"
                })
                .collect(Collectors.toList());
    }

    @Override
    public String generateAdvice(FinancialProfile profile) {
        StringBuilder advice = new StringBuilder();

        advice.append("Conservative Investment Strategy Recommendations:\n\n");
        advice.append("Based on your low risk tolerance, we recommend focusing on capital preservation and steady income.\n\n");

        advice.append("Recommended Investment Mix:\n");
        advice.append("- 50-70% in high-quality bonds and fixed-income securities\n");
        advice.append("- 20-30% in blue-chip dividend stocks\n");
        advice.append("- 10-20% in cash equivalents for liquidity\n\n");

        advice.append("Key Benefits:\n");
        advice.append("- Lower volatility and more predictable returns\n");
        advice.append("- Better protection during market downturns\n");
        advice.append("- Regular income through interest and dividends\n\n");

        // Add timeframe-specific advice
        if (profile.getInvestmentTimeframeMonths() < 24) {
            advice.append("For your short-term goals, consider high-yield savings accounts, certificates of deposit, or short-term bond funds which provide liquidity and minimal risk.\n");
        } else {
            advice.append("Even with a longer time horizon, maintaining a conservative approach means focusing on quality fixed-income investments and dividend aristocrats that have consistently increased dividends over decades.\n");
        }

        return advice.toString();
    }
}