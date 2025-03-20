package com.pes.financemanager.pesfinancemanager.repository;

import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);  // Find expenses for a specific user

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId")
    double getTotalExpensesByUserId(Long userId);

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND YEAR(e.date) = :year AND MONTH(e.date) = :month")
    List<Expense> findMonthlyExpenses(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND YEAR(e.date) = :year")
    List<Expense> findYearlyExpenses(@Param("userId") Long userId, @Param("year") int year);

    List<Expense> findAllByUserId(Long userId);
}