import React, { useState, useEffect } from "react";
import axios from "axios"; // Import axios for API calls
import "../styles/expenseReduction.css";
import Navbar from "./Navbar";

const ExpenseReduction = () => {
  const [expenses, setExpenses] = useState([]);
  const userId = localStorage.getItem("userId"); // Retrieve userId from localStorage

  useEffect(() => {
    const fetchExpenses = async () => {
      if (!userId) {
        console.error("User ID not found in localStorage");
        return;
      }

      try {
        const response = await axios.get(`http://localhost:8080/api/expenses/${userId}`);
        const expenseData = response.data;

        // Aggregate total spending per category
        const categoryTotals = {};
        expenseData.forEach(({ category, amount }) => {
          categoryTotals[category] = (categoryTotals[category] || 0) + amount;
        });

        // Convert to sorted array (highest to lowest)
        const sortedExpenses = Object.entries(categoryTotals)
          .map(([category, total]) => ({ category, total }))
          .sort((a, b) => b.total - a.total);

        setExpenses(sortedExpenses);
      } catch (error) {
        console.error("Error fetching expenses:", error);
      }
    };

    fetchExpenses();
  }, [userId]);

  // Cost-cutting suggestions based on category
  const expenseTips = {
    "Food": [
      "Cook meals at home instead of ordering takeout.",
      "Plan weekly meals to avoid last-minute expensive purchases.",
      "Buy groceries in bulk and look for store discounts."
    ],
    "Travel": [
      "Use public transportation or carpool instead of taxis.",
      "Book flights and hotels in advance for better deals.",
      "Consider fuel-efficient routes to save on gas costs."
    ],
    "Utilities": [
      "Turn off lights and electronics when not in use.",
      "Switch to energy-efficient appliances and LED bulbs.",
      "Check if your provider offers off-peak electricity discounts."
    ],
    "Entertainment": [
      "Use streaming services instead of expensive cable subscriptions.",
      "Look for free community events instead of paid activities.",
      "Set a monthly budget for leisure spending."
    ],
    "Other": [
      "Review and cancel unused subscriptions.",
      "Compare insurance plans to find better rates.",
      "Track miscellaneous spending to identify avoidable expenses."
    ]
  };

  return (
    <div className="expense-reduction-container">
      <Navbar />
      
      <div className="reduction-card">
        <h1>Expense Reduction Tips</h1>
        <p>Personalized cost-cutting strategies based on your spending habits.</p>

        {expenses.length > 0 ? (
          <ul className="suggestion-list">
            {expenses.map(({ category, total }, index) => (
              <li key={index} className="suggestion-item">
                <strong>{category} (₹{total.toFixed(2)} spent):</strong>
                <ul>
                  {expenseTips[category]?.map((tip, tipIndex) => (
                    <li key={tipIndex}>{tip}</li>
                  )) || <li>No suggestions available.</li>}
                </ul>
              </li>
            ))}
          </ul>
        ) : (
          <p>No expenses found. Start tracking your spending!</p>
        )}
      </div>
    </div>
  );
};

export default ExpenseReduction;
