import React, { useState, useEffect } from "react";
import "./../styles/budget.css";

const BudgetForm = ({ onSubmit, initialData = null, isEditing = false }) => {
  const [form, setForm] = useState({
    timePeriod: "MONTHLY",
    startDate: "",
    endDate: "",
    categoryAllocations: { Food: 0, Travel: 0, Utilities: 0 },
  });
  
  const [totalBudget, setTotalBudget] = useState(0);

  // Initialize form with data when editing
  useEffect(() => {
    if (initialData && isEditing) {
      // Format dates for input fields
      const formatDateForInput = (dateString) => {
        if (!dateString) return "";
        try {
          const date = new Date(dateString);
          return isNaN(date.getTime()) 
            ? "" 
            : date.toISOString().split('T')[0];
        } catch (e) {
          return "";
        }
      };

      setForm({
        timePeriod: initialData.timePeriod || "MONTHLY",
        startDate: formatDateForInput(initialData.startDate),
        endDate: formatDateForInput(initialData.endDate),
        categoryAllocations: initialData.categoryAllocations || {
          Food: 0,
          Travel: 0,
          Utilities: 0,
        }
      });
    }
  }, [initialData, isEditing]);

  // Calculate total whenever categoryAllocations change
  useEffect(() => {
    const total = Object.values(form.categoryAllocations).reduce(
      (sum, value) => sum + (parseFloat(value) || 0),
      0
    );
    setTotalBudget(total);
  }, [form.categoryAllocations]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (Object.keys(form.categoryAllocations).includes(name)) {
      setForm({
        ...form,
        categoryAllocations: {
          ...form.categoryAllocations,
          [name]: parseFloat(value) || 0,
        },
      });
    } else {
      setForm({ ...form, [name]: value });
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    onSubmit({
      ...form,
      startDate: form.startDate ? form.startDate : null,
      endDate: form.endDate ? form.endDate : null,
      totalBudget: totalBudget
    });

    // Only reset form after submission if not editing
    if (!isEditing) {
      setForm({
        timePeriod: "MONTHLY",
        startDate: "",
        endDate: "",
        categoryAllocations: { Food: 0, Travel: 0, Utilities: 0 },
      });
    }
  };

  return (
    <form className="budget-form" onSubmit={handleSubmit}>
      <label>Time Period</label>
      <select name="timePeriod" onChange={handleChange} value={form.timePeriod}>
        <option value="WEEKLY">Weekly</option>
        <option value="MONTHLY">Monthly</option>
        <option value="QUARTERLY">Quarterly</option>
      </select>
      
      <label>Start Date</label>
      <input
        type="date"
        name="startDate"
        value={form.startDate}
        onChange={handleChange}
        required
      />
      
      <label>End Date</label>
      <input
        type="date"
        name="endDate"
        value={form.endDate}
        onChange={handleChange}
        required
      />
      
      <h4>Category Allocations</h4>
      {Object.keys(form.categoryAllocations).map((cat) => (
        <div key={cat} className="category-input">
          <label>{cat}</label>
          <input
            type="number"
            min="0"
            name={cat}
            value={form.categoryAllocations[cat]}
            onChange={handleChange}
          />
        </div>
      ))}
      
      <div className="budget-total">
        <strong>Total Budget: ₹{totalBudget.toFixed(2)}</strong>
      </div>
      
      <button type="submit">
        {isEditing ? "Update Budget" : "Create Budget"}
      </button>
    </form>
  );
};

export default BudgetForm;