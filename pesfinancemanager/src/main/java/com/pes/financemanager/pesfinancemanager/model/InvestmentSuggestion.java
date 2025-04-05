package com.pes.financemanager.pesfinancemanager.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "investment_suggestions")
public class InvestmentSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String riskLevel; // LOW, MEDIUM, HIGH

    @Column(nullable = false)
    private Integer durationMonths;

    @Column(nullable = false)
    private Double expectedReturnPercentage;

    @Column(nullable = false)
    private Double minimumInvestmentAmount;

    @Column(columnDefinition = "TEXT")
    private String educationalContent;

    // Constructors, getters, and setters
    public InvestmentSuggestion() {}

    public InvestmentSuggestion(String name, String description, String riskLevel,
                                Integer durationMonths, Double expectedReturnPercentage,
                                Double minimumInvestmentAmount, String educationalContent) {
        this.name = name;
        this.description = description;
        this.riskLevel = riskLevel;
        this.durationMonths = durationMonths;
        this.expectedReturnPercentage = expectedReturnPercentage;
        this.minimumInvestmentAmount = minimumInvestmentAmount;
        this.educationalContent = educationalContent;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public Integer getDurationMonths() { return durationMonths; }
    public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }

    public Double getExpectedReturnPercentage() { return expectedReturnPercentage; }
    public void setExpectedReturnPercentage(Double expectedReturnPercentage) {
        this.expectedReturnPercentage = expectedReturnPercentage;
    }

    public Double getMinimumInvestmentAmount() { return minimumInvestmentAmount; }
    public void setMinimumInvestmentAmount(Double minimumInvestmentAmount) {
        this.minimumInvestmentAmount = minimumInvestmentAmount;
    }

    public String getEducationalContent() { return educationalContent; }
    public void setEducationalContent(String educationalContent) {
        this.educationalContent = educationalContent;
    }
}