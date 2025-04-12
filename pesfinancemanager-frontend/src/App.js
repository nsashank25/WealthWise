import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate } from "react-router-dom";
import Signup from "./components/Signup";
import Login from "./components/Login";
import HomePage from "./components/HomePage";
import Expenses from "./components/Expenses";
import Navbar from "./components/Navbar";
import IncomeVsExpenses from "./components/IncomeVsExpenses";
import ExpenseReduction from "./components/ExpenseReduction";
import FinancialGoals from "./components/FinancialGoals";
import InvestmentDashboard from "./components/InvestmentDashboard";
import FinancialProfileForm from "./components/FinancialProfileForm";
import TaxEstimation from "./components/TaxEstimation";
import BudgetPage from "./components/BudgetPage";

function App() {
  return (
    <Router>
      <AppRoutes />
    </Router>
  );
}

function AppRoutes() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const loginStatus = localStorage.getItem("isLoggedIn") === "true";
    setIsAuthenticated(loginStatus);
  }, []);

  const handleLoginSuccess = () => {
    localStorage.setItem("isLoggedIn", "true");
    setIsAuthenticated(true);
    navigate("/home");
  };

  const handleLogout = () => {
    localStorage.removeItem("isLoggedIn");
    setIsAuthenticated(false);
    navigate("/");
  };

  return (
    <>
      {/* Show Navbar only if user is authenticated */}
      {isAuthenticated && <Navbar onLogout={handleLogout} />}

      <Routes>
        <Route
          path="/"
          element={
            isAuthenticated ? (
              <Navigate to="/home" replace />
            ) : (
              <Login
                switchToSignup={() => navigate("/signup")}
                onLoginSuccess={handleLoginSuccess}
              />
            )
          }
        />
        <Route path="/signup" element={<Signup switchToLogin={() => navigate("/")}/>} />
        <Route
          path="/home"
          element={
            isAuthenticated ? (
              <HomePage onLogout={handleLogout} />
            ) : (
              <Navigate to="/" replace />
            )
          }
        />
        <Route
          path="/expenses"
          element={
            isAuthenticated ? <Expenses /> : <Navigate to="/" replace />
          }
        />
        <Route path="/income-vs-expenses" element={<IncomeVsExpenses />} />
        <Route path="/expense-reduction" element={<ExpenseReduction />} />
        <Route path="/financial-goals" element={<FinancialGoals />} />
        <Route path="/investment-suggestions" element={<InvestmentDashboard />} />
        <Route path="/update-profile" element={<FinancialProfileForm />} />
        <Route path="/tax-estimator" element={<TaxEstimation />} />
        <Route path="/budget-page" element={<BudgetPage />} />
        
      </Routes>
    </>
  );
}

export default App;