import React, { useState, useEffect } from "react";
import axios from "axios";

const FinancialGoals = () => {
  const userId = localStorage.getItem("userId");
  const [goals, setGoals] = useState([]);
  const [newGoal, setNewGoal] = useState({ goalName: "", targetAmount: "", deadline: "" });

  useEffect(() => {
    const fetchGoals = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/goals/${userId}`)
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
      window.location.reload(); // Refresh to show new goal
    } catch (error) {
      console.error("Error adding goal:", error);
    }
  };

  return (
    <div>
      <h2>Financial Goals</h2>
      <form onSubmit={handleGoalSubmit}>
        <input type="text" placeholder="Goal Name" value={newGoal.goalName} onChange={(e) => setNewGoal({ ...newGoal, goalName: e.target.value })} required />
        <input type="number" placeholder="Target Amount" value={newGoal.targetAmount} onChange={(e) => setNewGoal({ ...newGoal, targetAmount: e.target.value })} required />
        <input type="date" value={newGoal.deadline} onChange={(e) => setNewGoal({ ...newGoal, deadline: e.target.value })} required />
        <button type="submit">Add Goal</button>
      </form>

      <ul>
        {goals.map((goal) => (
          <li key={goal.id}>
            {goal.goalName} - ₹{goal.targetAmount} (Saved: ₹{goal.currentSavings})
          </li>
        ))}
      </ul>
    </div>
  );
};

export default FinancialGoals;
