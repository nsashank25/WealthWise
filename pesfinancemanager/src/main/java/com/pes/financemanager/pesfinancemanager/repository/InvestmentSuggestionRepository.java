package com.pes.financemanager.pesfinancemanager.repository;

import com.pes.financemanager.pesfinancemanager.model.InvestmentSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvestmentSuggestionRepository extends JpaRepository<InvestmentSuggestion, Long> {
    List<InvestmentSuggestion> findByRiskLevel(String riskLevel);
    List<InvestmentSuggestion> findByRiskLevelAndDurationMonthsLessThanEqual(String riskLevel, Integer durationMonths);
    List<InvestmentSuggestion> findByMinimumInvestmentAmountLessThanEqual(Double amount);
}