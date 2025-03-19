import React, { useEffect, useState, useCallback } from "react";
import { getExpenses } from "../services/api";

const ExpenseList = ({ userId }) => {
    const [expenses, setExpenses] = useState([]);

    const fetchExpenses = useCallback(async () => {
        try {
            const response = await getExpenses(userId);
            setExpenses(response.data);
        } catch (error) {
            console.error("Error fetching expenses:", error);
        }
    }, [userId]);

    useEffect(() => {
        fetchExpenses();
    }, [fetchExpenses]);

    return (
        <div className="expense-list-container">
            <h2>Your Expenses</h2>
            {expenses.length === 0 ? (
                <p className="no-expenses">No expenses recorded yet.</p>
            ) : (
                <table className="expense-table">
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Category</th>
                            <th>Amount ($)</th>
                            <th>Description</th>
                        </tr>
                    </thead>
                    <tbody>
                        {expenses.map((expense) => (
                            <tr key={expense.id}>
                                <td>{expense.date}</td>
                                <td>{expense.category}</td>
                                <td>{expense.amount}</td>
                                <td>{expense.description || "N/A"}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default ExpenseList;
