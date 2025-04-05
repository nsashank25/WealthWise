import React, { useState } from "react";
import ExpenseForm from "./ExpenseForm";
import ExpenseList from "./ExpenseList";

const ExpenseApp = () => {
    const userId = localStorage.getItem("userId");
    const [updateExpenses, setUpdateExpenses] = useState(false);
    const [alertMessage, setAlertMessage] = useState(null);

    return (
        <div className="expense-container">
            <h1>Expense Tracker</h1>

            {alertMessage && (
                <div className="alert warning-alert">
                    {alertMessage}
                </div>
            )}

            <ExpenseForm
                userId={userId}
                onExpenseAdded={() => setUpdateExpenses(!updateExpenses)}
                setAlertMessage={setAlertMessage}
            />
            <ExpenseList key={updateExpenses} userId={userId} />
        </div>
    );
};

export default ExpenseApp;
