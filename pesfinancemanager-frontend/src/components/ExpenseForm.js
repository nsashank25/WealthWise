import React, { useState } from "react";
import { addExpense } from "../services/api";

const ExpenseForm = ({ userId, onExpenseAdded, setAlertMessage }) => {
  const [category, setCategory] = useState("Food");
  const [amount, setAmount] = useState("");
  const [date, setDate] = useState("");
  const [description, setDescription] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!amount || !date) {
      setAlertMessage("Amount and Date are required!");
      return;
    }
    
    setIsSubmitting(true);
    const expenseData = { category, amount, date, description };
    
    try {
      const response = await addExpense(userId, expenseData);
      if (response.data.message) {
        setAlertMessage(response.data.message);
      } else {
        setAlertMessage(null);
      }
      onExpenseAdded();
      setAmount("");
      setDate("");
      setDescription("");
    } catch (error) {
      console.error("Error adding expense:", error);
      setAlertMessage("Failed to add expense. Please try again.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form className="expense-form" onSubmit={handleSubmit}>
      <h2 className="form-title">Add New Expense</h2>
      
      <div className="form-group">
        <label htmlFor="category">Category:</label>
        <select 
          id="category"
          value={category} 
          onChange={(e) => setCategory(e.target.value)}
          className="form-control"
        >
          <option value="Food">Food</option>
          <option value="Travel">Travel</option>
          <option value="Utilities">Utilities</option>
          <option value="Entertainment">Entertainment</option>
          <option value="Other">Other</option>
        </select>
      </div>
      
      <div className="form-group">
        <label htmlFor="amount">Amount:</label>
        <div className="input-with-icon">
          <span className="input-icon">₹</span>
          <input
            id="amount"
            type="number"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            placeholder="0.00"
            step="0.01"
            min="0"
            className="form-control with-icon"
            required
          />
        </div>
      </div>
      
      <div className="form-group">
        <label htmlFor="date">Date:</label>
        <input
          id="date"
          type="date"
          value={date}
          onChange={(e) => setDate(e.target.value)}
          className="form-control"
          required
        />
      </div>
      
      <div className="form-group">
        <label htmlFor="description">Description (Optional):</label>
        <textarea
          id="description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Enter details about this expense..."
          className="form-control"
          rows="3"
        />
      </div>
      
      <button 
        type="submit" 
        className="submit-button"
        disabled={isSubmitting}
      >
        {isSubmitting ? 'Adding...' : 'Add Expense'}
      </button>
    </form>
  );
};

export default ExpenseForm;