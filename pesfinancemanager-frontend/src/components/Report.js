import { useState, useEffect } from "react";
import axios from "axios";
import "../styles/reports.css";
import { Bar } from "react-chartjs-2";

const Reports = () => {
  const userId = 1; // Dynamic in actual implementation
  const [report, setReport] = useState(null);

  useEffect(() => {
    const fetchReport = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/reports/${userId}/monthly`, {
          params: { year: 2024, month: 3 }, // Change as needed
        });
        setReport(response.data);
      } catch (error) {
        console.error("Error fetching report", error);
      }
    };

    fetchReport();
  }, [userId]);

  return (
    <div className="reports-container">
      <div className="reports-card">
        <h2>Income vs. Expenses Report</h2>
        {report ? (
          <div>
            <p><strong>Total Income:</strong> ${report.income}</p>
            <p><strong>Total Expenses:</strong> ${report.totalExpenses}</p>
            <p className={`savings ${report.savings < 0 ? "negative" : "positive"}`}>
              <strong>Total Savings:</strong> ${report.savings}
            </p>
            <h3>Category-wise Expenses</h3>
            <Bar
              data={{
                labels: Object.keys(report.categoryWiseExpenses),
                datasets: [
                  {
                    label: "Expenses by Category",
                    data: Object.values(report.categoryWiseExpenses),
                    backgroundColor: "rgba(255, 99, 132, 0.5)",
                  },
                ],
              }}
            />
          </div>
        ) : (
          <p>Loading report...</p>
        )}
      </div>
    </div>
  );
};

export default Reports;
