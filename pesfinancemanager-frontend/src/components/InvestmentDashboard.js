import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import "../styles/investmentDashboard.css";

const InvestmentDashboard = () => {
  const userId = localStorage.getItem("userId");

  const [profile, setProfile] = useState(null);
  const [suggestions, setSuggestions] = useState([]);
  const [advice, setAdvice] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const profileResponse = await axios.get(
          `http://localhost:8080/api/investments/profile/${userId}`,
          { withCredentials: true }
        );
        setProfile(profileResponse.data);

        const suggestionsResponse = await axios.get(
          `http://localhost:8080/api/investments/suggestions/${userId}`,
          { withCredentials: true }
        );
        setSuggestions(suggestionsResponse.data);

        const adviceResponse = await axios.get(
          `http://localhost:8080/api/investments/advice/${userId}`,
          { withCredentials: true }
        );
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

      <section className="investment-suggestions">
        <h3>Recommended Investment Options</h3>
        {suggestions.length === 0 ? (
          <p>
            No investment suggestions available. Please update your profile.
          </p>
        ) : (
          <div className="suggestion-cards">
            {suggestions.map((suggestion) => (
              <div key={suggestion.id} className="suggestion-card">
                <h4>{suggestion.name}</h4>
                <p className="description">{suggestion.description}</p>
                <div className="suggestion-details">
                  <p>
                    <strong>Risk Level:</strong> {suggestion.riskLevel}
                  </p>
                  <p>
                    <strong>Duration:</strong> {suggestion.durationMonths}{" "}
                    months
                  </p>
                  <p>
                    <strong>Expected Return:</strong>{" "}
                    {suggestion.expectedReturnPercentage}%
                  </p>
                  <p>
                    <strong>Minimum Investment:</strong> ₹
                    {suggestion.minimumInvestmentAmount.toFixed(2)}
                  </p>
                </div>
                <div className="educational-content">
                  <h5>Learn More</h5>
                  <p>{suggestion.educationalContent}</p>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
};

export default InvestmentDashboard;
