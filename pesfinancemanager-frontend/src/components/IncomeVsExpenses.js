import React, { useState, useEffect } from "react";
import axios from "axios";
import { Pie } from "react-chartjs-2";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import "../styles/incomeVsExpenses.css";

ChartJS.register(ArcElement, Tooltip, Legend);

const IncomeVsExpenses = () => {
  const [reportData, setReportData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const userId = localStorage.getItem("userId");
    
  useEffect(() => {
    if (userId) {
      fetchReport();
    } else {
      setError("User not logged in.");
      setLoading(false);
    }
  }, [userId]);

  const fetchReport = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/api/reports/${userId}`);
      setReportData(response.data);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching financial report:", error);
      setError("Failed to load report data.");
      setLoading(false);
    }
  };

  const downloadReport = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/reports/${userId}/download`,{ responseType: "blob" });

      const blob = new Blob([response.data], { type: "application/pdf" });
      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = "Financial_Report.pdf";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } catch (error) {
      console.error("Error downloading the financial report:", error);
    }
  };

  if (loading) return <p>Loading...</p>;
  if (error) return <p>{error}</p>;

  const chartData = {
    labels: ["Income", "Expenses"],
    datasets: [
      {
        data: [reportData.totalIncome, reportData.totalExpenses],
        backgroundColor: ["#4CAF50", "#FF5733"],
      },
    ],
  };

  return (
    <div className="income-expenses-container">
      <h2>Income vs. Expenses Report</h2>
      <div className="chart-container">
        <Pie data={chartData} />
      </div>
      <button className="download-btn" onClick={downloadReport}>
        Download PDF Report
      </button>
    </div>
  );
};

export default IncomeVsExpenses;
