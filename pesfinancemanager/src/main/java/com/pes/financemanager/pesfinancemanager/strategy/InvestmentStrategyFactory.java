package com.pes.financemanager.pesfinancemanager.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvestmentStrategyFactory {

    @Autowired
    private ConservativeInvestmentStrategy conservativeStrategy;

    @Autowired
    private ModerateInvestmentStrategy moderateStrategy;

    @Autowired
    private AggressiveInvestmentStrategy aggressiveStrategy;

    public InvestmentStrategy getStrategy(String riskTolerance) {
        switch (riskTolerance.toUpperCase()) {
            case "LOW":
                return conservativeStrategy;
            case "MEDIUM":
                return moderateStrategy;
            case "HIGH":
                return aggressiveStrategy;
            default:
                // Default to moderate strategy if risk tolerance is not specified
                return moderateStrategy;
        }
    }
}