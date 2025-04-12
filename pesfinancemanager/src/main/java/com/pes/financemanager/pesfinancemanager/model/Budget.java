package com.pes.financemanager.pesfinancemanager.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Map;

@Entity
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String timePeriod; // "WEEKLY", "MONTHLY", "QUARTERLY"
    private LocalDate startDate;
    private LocalDate endDate;

    @ElementCollection
    private Map<String, Double> categoryAllocations;

    @ElementCollection
    private Map<String, Double> categorySpent;

    private double totalBudget;
    private double totalSpent;
    private int version;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Map<String, Double> getCategoryAllocations() {
        return categoryAllocations;
    }

    public void setCategoryAllocations(Map<String, Double> categoryAllocations) {
        this.categoryAllocations = categoryAllocations;
    }

    public Map<String, Double> getCategorySpent() {
        return categorySpent;
    }

    public void setCategorySpent(Map<String, Double> categorySpent) {
        this.categorySpent = categorySpent;
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
