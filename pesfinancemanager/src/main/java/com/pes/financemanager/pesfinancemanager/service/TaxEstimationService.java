package com.pes.financemanager.pesfinancemanager.service;

import com.pes.financemanager.pesfinancemanager.model.TaxBracket;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaxEstimationService {

    // Income tax brackets and rates (example for India)
    private final List<TaxBracket> incomeTaxBrackets = Arrays.asList(
            new TaxBracket(0, 250000, 0),
            new TaxBracket(250001, 500000, 5),
            new TaxBracket(500001, 750000, 10),
            new TaxBracket(750001, 1000000, 15),
            new TaxBracket(1000001, 1250000, 20),
            new TaxBracket(1250001, 1500000, 25),
            new TaxBracket(1500001, Double.MAX_VALUE, 30)
    );

    // Tax deduction categories
    private final Map<String, Double> standardDeductions = Map.of(
            "80C", 150000.0,   // PPF, ELSS, etc.
            "80D", 25000.0,    // Health Insurance
            "HRA", 60000.0,    // House Rent Allowance
            "NPS", 50000.0     // National Pension Scheme
    );

    public Map<String, Object> calculateTaxEstimate(double income, Map<String, Double> userDeductions) {
        double taxableIncome = calculateTaxableIncome(income, userDeductions);
        double taxLiability = calculateTaxLiability(taxableIncome);
        List<Map<String, Object>> taxSavingTips = generateTaxSavingTips(income, userDeductions);

        Map<String, Object> result = new HashMap<>();
        result.put("grossIncome", income);
        result.put("taxableIncome", taxableIncome);
        result.put("taxLiability", taxLiability);
        result.put("effectiveTaxRate", (taxLiability / income) * 100);
        result.put("taxSavingTips", taxSavingTips);

        return result;
    }

    private double calculateTaxableIncome(double income, Map<String, Double> userDeductions) {
        double totalDeductions = userDeductions.values().stream().mapToDouble(Double::doubleValue).sum();
        return Math.max(0, income - totalDeductions);
    }

    private double calculateTaxLiability(double taxableIncome) {
        double tax = 0;

        for (TaxBracket bracket : incomeTaxBrackets) {
            if (taxableIncome > bracket.getMin()) {
                double taxableInThisBracket = Math.min(taxableIncome, bracket.getMax()) - bracket.getMin();
                tax += taxableInThisBracket * (bracket.getRatePercent() / 100);
            }
        }

        double cess = tax * 0.04; // 4% Health & Education Cess
        return tax + cess;
    }

    private List<Map<String, Object>> generateTaxSavingTips(double income, Map<String, Double> userDeductions) {
        List<Map<String, Object>> tips = new ArrayList<>();

        standardDeductions.forEach((category, maxAmount) -> {
            double currentDeduction = userDeductions.getOrDefault(category, 0.0);
            if (currentDeduction < maxAmount) {
                Map<String, Object> tip = new HashMap<>();
                tip.put("category", category);
                tip.put("potential", maxAmount - currentDeduction);
                tip.put("description", getDeductionDescription(category));
                tips.add(tip);
            }
        });

        return tips;
    }

    private String getDeductionDescription(String category) {
        return switch (category) {
            case "80C" -> "Invest in tax-saving instruments like PPF (15-year lock-in), ELSS mutual funds (3-year lock-in), life insurance premiums, or 5-year tax-saving fixed deposits to claim deduction up to ₹1.5 lakh per year";
            case "80D" -> "Purchase health insurance for yourself (up to ₹25,000 deduction), additional ₹25,000 for parents, and ₹50,000 if parents are senior citizens. Premium payments can be made via any mode except cash";
            case "HRA" -> "If you're paying rent and not receiving HRA from your employer, claim deduction under Section 80GG up to ₹60,000 annually. If receiving HRA, claim exemption for the lowest of: actual HRA, 50% of salary (metro) or 40% (non-metro), or rent paid minus 10% of salary";
            case "NPS" -> "Contribute to National Pension Scheme Tier-1 account for an additional tax deduction of up to ₹50,000 under Section 80CCD(1B), over and above the ₹1.5 lakh limit of Section 80C. Offers market-linked returns with partial withdrawal options after 3 years";
            default -> "Check with a tax advisor for specific details";
        };
    }
}
