package com.pes.financemanager.pesfinancemanager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "financial_profiles")
public class FinancialProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double monthlyIncome;

    @Column(nullable = false)
    private Double monthlyExpenses;

    @Column(nullable = false)
    private Double savingsAmount;

    @Column(nullable = false)
    private String riskTolerance; // LOW, MEDIUM, HIGH

    @Column(nullable = true)
    private Integer investmentTimeframeMonths;

    // Constructors, getters, and setters
    public FinancialProfile() {}

    public FinancialProfile(User user, Double monthlyIncome, Double monthlyExpenses,
                            Double savingsAmount, String riskTolerance,
                            Integer investmentTimeframeMonths) {
        this.user = user;
        this.monthlyIncome = monthlyIncome;
        this.monthlyExpenses = monthlyExpenses;
        this.savingsAmount = savingsAmount;
        this.riskTolerance = riskTolerance;
        this.investmentTimeframeMonths = investmentTimeframeMonths;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(Double monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    public Double getMonthlyExpenses() { return monthlyExpenses; }
    public void setMonthlyExpenses(Double monthlyExpenses) { this.monthlyExpenses = monthlyExpenses; }

    public Double getSavingsAmount() { return savingsAmount; }
    public void setSavingsAmount(Double savingsAmount) { this.savingsAmount = savingsAmount; }

    public String getRiskTolerance() { return riskTolerance; }
    public void setRiskTolerance(String riskTolerance) { this.riskTolerance = riskTolerance; }

    public Integer getInvestmentTimeframeMonths() { return investmentTimeframeMonths; }
    public void setInvestmentTimeframeMonths(Integer investmentTimeframeMonths) {
        this.investmentTimeframeMonths = investmentTimeframeMonths;
    }

    public Double getMonthlyDisposableIncome() {
        return monthlyIncome - monthlyExpenses;
    }
}