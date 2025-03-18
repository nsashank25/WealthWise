//package com.pes.financemanager.pesfinancemanager.service;
//
//import com.pes.financemanager.pesfinancemanager.model.Expense;
//import com.pes.financemanager.pesfinancemanager.model.User;
//import com.pes.financemanager.pesfinancemanager.repository.ExpenseRepository;
//import com.pes.financemanager.pesfinancemanager.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class ExpenseService {
//
//    @Autowired
//    private ExpenseRepository expenseRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    public Expense addExpense(Long userId, Expense expense) {
//        Optional<User> userOptional = userRepository.findById(userId);
//        if (userOptional.isEmpty()) {
//            throw new RuntimeException("User not found");
//        }
//        expense.setUser(userOptional.get());
//        return expenseRepository.save(expense);
//    }
//
//    public List<Expense> getExpensesByUser(Long userId) {
//        Optional<User> userOptional = userRepository.findById(userId);
//        return userOptional.map(user -> expenseRepository.findByUserId(userId))  // ✅ Pass userId explicitly
//                .orElseThrow(() -> new RuntimeException("User not found"));
//    }
//
//}

package com.pes.financemanager.pesfinancemanager.service;

import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.model.User;
import com.pes.financemanager.pesfinancemanager.repository.ExpenseRepository;
import com.pes.financemanager.pesfinancemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    public Expense addExpense(Long userId, Expense expense) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        expense.setUser(userOptional.get());
        return expenseRepository.save(expense);
    }

    public List<Expense> getExpensesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return expenseRepository.findByUser(user);
    }
}