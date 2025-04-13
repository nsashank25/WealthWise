import React, { useEffect, useState, useCallback } from "react";
import { getExpenses } from "../services/api";

const ExpenseList = ({ userId, updateTrigger }) => {
  const [expenses, setExpenses] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [sortField, setSortField] = useState('date');
  const [sortDirection, setSortDirection] = useState('desc');

  const fetchExpenses = useCallback(async () => {
    setIsLoading(true);
    try {
      const response = await getExpenses(userId);
      setExpenses(response.data);
      setError(null);
    } catch (error) {
      console.error("Error fetching expenses:", error);
      setError("Failed to load expenses. Please try again later.");
    } finally {
      setIsLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    fetchExpenses();
  }, [fetchExpenses, updateTrigger]);

  const handleSort = (field) => {
    if (sortField === field) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortDirection('asc');
    }
  };

  const getSortedExpenses = () => {
    return [...expenses].sort((a, b) => {
      let comparison = 0;
      
      if (sortField === 'amount') {
        comparison = parseFloat(a.amount) - parseFloat(b.amount);
      } else if (sortField === 'date') {
        comparison = new Date(a.date) - new Date(b.date);
      } else {
        comparison = a[sortField].localeCompare(b[sortField]);
      }
      
      return sortDirection === 'asc' ? comparison : -comparison;
    });
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric' 
    });
  };

  const formatAmount = (amount) => {
    return parseFloat(amount).toFixed(2);
  };

  const getSortIcon = (field) => {
    if (sortField !== field) return null;
    return sortDirection === 'asc' ? '↑' : '↓';
  };

  return (
    <div className="expense-list-container">
      <h2>Your Expenses</h2>
      
      {isLoading && <div className="loading-spinner">Loading...</div>}
      
      {error && <div className="error-message">{error}</div>}
      
      {!isLoading && !error && expenses.length === 0 && (
        <div className="no-expenses">
          <p>No expenses recorded yet.</p>
          <p>Add your first expense using the form above.</p>
        </div>
      )}
      
      {!isLoading && !error && expenses.length > 0 && (
        <div className="table-container">
          <table className="expense-table">
            <thead>
              <tr>
                <th onClick={() => handleSort('date')} className="sortable-header">
                  Date {getSortIcon('date')}
                </th>
                <th onClick={() => handleSort('category')} className="sortable-header">
                  Category {getSortIcon('category')}
                </th>
                <th onClick={() => handleSort('amount')} className="sortable-header">
                  Amount (₹) {getSortIcon('amount')}
                </th>
                <th onClick={() => handleSort('description')} className="sortable-header">
                  Description {getSortIcon('description')}
                </th>
              </tr>
            </thead>
            <tbody>
              {getSortedExpenses().map((expense, index) => (
                <tr key={index} className="expense-row">
                  <td>{formatDate(expense.date)}</td>
                  <td>
                    <span className={`category-badge ${expense.category.toLowerCase()}`}>
                      {expense.category}
                    </span>
                  </td>
                  <td className="amount-cell">₹{formatAmount(expense.amount)}</td>
                  <td className="description-cell">{expense.description || "N/A"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default ExpenseList;