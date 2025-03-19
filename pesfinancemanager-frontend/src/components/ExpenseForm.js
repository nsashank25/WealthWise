import React, { useState } from "react";
import { addExpense } from "../services/api";

const ExpenseForm = ({ userId, onExpenseAdded }) => {
    const [category, setCategory] = useState("Food");
    const [amount, setAmount] = useState("");
    const [date, setDate] = useState("");
    const [description, setDescription] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!amount || !date) {
            alert("Amount and Date are required!");
            return;
        }

        const expenseData = { category, amount, date, description };
        try {
            await addExpense(userId, expenseData);
            onExpenseAdded();
            setAmount("");
            setDate("");
            setDescription("");
        } catch (error) {
            console.error("Error adding expense:", error);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="expense-form">
            <label>Category:</label>
            <select value={category} onChange={(e) => setCategory(e.target.value)}>
                <option value="Food">Food</option>
                <option value="Travel">Travel</option>
                <option value="Utilities">Utilities</option>
                <option value="Entertainment">Entertainment</option>
                <option value="Other">Other</option>
            </select>

            <label>Amount:</label>
            <input
                type="number"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                required
            />

            <label>Date:</label>
            <input
                type="date"
                value={date}
                onChange={(e) => setDate(e.target.value)}
                required
            />

            <label>Description (Optional):</label>
            <input
                type="text"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
            />

            <button type="submit">Add Expense</button>
        </form>
    );
};

export default ExpenseForm;
