// // import React, { useState, useEffect } from "react";
// // import axios from "axios";
// // import { Pie } from "react-chartjs-2";
// // import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
// // import "../styles/incomeVsExpenses.css";

// // ChartJS.register(ArcElement, Tooltip, Legend);

// // const IncomeVsExpenses = () => {
// //   const [reportData, setReportData] = useState(null);
// //   const [loading, setLoading] = useState(true);
// //   const [error, setError] = useState(null);
// //   const userId = localStorage.getItem("userId");

// //   useEffect(() => {
// //     const fetchReport = async () => {
// //       try {
// //         const response = await axios.get(
// //           `http://localhost:8080/api/reports/${userId}`
// //         );
// //         setReportData(response.data);
// //         setLoading(false);
// //       } catch (error) {
// //         console.error("Error fetching financial report:", error);
// //         setError("Failed to load report data.");
// //         setLoading(false);
// //       }
// //     };

// //     if (userId) {
// //       fetchReport();
// //     } else {
// //       setError("User not logged in.");
// //       setLoading(false);
// //     }
// //   }, [userId]);

// //   const downloadReport = async () => {
// //     try {
// //       const response = await axios.get(
// //         `http://localhost:8080/api/reports/${userId}/download`,
// //         { responseType: "blob" }
// //       );

// //       const blob = new Blob([response.data], { type: "application/pdf" });
// //       const link = document.createElement("a");
// //       link.href = window.URL.createObjectURL(blob);
// //       link.download = "Financial_Report.pdf";
// //       document.body.appendChild(link);
// //       link.click();
// //       document.body.removeChild(link);
// //     } catch (error) {
// //       console.error("Error downloading the financial report:", error);
// //     }
// //   };

// //   if (loading) return <p>Loading...</p>;
// //   if (error) return <p>{error}</p>;

// //   const spent = reportData.totalExpenses;
// //   const remaining = Math.max(
// //     0,
// //     reportData.totalIncome - reportData.totalExpenses
// //   );

// //   const chartData = {
// //     labels: ["Spent", "Remaining"],
// //     datasets: [
// //       {
// //         data: [spent, remaining],
// //         backgroundColor: ["#468189", "#77ACA2"], // light grey and slate
// //       },
// //     ],
// //   };

// //   return (
// //     <>
// //       <div className="income-expenses-container">
// //         <div className="income-expenses-card">
// //           <h2>Income vs. Expenses Report</h2>
// //           <div className="chart-container">
// //             <Pie data={chartData} />
// //           </div>
// //           <button className="download-btn" onClick={downloadReport}>
// //             Download PDF Report
// //           </button>
// //         </div>
// //       </div>
// //       <div className="summary">
// //         <p>
// //           <strong>Total Income:</strong> ₹{reportData.totalIncome.toFixed(2)}
// //         </p>
// //         <p>
// //           <strong>Total Expenses:</strong> ₹
// //           {reportData.totalExpenses.toFixed(2)}
// //         </p>
// //         <p>
// //           <strong>Remaining Balance:</strong> ₹{remaining.toFixed(2)}
// //         </p>
// //       </div>
// //     </>
// //   );
// // };

// // export default IncomeVsExpenses;

// import React, { useState, useEffect } from "react";
// import axios from "axios";
// import { Pie, Bar } from "react-chartjs-2";
// import {
//   Chart as ChartJS,
//   ArcElement,
//   BarElement,
//   CategoryScale,
//   LinearScale,
//   Tooltip,
//   Legend,
// } from "chart.js";
// import "../styles/incomeVsExpenses.css";

// ChartJS.register(
//   ArcElement,
//   BarElement,
//   CategoryScale,
//   LinearScale,
//   Tooltip,
//   Legend
// );

// const IncomeVsExpenses = () => {
//   const [reportData, setReportData] = useState(null);
//   const [monthlyTrend, setMonthlyTrend] = useState([]);
//   const [topCategories, setTopCategories] = useState([]);
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);
//   const userId = localStorage.getItem("userId");

//   useEffect(() => {
//     const fetchReport = async () => {
//       try {
//         const [reportRes, trendRes, categoriesRes] = await Promise.all([
//           axios.get(`http://localhost:8080/api/reports/${userId}`),
//           axios.get(
//             `http://localhost:8080/api/reports/monthly-trend/${userId}`
//           ),
//           axios.get(
//             `http://localhost:8080/api/reports/top-categories/${userId}`
//           ),
//         ]);

//         setReportData(reportRes.data);
//         setMonthlyTrend(trendRes.data);
//         setTopCategories(categoriesRes.data);
//         setLoading(false);
//       } catch (error) {
//         console.error("Error fetching financial report:", error);
//         setError("Failed to load report data.");
//         setLoading(false);
//       }
//     };

//     if (userId) {
//       fetchReport();
//     } else {
//       setError("User not logged in.");
//       setLoading(false);
//     }
//   }, [userId]);

//   const downloadReport = async () => {
//     try {
//       const response = await axios.get(
//         `http://localhost:8080/api/reports/${userId}/download`,
//         { responseType: "blob" }
//       );

//       const blob = new Blob([response.data], { type: "application/pdf" });
//       const link = document.createElement("a");
//       link.href = window.URL.createObjectURL(blob);
//       link.download = "Financial_Report.pdf";
//       document.body.appendChild(link);
//       link.click();
//       document.body.removeChild(link);
//     } catch (error) {
//       console.error("Error downloading the financial report:", error);
//     }
//   };

//   if (loading) return <p>Loading...</p>;
//   if (error) return <p>{error}</p>;

//   const spent = reportData.totalExpenses;
//   const remaining = Math.max(
//     0,
//     reportData.totalIncome - reportData.totalExpenses
//   );

//   const chartData = {
//     labels: ["Spent", "Remaining"],
//     datasets: [
//       {
//         data: [spent, remaining],
//         backgroundColor: ["#d63031", "#00b894"],
//       },
//     ],
//   };

//   const barChartData = {
//     labels: monthlyTrend.map((item) => item.month),
//     datasets: [
//       {
//         label: "Income",
//         backgroundColor: "#00b894",
//         data: monthlyTrend.map((item) => item.income / 12),
//       },
//       {
//         label: "Expenses",
//         backgroundColor: "#d63031",
//         data: monthlyTrend.map((item) => item.expenses),
//       },
//     ],
//   };

//   const barChartOptions = {
//     responsive: true,
//     plugins: {
//       legend: { position: "top" },
//     },
//   };

//   return (
//     <>
//       <div className="income-expenses-container">
//         <div className="income-expenses-card">
//           <h2>Income vs. Expenses Report</h2>
//           <div className="chart-container">
//             <Pie data={chartData} />
//           </div>
//           <button className="download-btn" onClick={downloadReport}>
//             Download PDF Report
//           </button>
//         </div>
//       </div>

//       <div className="summary">
//         <p>
//           <strong>Annual Income:</strong> ₹{reportData.totalIncome.toFixed(2)}
//         </p>
//         <p>
//           <strong>Total Expenses:</strong> ₹
//           {reportData.totalExpenses.toFixed(2)}
//         </p>
//         <p>
//           <strong>Remaining Balance:</strong> ₹{remaining.toFixed(2)}
//         </p>
//       </div>

//       <div className="bar-chart-container">
//         <h3>Monthly Income vs Expenses</h3>
//         <Bar data={barChartData} options={barChartOptions} />
//       </div>

//       <div className="top-categories">
//         <h3>Top Spending Categories</h3>
//         <ul>
//           {topCategories.map((cat, idx) => (
//             <li key={idx}>
//               <strong>{cat.category}:</strong> ₹
//               {cat.amount !== undefined && cat.amount !== null
//                 ? cat.amount.toFixed(2)
//                 : "N/A"}
//             </li>
//           ))}
//         </ul>
//       </div>
//     </>
//   );
// };

// export default IncomeVsExpenses;

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

  if (loading) return <p>Loading...</p>;
  if (error) return <p>{error}</p>;

  const spent = reportData.totalExpenses;
  const remaining = Math.max(0, reportData.totalIncome - spent);

  const chartData = {
    labels: ["Spent", "Remaining"],
    datasets: [
      {
        data: [spent, remaining],
        backgroundColor: ["#d63031", "#00b894"],
      },
    ],
  };

  const barChartData = {
    labels: monthlyTrend.map((item) => item.month),
    datasets: [
      {
        label: "Monthly Income",
        backgroundColor: "#00b894",
        data: monthlyTrend.map((item) => item.income / 12),
      },
      {
        label: "Monthly Expenses",
        backgroundColor: "#d63031",
        data: monthlyTrend.map((item) => item.expenses),
      },
    ],
  };

  const barChartOptions = {
    responsive: true,
    plugins: {
      legend: { position: "top" },
    },
  };

  return (
    <>
      <div className="income-expenses-container">
        <div className="income-expenses-card">
          <h2>Income vs. Expenses Report</h2>
          <div className="chart-container">
            <Pie data={chartData} />
          </div>
          <button className="download-btn" onClick={downloadReport}>
            Download PDF Report
          </button>
        </div>
      </div>

      <div className="summary">
        <p>
          <strong>Annual Income:</strong> {formatCurrencyIN(reportData.totalIncome)}
        </p>
        <p>
          <strong>Total Expenses:</strong> {formatCurrencyIN(reportData.totalExpenses)}
        </p>
        <p>
          <strong>Remaining Balance:</strong> {formatCurrencyIN(remaining)}
        </p>
      </div>

      <div className="bar-chart-container">
        <h3>Monthly Income vs Expenses</h3>
        <Bar data={barChartData} options={barChartOptions} />
      </div>

      <div className="top-categories">
        <h3>Top Spending Categories</h3>
        <ul>
          {topCategories.map((cat, idx) => (
            <li key={idx}>
              <strong>{cat.category}:</strong>{" "}
              {cat.amount !== undefined && cat.amount !== null
                ? formatCurrencyIN(cat.amount)
                : "N/A"}
            </li>
          ))}
        </ul>
      </div>
    </>
  );
};

export default IncomeVsExpenses;
