import React, { useState, useEffect } from "react";
import axios from "axios";
import { Pie, Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  ArcElement,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend,
} from "chart.js";
import "../styles/incomeVsExpenses.css";

ChartJS.register(
  ArcElement,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend
);

// Currency formatter for Indian locale
const formatCurrencyIN = (amount) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  }).format(amount);

const IncomeVsExpenses = () => {
  const [reportData, setReportData] = useState(null);
  const [monthlyTrend, setMonthlyTrend] = useState([]);
  const [topCategories, setTopCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    const fetchReport = async () => {
      try {
        const [reportRes, trendRes, categoriesRes] = await Promise.all([
          axios.get(`http://localhost:8080/api/reports/${userId}`),
          axios.get(
            `http://localhost:8080/api/reports/monthly-trend/${userId}`
          ),
          axios.get(
            `http://localhost:8080/api/reports/top-categories/${userId}`
          ),
        ]);

        setReportData(reportRes.data);
        setMonthlyTrend(trendRes.data);
        setTopCategories(categoriesRes.data);
        setLoading(false);
      } catch (error) {
        console.error("Error fetching financial report:", error);
        setError("Failed to load report data.");
        setLoading(false);
      }
    };

    if (userId) {
      fetchReport();
    } else {
      setError("User not logged in.");
      setLoading(false);
    }
  }, [userId]);

  const downloadReport = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/reports/${userId}/download`,
        { responseType: "blob" }
      );

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

  if (loading) return (
    <div className="loading-container">
      <div className="loader"></div>
      <p>Loading your financial dashboard...</p>
    </div>
  );
  
  if (error) return (
    <div className="error-container">
      <div className="error-icon">!</div>
      <p>{error}</p>
      <button onClick={() => window.location.reload()} className="retry-btn">
        Retry
      </button>
    </div>
  );

  const spent = reportData.totalExpenses;
  const remaining = Math.max(0, reportData.totalIncome - spent);

  const chartData = {
    labels: ["Spent", "Remaining"],
    datasets: [
      {
        data: [spent, remaining],
        backgroundColor: ["#d63031", "#00b894"],
        borderWidth: 0,
        hoverOffset: 10,
      },
    ],
  };

  const barChartData = {
    labels: monthlyTrend.map((item) => item.month),
    datasets: [
      {
        label: "Monthly Income",
        backgroundColor: "#00b894",
        data: monthlyTrend.map((item) => item.income),
        borderRadius: 8,
      },
      {
        label: "Monthly Expenses",
        backgroundColor: "#d63031",
        data: monthlyTrend.map((item) => item.expenses),
        borderRadius: 8,
      },
    ],
  };

  const barChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: "top" },
      tooltip: {
        backgroundColor: 'rgba(0, 0, 0, 0.8)',
        padding: 12,
        cornerRadius: 8,
        titleFont: {
          size: 14,
          weight: 'bold'
        },
        bodyFont: {
          size: 13
        },
        callbacks: {
          label: function(context) {
            return `${context.dataset.label}: ${formatCurrencyIN(context.raw)}`;
          }
        }
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        grid: {
          color: 'rgba(255, 255, 255, 0.05)'
        }
      },
      x: {
        grid: {
          display: false
        }
      }
    }
  };

  const pieOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom',
        labels: {
          padding: 20,
          font: {
            size: 14
          }
        }
      },
      tooltip: {
        callbacks: {
          label: function(context) {
            const value = context.raw;
            const total = context.chart.getDatasetMeta(0).total;
            const percentage = Math.round((value / total) * 100);
            return `${context.label}: ${formatCurrencyIN(value)} (${percentage}%)`;
          }
        }
      }
    }
  };

  // Get max value for category progress bars
  const maxCategoryValue = Math.max(...topCategories.map(cat => cat.amount || 0));

  return (
    <div className="income-expenses-page">
      <div className="dashboard-grid">
        <div className="card income-expenses-card">
          <h2>Income vs. Expenses</h2>
          <div className="chart-container">
            <Pie data={chartData} options={pieOptions} />
          </div>
          <button className="download-btn" onClick={downloadReport}>
            DOWNLOAD REPORT
          </button>
        </div>
        
        <div className="summary card">
          <h3 className="summary-title">Financial Summary</h3>
          <div className="summary-items">
            <div className="summary-item income">
              <div className="summary-item-label">Monthly Income</div>
              <div className="summary-item-value">
                {formatCurrencyIN(reportData.totalIncome)}
              </div>
            </div>
            
            <div className="summary-item expenses">
              <div className="summary-item-label">Total Expenses</div>
              <div className="summary-item-value">
                {formatCurrencyIN(reportData.totalExpenses)}
              </div>
            </div>
            
            <div className="summary-item balance">
              <div className="summary-item-label">Remaining Balance</div>
              <div className="summary-item-value">
                {formatCurrencyIN(remaining)}
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="bar-chart-container card">
        <h3>Monthly Income vs Expenses</h3>
        <div style={{ height: "400px", width: "100%" }}>
          <Bar data={barChartData} options={barChartOptions} />
        </div>
      </div>

      <div className="top-categories card">
        <h3>Top Spending Categories</h3>
        <ul className="categories-list">
          {topCategories.map((cat, idx) => (
            <li key={idx} className="category-item">
              <div>
                <div className="category-name">{cat.category}</div>
                <div className="progress-container">
                  <div 
                    className="progress-bar" 
                    style={{ 
                      width: `${(cat.amount / maxCategoryValue) * 100}%` 
                    }}
                  ></div>
                </div>
              </div>
              <div className="category-amount">
                {cat.amount !== undefined && cat.amount !== null
                  ? formatCurrencyIN(cat.amount)
                  : "N/A"}
              </div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default IncomeVsExpenses;