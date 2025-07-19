// package com.pes.financemanager.pesfinancemanager.strategy;

// import com.pes.financemanager.pesfinancemanager.model.FinancialProfile;

// public interface InvestmentStrategy {
//     String generateAdvice(FinancialProfile profile);
// }

package com.pes.financemanager.pesfinancemanager.strategy;

import com.pes.financemanager.pesfinancemanager.model.FinancialProfile;
import com.pes.financemanager.pesfinancemanager.model.InvestmentSuggestion;
import java.util.List;

public interface InvestmentStrategy {
    List<InvestmentSuggestion> suggestInvestments(FinancialProfile profile, double investmentCapacity);
    String generateAdvice(FinancialProfile profile);
}