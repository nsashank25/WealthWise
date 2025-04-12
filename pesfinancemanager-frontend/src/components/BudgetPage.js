import React, { useEffect, useState } from "react";
import axios from "axios";
import BudgetForm from "../components/BudgetForm";
import "../styles/BudgetPage.css";

const BudgetPage = () => {
  const [budgets, setBudgets] = useState([]);
  const [message, setMessage] = useState("");
  const [editingBudget, setEditingBudget] = useState(null);
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    const fetchBudgets = async () => {
      if (!userId) {
        setMessage("User ID not found. Please log in.");
        return;
      }

      try {
        const res = await axios.get(
          `http://localhost:8080/api/budgets/${userId}`
        );
        setBudgets(Array.isArray(res.data) ? res.data : []);
      } catch (error) {
        console.error("Error fetching budgets:", error);
        if (error.response) {
          setMessage(`Failed to load budgets: ${error.response.statusText}`);
        } else {
          setMessage("Error connecting to server");
        }
      }
    };

    fetchBudgets();
  }, [userId]);

  const handleCreate = async (budgetData) => {
    if (!userId) {
      setMessage("User ID not found. Please log in.");
      return;
    }

    const formattedData = {
      ...budgetData,
      userId,
    };

    try {
      const res = await axios.post(
        "http://localhost:8080/api/budgets",
        formattedData
      );
      setBudgets((prev) => [...prev, res.data]);
      setMessage("Budget created successfully!");
      setTimeout(() => setMessage(""), 3000);
    } catch (error) {
      console.error("Error creating budget:", error);
      if (error.response) {
        setMessage(`Failed to create budget: ${error.response.status}`);
      } else {
        setMessage("Error connecting to server");
      }
    }
  };

  const handleUpdateBudget = async (budgetId, updatedData) => {
    try {
      const res = await axios.put(
        `http://localhost:8080/api/budgets/${budgetId}`,
        updatedData
      );
      setBudgets((prev) => prev.map((b) => (b.id === budgetId ? res.data : b)));
      setMessage("Budget updated successfully!");
      setEditingBudget(null); // Exit edit mode
      setTimeout(() => setMessage(""), 3000);
    } catch (error) {
      console.error("Error updating budget:", error);
      setMessage("Failed to update budget.");
    }
  };

  const startEditing = (budget) => {
    setEditingBudget(budget);
  };

  const cancelEditing = () => {
    setEditingBudget(null);
  };

  const handleTrackExpense = async (budgetId, category, amount) => {
    try {
      const res = await axios.post(
        `http://localhost:8080/api/budgets/${budgetId}/track-specific?category=${category}&amount=${amount}`
      );
      setBudgets((prev) => prev.map((b) => (b.id === budgetId ? res.data : b)));
      setMessage("Expense tracked!");
      setTimeout(() => setMessage(""), 3000);
    } catch (err) {
      console.error("Error tracking expense:", err);
      setMessage("Failed to track expense.");
    }
  };

  const handleFetchDetails = async (budgetId) => {
    try {
      const res = await axios.get(
        `http://localhost:8080/api/budgets/${budgetId}/details`
      );
      alert(`Detailed budget: \n${JSON.stringify(res.data, null, 2)}`);
    } catch (error) {
      console.error("Error fetching details:", error);
      setMessage("Could not fetch detailed budget.");
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return "Not set";
    try {
      const date = new Date(dateString);
      return isNaN(date.getTime()) ? dateString : date.toLocaleDateString();
    } catch (e) {
      console.warn("Date formatting error:", e);
      return dateString;
    }
  };

  return (
    <div className="budget-page-container">
      <div className="budget-card">
        <h2>{editingBudget ? "Edit Budget" : "Budget Planning"}</h2>
        {message && <div className="message">{message}</div>}
        
        {editingBudget ? (
          <div className="edit-budget-section">
            <BudgetForm 
              onSubmit={(data) => handleUpdateBudget(editingBudget.id, data)} 
              initialData={editingBudget}
              isEditing={true}
            />
            <button className="cancel-edit-btn" onClick={cancelEditing}>
              Cancel Edit
            </button>
          </div>
        ) : (
          <BudgetForm onSubmit={handleCreate} />
        )}

        <h3>Your Budgets</h3>
        {budgets.length === 0 ? (
          <p>No budgets created yet. Create one above to get started!</p>
        ) : (
          <div className="budgets-list">
            {budgets.map((budget, i) => (
              <div key={i} className="budget-item">
                <h4>
                  {budget.timePeriod
                    ? budget.timePeriod.toLowerCase()
                    : "Budget"}
                </h4>
                <p>
                  <strong>Period:</strong> {formatDate(budget.startDate)} to{" "}
                  {formatDate(budget.endDate)}
                </p>
                <p>
                  <strong>Total Budget:</strong> ₹
                  {budget.totalBudget ? budget.totalBudget.toFixed(2) : "0.00"}
                </p>
                <p>
                  <strong>Spent so far:</strong> ₹
                  {budget.totalSpent ? budget.totalSpent.toFixed(2) : "0.00"}
                </p>
                <div className="budget-actions">
                  <button onClick={() => handleFetchDetails(budget.id)}>
                    View Details
                  </button>
                  <button 
                    className="edit-btn" 
                    onClick={() => startEditing(budget)}
                  >
                    Edit Budget
                  </button>
                </div>

                <TrackExpenseForm
                  budgetId={budget.id}
                  onTrack={handleTrackExpense}
                />

                {budget.categoryAllocations && (
                  <div className="category-breakdown">
                    <h5>Category Breakdown:</h5>
                    <ul>
                      {Object.entries(budget.categoryAllocations).map(
                        ([cat, amount]) => (
                          <li key={cat}>
                            <strong>{cat}:</strong> ₹{Number(amount).toFixed(2)}{" "}
                            allocated
                            {budget.categorySpent &&
                              typeof budget.categorySpent[cat] !==
                                "undefined" && (
                                <span>
                                  {" "}
                                  (₹
                                  {Number(budget.categorySpent[cat]).toFixed(
                                    2
                                  )}{" "}
                                  spent)
                                </span>
                              )}
                          </li>
                        )
                      )}
                    </ul>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

const TrackExpenseForm = ({ budgetId, onTrack }) => {
  const [category, setCategory] = useState("Food");
  const [amount, setAmount] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    if (amount && category) {
      onTrack(budgetId, category, parseFloat(amount));
      setAmount("");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="track-expense-form">
      <h5>Track Expense</h5>
      <select value={category} onChange={(e) => setCategory(e.target.value)}>
        <option value="Food">Food</option>
        <option value="Travel">Travel</option>
        <option value="Utilities">Utilities</option>
      </select>
      <input
        type="number"
        min="0"
        placeholder="Amount"
        value={amount}
        onChange={(e) => setAmount(e.target.value)}
        required
      />
      <button type="submit">Track</button>
    </form>
  );
};

export default BudgetPage;