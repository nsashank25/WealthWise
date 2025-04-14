package com.pes.financemanager.pesfinancemanager.strategy;

import com.pes.financemanager.pesfinancemanager.model.FinancialProfile;
import com.pes.financemanager.pesfinancemanager.model.InvestmentSuggestion;
import com.pes.financemanager.pesfinancemanager.repository.InvestmentSuggestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AggressiveInvestmentStrategy implements InvestmentStrategy {

    @Autowired
    private InvestmentSuggestionRepository investmentSuggestionRepository;

    @Override
    public List<InvestmentSuggestion> suggestInvestments(FinancialProfile profile, double investmentCapacity) {
        // Get high-risk investments with appropriate timeframe
        List<InvestmentSuggestion> suggestions = investmentSuggestionRepository
                .findByRiskLevelAndDurationMonthsLessThanEqual("HIGH", profile.getInvestmentTimeframeMonths());

        // If needed, supplement with some medium-risk options
        if (suggestions.size() < 5) {
            suggestions.addAll(investmentSuggestionRepository
                    .findByRiskLevelAndDurationMonthsLessThanEqual("MEDIUM", profile.getInvestmentTimeframeMonths()));
        }

        // Filter by investment amount and prioritize growth potential
        return suggestions.stream()
                .filter(s -> s.getMinimumInvestmentAmount() <= investmentCapacity)
                .sorted((s1, s2) -> {
                    // Sort primarily by expected return
                    return s2.getExpectedReturnPercentage().compareTo(s1.getExpectedReturnPercentage());
                })
                .collect(Collectors.toList());
    }

    @Override
    public String generateAdvice(FinancialProfile profile) {
        StringBuilder advice = new StringBuilder();

        advice.append("Aggressive Investment Strategy Recommendations:\n\n");
        advice.append("Based on your high risk tolerance, we recommend a growth-oriented approach focused on capital appreciation.\n\n");

        advice.append("Recommended Investment Mix:\n");
        advice.append("- 70-80% in stocks, including growth stocks and emerging markets\n");
        advice.append("- 10-20% in high-yield bonds and alternative investments\n");
        advice.append("- 5-10% in cash equivalents for opportunistic purchases\n\n");

        advice.append("Key Benefits:\n");
        advice.append("- Maximum growth potential for long-term wealth building\n");
        advice.append("- Opportunity to outpace inflation significantly\n");
        advice.append("- Potential for higher overall returns\n\n");

        advice.append("Important Considerations:\n");
        advice.append("- Higher volatility means you'll experience more significant market swings\n");
        advice.append("- This strategy works best with a longer time horizon (5+ years)\n");
        advice.append("- Important to maintain discipline during market downturns\n\n");

        // Add timeframe-specific advice
        if (profile.getInvestmentTimeframeMonths() < 48) {
            advice.append("Note: Your current time horizon is shorter than ideal for an aggressive strategy. Consider allocating only a portion of your portfolio to this approach, or extending your time horizon if possible.\n");
        } else {
            advice.append("With your longer time horizon, you're well-positioned to benefit from this growth-oriented approach. Consider dollar-cost averaging into the market to mitigate timing risk.\n");
        }

        return advice.toString();
    }
}