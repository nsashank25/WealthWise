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
      </div>
    </div>
  );
};

export default HomePage;
