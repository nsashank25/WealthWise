//package com.pes.financemanager.pesfinancemanager.repository;
//
//import com.pes.financemanager.pesfinancemanager.model.Expense;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface ExpenseRepository extends JpaRepository<Expense, Long> {
//    List<Expense> findByUserId(Long userId);
//}

package com.pes.financemanager.pesfinancemanager.repository;

import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);  // Find expenses for a specific user
}