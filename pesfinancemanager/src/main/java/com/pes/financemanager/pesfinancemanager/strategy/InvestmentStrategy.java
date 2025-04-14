package com.pes.financemanager.pesfinancemanager.strategy;

import com.pes.financemanager.pesfinancemanager.model.FinancialProfile;

public interface InvestmentStrategy {
    String generateAdvice(FinancialProfile profile);
}