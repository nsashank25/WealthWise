import React, { useState, useEffect } from "react";
import axios from "axios";
import "../styles/financialGoals.css";

const FinancialGoals = () => {
  const userId = localStorage.getItem("userId");
  const [goals, setGoals] = useState([]);
  const [newGoal, setNewGoal] = useState({ goalName: "", targetAmount: "", deadline: "" });
  const [savingsInput, setSavingsInput] = useState({});

  useEffect(() => {
    const fetchGoals = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/goals/${userId}`);
        setGoals(response.data);
      } catch (error) {
        console.error("Error fetching goals:", error);
      }
    };
    fetchGoals();
  }, [userId]);

  const handleGoalSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post(`http://localhost:8080/api/goals/${userId}/add`, newGoal);
      setNewGoal({ goalName: "", targetAmount: "", deadline: "" });
      window.location.reload();
    } catch (error) {
      console.error("Error adding goal:", error);
    }
  };

  const handleAddSavings = async (goalId, targetAmount, currentSavings) => {
    const amount = parseFloat(savingsInput[goalId]) || 0;
    if (amount <= 0 || currentSavings + amount > targetAmount) return;

    try {
      await axios.patch(`http://localhost:8080/api/goals/${goalId}/update-savings`, 
        { amount }, 
        { headers: { "Content-Type": "application/json" } }
      );
      
      setSavingsInput({ ...savingsInput, [goalId]: "" });
      window.location.reload();
    } catch (error) {
      console.error("Error updating savings:", error);
    }
  };

  const ongoingGoals = goals.filter(goal => goal.currentSavings < goal.targetAmount);
  const completedGoals = goals.filter(goal => goal.currentSavings >= goal.targetAmount);

  return (
    <div className="financial-goals-container">
      <div className="goals-section">
        <div className="financial-goals-card">
          <h2>Ongoing Financial Goals</h2>
          <form onSubmit={handleGoalSubmit} className="goal-form">
            <input type="text" placeholder="Goal Name" value={newGoal.goalName} onChange={(e) => setNewGoal({ ...newGoal, goalName: e.target.value })} required />
            <input type="number" placeholder="Target Amount" value={newGoal.targetAmount} onChange={(e) => setNewGoal({ ...newGoal, targetAmount: e.target.value })} required />
            <input type="date" value={newGoal.deadline} onChange={(e) => setNewGoal({ ...newGoal, deadline: e.target.value })} required />
            <button type="submit" className="add-goal-button">Add Goal</button>
          </form>

          <ul className="goals-list">
            {ongoingGoals.map((goal) => (
              <li key={goal.id} className="goal-item">
                <strong>{goal.goalName}</strong> - ₹{goal.targetAmount}
                <span className="saved-amount">(Saved: ₹{goal.currentSavings} | Remaining: ₹{goal.targetAmount - goal.currentSavings})</span>
                <div className="savings-update">
                  <input type="number" placeholder="Add savings" value={savingsInput[goal.id] || ""} onChange={(e) => setSavingsInput({ ...savingsInput, [goal.id]: e.target.value })} />
                  <button onClick={() => handleAddSavings(goal.id, goal.targetAmount, goal.currentSavings)}>Save</button>
                </div>
              </li>
            ))}
          </ul>
        </div>

        <div className="financial-goals-card completed-goals">
          <h2>Completed Financial Goals</h2>
          <ul className="goals-list">
            {completedGoals.map((goal) => (
              <li key={goal.id} className="goal-item completed">
                <strong>{goal.goalName}</strong> - ₹{goal.targetAmount} (Completed)
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default FinancialGoals;
