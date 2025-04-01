import React from "react";
import "../styles/homePage.css"; 
import Navbar from "./Navbar";

const HomePage = ({ onLogout }) => {
  return (
    <div className="home-container">
      {/* Navbar at the top */}
      <Navbar onLogout={onLogout} />

      <div className="home-card">
        <h1>Welcome to Your Dashboard</h1>
        <p>Manage your finance with ease and security.</p>
        <div className="financial-tip">
          <h3>💡 Financial Tip</h3>
          <br></br>
          <p>"A budget is telling your money where to go instead of wondering where it went." - Dave Ramsey</p>
        </div>
      </div>
    </div>
  );
};

export default HomePage;