import React, { useState } from "react";
import ExpenseForm from "./ExpenseForm";
import ExpenseList from "./ExpenseList";
import { undoLastExpenseOperation, redoLastExpenseOperation } from "../services/api";

const ExpenseApp = () => {
  const userId = localStorage.getItem("userId");
  const [updateExpenses, setUpdateExpenses] = useState(false);
  const [alertMessage, setAlertMessage] = useState(null);

  const handleUndo = async () => {
    try {
      const response = await undoLastExpenseOperation();
      setAlertMessage(response.data);
      setUpdateExpenses(!updateExpenses); // Trigger re-fetch of expense list
    } catch (error) {
      setAlertMessage(error.response?.data || "Error undoing operation");
      console.error("Error undoing operation:", error);
    }
  };

  const handleRedo = async () => {
    try {
      const response = await redoLastExpenseOperation();
      setAlertMessage(response.data);
      setUpdateExpenses(!updateExpenses); // Trigger re-fetch of expense list
    } catch (error) {
      setAlertMessage(error.response?.data || "Error redoing operation");
      console.error("Error redoing operation:", error);
    }
  };

  return (
    <div className="expense-container">
      <div className="app-header">
        <h1 className="app-title">Expense Tracker</h1>
        {alertMessage && (
          <div className="alert-message">
            <p>{alertMessage}</p>
            <button 
              className="close-alert" 
              onClick={() => setAlertMessage(null)}
            >
              ×
            </button>
          </div>
        )}
        <div className="action-buttons">
          <button 
            className="action-button undo-button" 
            onClick={handleUndo}
          >
            <span className="button-icon">↩</span> Undo
          </button>
          <button 
            className="action-button redo-button" 
            onClick={handleRedo}
          >
            <span className="button-icon">↪</span> Redo
          </button>
        </div>
      </div>
      
      <ExpenseForm 
        userId={userId} 
        onExpenseAdded={() => setUpdateExpenses(!updateExpenses)}
        setAlertMessage={setAlertMessage}
      />
      
      <ExpenseList 
        userId={userId} 
        updateTrigger={updateExpenses} 
      />
    </div>
  );
};

export default ExpenseApp;