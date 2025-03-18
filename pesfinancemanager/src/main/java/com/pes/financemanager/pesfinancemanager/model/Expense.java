//package com.pes.financemanager.pesfinancemanager.model;
//
//import jakarta.persistence.*;
//import java.time.LocalDate;
//
//@Entity
//@Table(name = "expenses")
//public class Expense {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;  // Link expenses to a user
//
//    @Column(nullable = false)
//    private String category;  // Food, Travel, etc.
//
//    @Column(nullable = false)
//    private Double amount;
//
//    @Column(nullable = false)
//    private LocalDate date;
//
//    @Column(length = 255)
//    private String description;  // Optional note
//
//    // Constructors
//    public Expense() {}
//
//    public Expense(User user, String category, Double amount, LocalDate date, String description) {
//        this.user = user;
//        this.category = category;
//        this.amount = amount;
//        this.date = date;
//        this.description = description;
//    }
//
//    // Getters and Setters
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//
//    public User getUser() { return user; }
//    public void setUser(User user) { this.user = user; }
//
//    public String getCategory() { return category; }
//    public void setCategory(String category) { this.category = category; }
//
//    public Double getAmount() { return amount; }
//    public void setAmount(Double amount) { this.amount = amount; }
//
//    public LocalDate getDate() { return date; }
//    public void setDate(LocalDate date) { this.date = date; }
//
//    public String getDescription() { return description; }
//    public void setDescription(String description) { this.description = description; }
//}

package com.pes.financemanager.pesfinancemanager.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Link expenses to a user

    @Column(nullable = false)
    private String category;  // Food, Travel, etc.

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 255)
    private String description;  // Optional note

    // Constructors
    public Expense() {}

    public Expense(User user, String category, Double amount, LocalDate date, String description) {
        this.user = user;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}