import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import "../styles/investmentDashboard.css";

const InvestmentDashboard = () => {
  const userId = localStorage.getItem("userId");

  const [profile, setProfile] = useState(null);
  const [advice, setAdvice] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        
        // Always fetch profile data
        const profileResponse = await axios.get(
          `http://localhost:8080/api/investments/profile/${userId}`,
          { withCredentials: true }
        );
        setProfile(profileResponse.data);

        // Fetch advice based on user's profile
        const adviceUrl = `http://localhost:8080/api/investments/advice/${userId}`;
        const adviceResponse = await axios.get(adviceUrl, { withCredentials: true });
        setAdvice(adviceResponse.data.advice);

        setLoading(false);
      } catch (error) {
        console.error("Error fetching investment data:", error);
        setLoading(false);
      }
    };

    fetchData();
  }, [userId]);

  if (loading) {
    return <div className="loading">Loading investment recommendations...</div>;
  }

  const disposableIncome = profile
    ? profile.monthlyIncome - profile.monthlyExpenses
    : 0;
  const savingsToIncomeRatio = profile
    ? (profile.savingsAmount / profile.monthlyIncome).toFixed(1)
    : 0;

  // Wrap the content with a container for consistent layout and padding
  return (
    <div className="dashboard-container">
      <h2>Your Investment Dashboard</h2>

      <section className="financial-summary">
        <h3>Financial Health Summary</h3>
        <div className="metrics-grid">
          {[
            {
              label: "Monthly Income",
              value: `₹${profile?.monthlyIncome.toFixed(2)}`,
            },
            {
              label: "Monthly Expenses",
              value: `₹${profile?.monthlyExpenses.toFixed(2)}`,
            },
            {
              label: "Disposable Income",
              value: `₹${disposableIncome.toFixed(2)}`,
            },
            {
              label: "Savings",
              value: `₹${profile?.savingsAmount.toFixed(2)}`,
            },
            {
              label: "Savings Ratio",
              value: `${savingsToIncomeRatio} months of income`,
            },
            { label: "Risk Profile", value: profile?.riskTolerance },
          ].map((metric, i) => (
            <div key={i} className="metric-card">
              <h4>{metric.label}</h4>
              <p>{metric.value}</p>
            </div>
          ))}
        </div>
        <Link to="/update-profile" className="button secondary">
          Update Financial Profile
        </Link>
      </section>

      <section className="investment-advice">
        <h3>Personalized Investment Advice</h3>
        <div className="advice-card">
          <p className="advice-text">
            {advice.split("\n").map((text, i) => (
              <React.Fragment key={i}>
                {text}
                <br />
              </React.Fragment>
            ))}
          </p>
        </div>
      </section>
    </div>
  );
};

export default InvestmentDashboard;