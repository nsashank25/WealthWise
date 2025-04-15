import React, { useState, useEffect } from "react";
import axios from "axios";
import "../styles/expenseReduction.css";
import Navbar from "./Navbar";
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from "recharts";

const ExpenseReduction = () => {
  const [expenses, setExpenses] = useState([]);
  const [savingsGoal, setSavingsGoal] = useState(0);
  const [savingsTimeframe, setSavingsTimeframe] = useState(6); // Default 6 months
  const [activeCategory, setActiveCategory] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    const fetchExpenses = async () => {
      if (!userId) {
        console.error("User ID not found in localStorage");
        setIsLoading(false);
        return;
      }
      
      try {
        setIsLoading(true);
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
        
        // Set default savings goal to 15% of total spending
        const totalSpending = sortedExpenses.reduce((sum, exp) => sum + exp.total, 0);
        setSavingsGoal(Math.round(totalSpending * 0.15));
      } catch (error) {
        console.error("Error fetching expenses:", error);
      } finally {
        setIsLoading(false);
      }
    };
    
    fetchExpenses();
  }, [userId]);

  // Cost-cutting suggestions based on category
  const expenseTips = {
    "Food": [
      "Cook meals at home instead of ordering takeout (potential savings: 60-70%).",
      "Plan weekly meals to avoid last-minute expensive purchases.",
      "Buy groceries in bulk and look for store discounts.",
      "Use cashback apps for grocery shopping."
    ],
    "Travel": [
      "Use public transportation or carpool instead of taxis (potential savings: 40-50%).",
      "Book flights and hotels in advance for better deals.",
      "Consider fuel-efficient routes to save on gas costs.",
      "Explore travel credit cards with reward points."
    ],
    "Utilities": [
      "Turn off lights and electronics when not in use (potential savings: 10-15%).",
      "Switch to energy-efficient appliances and LED bulbs.",
      "Check if your provider offers off-peak electricity discounts.",
      "Install a programmable thermostat to optimize heating/cooling."
    ],
    "Entertainment": [
      "Use streaming services instead of expensive cable subscriptions (potential savings: 30-50%).",
      "Look for free community events instead of paid activities.",
      "Set a monthly budget for leisure spending.",
      "Use student, senior, or group discounts where applicable."
    ],
    "Shopping": [
      "Wait 24 hours before making non-essential purchases (potential savings: 20-40%).",
      "Look for sales cycles and seasonal discounts for big purchases.",
      "Consider buying second-hand for certain items.",
      "Use price comparison tools when shopping online."
    ],
    "Other": [
      "Review and cancel unused subscriptions (potential savings: 5-10%).",
      "Compare insurance plans to find better rates.",
      "Track miscellaneous spending to identify avoidable expenses.",
      "Consider refinancing high-interest debt."
    ]
  };

  // Calculate potential monthly savings based on categories
  const calculatePotentialSavings = () => {
    let totalPotentialSavings = 0;
    
    expenses.forEach(({ category, total }) => {
      // Estimate potential savings based on category
      const savingsPercentage = getSavingsPercentage(category);
      totalPotentialSavings += (total * savingsPercentage);
    });
    
    return Math.round(totalPotentialSavings);
  };

  // Get estimated savings percentage by category
  const getSavingsPercentage = (category) => {
    switch(category) {
      case "Food": return 0.2; // 20% savings potential
      case "Travel": return 0.3; // 30% savings potential  
      case "Entertainment": return 0.4; // 40% savings potential
      case "Utilities": return 0.1; // 10% savings potential
      case "Shopping": return 0.25; // 25% savings potential
      default: return 0.15; // 15% for other categories
    }
  };

  // Calculate time to goal
  const calculateMonthsToGoal = () => {
    const monthlySavings = calculatePotentialSavings();
    if (monthlySavings <= 0 || savingsGoal <= 0) return 0;
    return Math.ceil(savingsGoal / monthlySavings);
  };

  // Calculate monthly amount needed to save to reach goal in the specified timeframe
  const calculateRequiredMonthlySavings = () => {
    if (savingsGoal <= 0 || savingsTimeframe <= 0) return 0;
    return Math.ceil(savingsGoal / savingsTimeframe);
  };

  // Colors for pie chart
  const COLORS = ['#00C49F', '#0088FE', '#FFBB28', '#FF8042', '#8884D8', '#82ca9d'];

  // Custom tooltip for pie chart
  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      const data = payload[0].payload;
      return (
        <div className="expense-reduction__custom-tooltip">
          <p className="expense-reduction__tooltip-category">{data.category}</p>
          <p className="expense-reduction__tooltip-amount">₹{data.total.toFixed(2)}</p>
          <p className="expense-reduction__tooltip-percent">{((data.total / expenses.reduce((sum, exp) => sum + exp.total, 0)) * 100).toFixed(1)}%</p>
        </div>
      );
    }
    return null;
  };

  // Toggle active category for detailed tips
  const toggleCategory = (category) => {
    if (activeCategory === category) {
      setActiveCategory(null);
    } else {
      setActiveCategory(category);
    }
  };

  return (
    <div className="expense-reduction__container">
      <Navbar />
      
      <div className="expense-reduction__dashboard">
        <div className="expense-reduction__header-section">
          <h1>Smart Expense Reduction</h1>
          <p className="expense-reduction__subtitle">Personalized cost-cutting strategies based on your spending habits</p>
        </div>
        
        {isLoading ? (
          <div className="expense-reduction__loading-spinner">
            <div className="expense-reduction__spinner"></div>
            <p>Analyzing your expenses...</p>
          </div>
        ) : expenses.length > 0 ? (
          <>
            <div className="expense-reduction__dashboard-grid">
              {/* Spending Distribution Chart */}
              <div className="expense-reduction__chart-card">
                <h3>Your Spending Distribution</h3>
                <div className="expense-reduction__chart-container">
                  <ResponsiveContainer width="100%" height={300}>
                    <PieChart>
                      <Pie
                        data={expenses}
                        cx="50%"
                        cy="50%"
                        outerRadius={100}
                        innerRadius={60}
                        fill="#8884d8"
                        dataKey="total"
                        nameKey="category"
                        // Removed the label property to avoid overlapping labels
                      >
                        {expenses.map((entry, index) => (
                          <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                        ))}
                      </Pie>
                      <Tooltip content={<CustomTooltip />} />
                      <Legend layout="vertical" align="right" verticalAlign="middle" />
                    </PieChart>
                  </ResponsiveContainer>
                  <div className="expense-reduction__chart-instructions">Hover over chart segments to see details</div>
                </div>
              </div>
              
              {/* Savings Calculator */}
              <div className="expense-reduction__savings-calculator-card">
                <h3>Savings Goal Calculator</h3>
                <div className="calculator-content">
                  <div className="expense-reduction__input-group">
                    <label>Your savings goal (₹)</label>
                    <input 
                      type="number" 
                      value={savingsGoal}
                      onChange={(e) => setSavingsGoal(Number(e.target.value))}
                      min="0"
                    />
                  </div>
                  
                  <div className="expense-reduction__input-group">
                    <label>Target timeframe (months)</label>
                    <input 
                      type="range" 
                      min="1" 
                      max="24" 
                      value={savingsTimeframe}
                      onChange={(e) => setSavingsTimeframe(Number(e.target.value))}
                      className="expense-reduction__slider"
                    />
                    <div className="expense-reduction__range-value">{savingsTimeframe} months</div>
                  </div>
                  
                  <div className="expense-reduction__savings-stats">
                    <div className="expense-reduction__stat-item">
                      <div className="expense-reduction__stat-value">₹{calculatePotentialSavings()}</div>
                      <div className="expense-reduction__stat-label">Potential monthly savings</div>
                    </div>
                    <div className="expense-reduction__stat-item">
                      <div className="expense-reduction__stat-value">₹{calculateRequiredMonthlySavings()}</div>
                      <div className="expense-reduction__stat-label">Required monthly savings</div>
                    </div>
                  </div>
                  
                  <div className="expense-reduction__savings-message">
                    {calculatePotentialSavings() > 0 ? (
                      calculatePotentialSavings() >= calculateRequiredMonthlySavings() ? (
                        <p className="expense-reduction__success-message">
                          Great news! By following our recommendations, you could reach your goal in {savingsTimeframe} months or less.
                        </p>
                      ) : (
                        <p className="expense-reduction__warning-message">
                          To reach your goal in {savingsTimeframe} months, you'll need to save ₹{calculateRequiredMonthlySavings() - calculatePotentialSavings()} more per month beyond our current recommendations.
                        </p>
                      )
                    ) : (
                      <p>Add more expense data to get accurate savings predictions.</p>
                    )}
                  </div>
                </div>
              </div>
            </div>
            
            {/* Expense Reduction Tips */}
            <div className="expense-reduction__reduction-card">
              <h2>Personalized Expense Reduction Tips</h2>
              <p>Click on a category to see detailed suggestions</p>
              
              <ul className="expense-reduction__suggestion-list">
                {expenses.map(({ category, total }, index) => (
                  <li 
                    key={index} 
                    className={`expense-reduction__suggestion-item ${activeCategory === category ? 'active' : ''}`}
                    onClick={() => toggleCategory(category)}
                    style={{ borderLeftColor: COLORS[index % COLORS.length] }}
                  >
                    <div className="expense-reduction__category-header">
                      <div>
                        <strong>{category}</strong>
                        <span className="expense-reduction__amount">₹{total.toFixed(2)} spent</span>
                      </div>
                      <div className="expense-reduction__potential-savings">
                        Potential monthly savings: ₹{(total * getSavingsPercentage(category)).toFixed(2)}
                      </div>
                      <div className="expense-reduction__expand-icon">{activeCategory === category ? '−' : '+'}</div>
                    </div>
                    
                    {activeCategory === category && (
                      <ul className="expense-reduction__tips-list">
                        {expenseTips[category]?.map((tip, tipIndex) => (
                          <li key={tipIndex} className="expense-reduction__tip-item">
                            <div className="expense-reduction__tip-icon">💡</div>
                            <div className="expense-reduction__tip-text">{tip}</div>
                          </li>
                        )) || <li>No suggestions available.</li>}
                      </ul>
                    )}
                  </li>
                ))}
              </ul>
            </div>
          </>
        ) : (
          <div className="expense-reduction__empty-state">
            <div className="expense-reduction__empty-icon">📊</div>
            <h3>No expenses found</h3>
            <p>Start tracking your spending to get personalized saving suggestions!</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ExpenseReduction;