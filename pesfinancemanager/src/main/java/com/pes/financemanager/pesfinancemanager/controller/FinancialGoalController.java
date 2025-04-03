//package com.pes.financemanager.pesfinancemanager.controller;
//
//import com.pes.financemanager.pesfinancemanager.model.FinancialGoal;
//import com.pes.financemanager.pesfinancemanager.service.FinancialGoalService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/goals")
//@CrossOrigin(origins = "http://localhost:3000")
//public class FinancialGoalController {
//
//    @Autowired
//    private FinancialGoalService financialGoalService;
//
//    @PostMapping("/{userId}/add")
//    public FinancialGoal addGoal(@PathVariable Long userId, @RequestBody FinancialGoal goal) {
//        return financialGoalService.createGoal(userId, goal);
//    }
//
//    @GetMapping("/{userId}")
//    public List<FinancialGoal> getUserGoals(@PathVariable Long userId) {
//        return financialGoalService.getUserGoals(userId);
//    }
//
//    @PutMapping("/{goalId}/update-savings")
//    public FinancialGoal updateSavings(@PathVariable Long goalId, @RequestParam Double amount) {
//        return financialGoalService.updateSavings(goalId, amount);
//    }
//}


package com.pes.financemanager.pesfinancemanager.controller;
import com.pes.financemanager.pesfinancemanager.model.FinancialGoal;
import com.pes.financemanager.pesfinancemanager.service.FinancialGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
public class FinancialGoalController {

    @Autowired
    private FinancialGoalService financialGoalService;

    @PostMapping("/{userId}/add")
    public FinancialGoal addGoal(@PathVariable Long userId, @RequestBody FinancialGoal goal) {
        return financialGoalService.createGoal(userId, goal);
    }

    @GetMapping("/{userId}")
    public List<FinancialGoal> getUserGoals(@PathVariable Long userId) {
        return financialGoalService.getUserGoals(userId);
    }

    // Updated savings update endpoint to accept JSON payload
    @PatchMapping("/{goalId}/update-savings")
    public FinancialGoal updateSavings(@PathVariable Long goalId, @RequestBody Map<String, Double> request) {
        Double amount = request.get("amount");
        return financialGoalService.updateSavings(goalId, amount);
    }
}
