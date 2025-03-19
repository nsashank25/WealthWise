import React, { useState } from "react";
import ExpenseForm from "./ExpenseForm";
import ExpenseList from "./ExpenseList";

const ExpenseApp = () => {
    const userId = localStorage.getItem("userId");
    const [updateExpenses, setUpdateExpenses] = useState(false);

    return (
        <div className="expense-container">
            <h1>Expense Tracker</h1>
            <ExpenseForm userId={userId} onExpenseAdded={() => setUpdateExpenses(!updateExpenses)} />
            <ExpenseList key={updateExpenses} userId={userId} />
        </div>
    );
};

export default ExpenseApp;
