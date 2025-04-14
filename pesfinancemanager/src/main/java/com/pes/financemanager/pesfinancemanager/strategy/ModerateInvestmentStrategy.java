package com.pes.financemanager.pesfinancemanager.strategy;

import com.pes.financemanager.pesfinancemanager.model.FinancialProfile;
import com.pes.financemanager.pesfinancemanager.model.InvestmentSuggestion;
import com.pes.financemanager.pesfinancemanager.repository.InvestmentSuggestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ModerateInvestmentStrategy implements InvestmentStrategy {

    @Autowired
    private InvestmentSuggestionRepository investmentSuggestionRepository;

    @Override
    public List<InvestmentSuggestion> suggestInvestments(FinancialProfile profile, double investmentCapacity) {
        // Get medium-risk investments with appropriate timeframe
        List<InvestmentSuggestion> suggestions = investmentSuggestionRepository
                .findByRiskLevelAndDurationMonthsLessThanEqual("MEDIUM", profile.getInvestmentTimeframeMonths());

        // Filter by investment amount and balance risk/return
        return suggestions.stream()
                .filter(s -> s.getMinimumInvestmentAmount() <= investmentCapacity)
                .sorted((s1, s2) -> {
                    // Sort by best risk-adjusted return (simplified approach)
                    double riskAdjustedReturn1 = s1.getExpectedReturnPercentage() /
                            (s1.getRiskLevel().equals("LOW") ? 1 :
                                    s1.getRiskLevel().equals("MEDIUM") ? 2 : 3);

                    double riskAdjustedReturn2 = s2.getExpectedReturnPercentage() /
                            (s2.getRiskLevel().equals("LOW") ? 1 :
                                    s2.getRiskLevel().equals("MEDIUM") ? 2 : 3);

                    return Double.compare(riskAdjustedReturn2, riskAdjustedReturn1);
                })
                .collect(Collectors.toList());
    }

    @Override
    public String generateAdvice(FinancialProfile profile) {
        StringBuilder advice = new StringBuilder();

        advice.append("Moderate Investment Strategy Recommendations:\n\n");
        advice.append("Based on your moderate risk tolerance, we recommend a balanced approach that provides growth potential with reasonable security.\n\n");

        advice.append("Recommended Investment Mix:\n");
        advice.append("- 40-50% in quality stocks, including growth and dividend-paying companies\n");
        advice.append("- 40-50% in investment-grade bonds and fixed-income securities\n");
        advice.append("- 5-10% in alternative investments (REITs, high-yield bonds)\n\n");

        advice.append("Key Benefits:\n");
        advice.append("- Balanced approach to growth and income\n");
        advice.append("- Reduced portfolio volatility compared to aggressive strategies\n");
        advice.append("- Potential for moderate long-term returns\n\n");

        // Add timeframe-specific advice
        if (profile.getInvestmentTimeframeMonths() < 36) {
            advice.append("For your medium-term goals, consider balanced mutual funds or ETFs that maintain a mix of stocks and bonds, providing a ready-made diversified portfolio.\n");
        } else {
            advice.append("With a longer time horizon, you might consider gradually increasing your allocation to stocks to capture more growth potential while maintaining a substantial bond position for stability.\n");
        }

        return advice.toString();
    }
}