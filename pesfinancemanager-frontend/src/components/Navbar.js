import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import "../styles/navbar.css";
import logo from "../images/logo.png";

const Navbar = ({ onLogout }) => {
  const navigate = useNavigate();
  const [isReportsOpen, setIsReportsOpen] = useState(false);

  return (
    <nav className="navbar">
      <div className="navbar-logo" onClick={() => navigate("/home")}>
        <img src={logo} alt="WealthWise Logo" className="logo-image" />
      </div>

      <ul className="navbar-links">
        <li><Link to="/expenses">Expenses</Link></li>
        <li><Link to="/financial-goals">Financial Goals</Link></li>
        <li><Link to="/budget-page">Manage Budget</Link></li>

        {/* Reports Dropdown */}
        <li 
          className="dropdown"
          onMouseEnter={() => setIsReportsOpen(true)}
          onMouseLeave={() => setIsReportsOpen(false)}
        >
          <span className="dropdown-toggle">Reports</span>
          {isReportsOpen && (
            <ul className="dropdown-menu">
              <li><Link to="/income-vs-expenses">Income vs. Expenses</Link></li>
              <li><Link to="/investment-suggestions">Investment Suggestions</Link></li>
              <li><Link to="/tax-estimator">Tax Estimator</Link></li>
              <li><Link to="/expense-reduction">Expense Reduction</Link></li>
            </ul>
          )}
        </li>

        <li className="logout-item" onClick={onLogout}>Logout</li>
      </ul>
    </nav>
  );
};

export default Navbar;
