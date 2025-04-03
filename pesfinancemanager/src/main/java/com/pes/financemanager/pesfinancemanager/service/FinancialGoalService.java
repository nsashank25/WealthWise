package com.pes.financemanager.pesfinancemanager.service;
import com.pes.financemanager.pesfinancemanager.model.FinancialGoal;
import com.pes.financemanager.pesfinancemanager.model.User;
import com.pes.financemanager.pesfinancemanager.repository.FinancialGoalRepository;
import com.pes.financemanager.pesfinancemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FinancialGoalService {

    @Autowired
    private FinancialGoalRepository financialGoalRepository;

    @Autowired
    private UserRepository userRepository;

    public FinancialGoal createGoal(Long userId, FinancialGoal goal) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        goal.setUser(userOptional.get());
        return financialGoalRepository.save(goal);
    }

    public List<FinancialGoal> getUserGoals(Long userId) {
        return financialGoalRepository.findByUserId(userId);
    }

    public FinancialGoal updateSavings(Long goalId, Double amount) {
        FinancialGoal goal = financialGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        double newSavings = goal.getCurrentSavings() + amount;

        if (newSavings > goal.getTargetAmount()) {
            throw new RuntimeException("Savings cannot exceed the target amount.");
        }

        goal.setCurrentSavings(newSavings);
        return financialGoalRepository.save(goal);
    }
}
