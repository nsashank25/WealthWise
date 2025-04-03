package com.pes.financemanager.pesfinancemanager.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "financial_goals")
public class FinancialGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String goalName;

    @Column(nullable = false)
    private Double targetAmount;

    @Column(nullable = false)
    private Double currentSavings = 0.0;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt = LocalDate.now();

    // Constructor
    public FinancialGoal() {}

    public FinancialGoal(User user, String goalName, Double targetAmount, LocalDate deadline) {
        this.user = user;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.deadline = deadline;
        this.currentSavings = 0.0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getGoalName() { return goalName; }
    public void setGoalName(String goalName) { this.goalName = goalName; }

    public Double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(Double targetAmount) { this.targetAmount = targetAmount; }

    public Double getCurrentSavings() { return currentSavings; }
    public void setCurrentSavings(Double currentSavings) { this.currentSavings = currentSavings; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public LocalDate getCreatedAt() { return createdAt; }

    // New method to calculate the remaining amount
    public Double getRemainingAmount() {
        return targetAmount - currentSavings;
    }
}
