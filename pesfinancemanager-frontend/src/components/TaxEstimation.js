import React, { useState, useEffect, useCallback } from "react";
import axios from "axios";
import { Pie } from "react-chartjs-2";
import "../styles/taxEstimation.css";

// Utility function to format INR currency
const formatINR = (amount) => {
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  }).format(amount);
};

const TaxEstimation = () => {
  const [taxData, setTaxData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const userId = localStorage.getItem("userId");

  const fetchTaxEstimate = useCallback(async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/tax/${userId}`
      );
      setTaxData(response.data);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching tax estimation:", error);
      setError("Failed to load tax data.");
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    fetchTaxEstimate();
  }, [fetchTaxEstimate]);

  const [deductions, setDeductions] = useState({
    "80C": 0,
    "80D": 0,
    HRA: 0,
    NPS: 0,
  });

  const handleDeductionChange = (category, value) => {
    setDeductions({
      ...deductions,
      [category]: parseFloat(value) || 0,
    });
  };

  const calculateCustomTax = async () => {
    try {
      setLoading(true);
      const response = await axios.post(
        `http://localhost:8080/api/tax/${userId}`,
        deductions
      );
      setTaxData(response.data);
      setLoading(false);
    } catch (error) {
      console.error("Error calculating custom tax:", error);
      setError("Failed to calculate tax.");
      setLoading(false);
    }
  };

  if (loading) return <p>Loading tax estimation...</p>;
  if (error) return <p>{error}</p>;

  // Prepare chart data
  const chartData = {
    labels: ["Tax Liability", "Take Home"],
    datasets: [
      {
        data: [
          taxData.taxLiability,
          taxData.grossIncome - taxData.taxLiability,
        ],
        backgroundColor: ["#d63031", "#00b894"],
      },
    ],
  };

  return (
    <div className="tax-estimation-container">
      <div className="tax-summary-card">
        <h2>Tax Estimation</h2>

        <div className="tax-summary">
          <div className="chart-container">
            <Pie data={chartData} />
          </div>

          <div className="tax-details">
            <p>
              <strong>Gross Income:</strong> {formatINR(taxData.grossIncome)}
            </p>
            <p>
              <strong>Taxable Income:</strong> {formatINR(taxData.taxableIncome)}
            </p>
            <p>
              <strong>Tax Liability:</strong> {formatINR(taxData.taxLiability)}
            </p>
            <p>
              <strong>Effective Tax Rate:</strong>{" "}
              {taxData.effectiveTaxRate.toFixed(2)}%
            </p>
          </div>
        </div>
      </div>

      <div className="deduction-calculator">
        <h3>Calculate Your Tax Savings</h3>
        <div className="deduction-inputs">
          <div className="input-group">
            <label>80C (PPF, ELSS, etc.)</label>
            <input
              type="number"
              value={deductions["80C"]}
              onChange={(e) => handleDeductionChange("80C", e.target.value)}
              placeholder="Amount"
            />
            <span>Max: {formatINR(150000)}</span>
          </div>

          <div className="input-group">
            <label>80D (Health Insurance)</label>
            <input
              type="number"
              value={deductions["80D"]}
              onChange={(e) => handleDeductionChange("80D", e.target.value)}
              placeholder="Amount"
            />
            <span>Max: {formatINR(25000)}</span>
          </div>

          <div className="input-group">
            <label>HRA (House Rent)</label>
            <input
              type="number"
              value={deductions["HRA"]}
              onChange={(e) => handleDeductionChange("HRA", e.target.value)}
              placeholder="Amount"
            />
          </div>

          <div className="input-group">
            <label>NPS (Pension Scheme)</label>
            <input
              type="number"
              value={deductions["NPS"]}
              onChange={(e) => handleDeductionChange("NPS", e.target.value)}
              placeholder="Amount"
            />
            <span>Max: {formatINR(50000)}</span>
          </div>
        </div>

        <button className="calculate-btn" onClick={calculateCustomTax}>
          Calculate Tax
        </button>
      </div>

      <div className="tax-saving-tips">
        <h3 className="tips-heading">💡 Tax Saving Tips</h3>
        <ul>
          {taxData.taxSavingTips.map((tip, index) => (
            <li key={index}>
              <span className="tip-category">{tip.category}:</span>
              <span className="tip-description">
                Save up to <strong>{formatINR(tip.potential)}</strong> by{" "}
                {tip.description}
              </span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default TaxEstimation;
