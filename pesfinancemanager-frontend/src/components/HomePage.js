import React, { useState, useEffect } from "react";
import "../styles/homePage.css";
import Navbar from "./Navbar";

const HomePage = ({ onLogout }) => {
  // Mock data for dashboard widgets
  const [currentTime, setCurrentTime] = useState(new Date());
  const [financialTips] = useState([
    '"A budget is telling your money where to go instead of wondering where it went." - Dave Ramsey',
    '"Do not save what is left after spending, but spend what is left after saving." - Warren Buffett',
    '"The stock market is a device for transferring money from the impatient to the patient." - Warren Buffett',
    '"Never depend on a single income. Make an investment to create a second source." - Warren Buffett',
    '"Money is a terrible master but an excellent servant." - P.T. Barnum'
  ]);  
  
  const [currentTip, setCurrentTip] = useState(0);
  
  // Update time every minute
  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 60000);
    
    return () => clearInterval(timer);
  }, []);
  
  // Rotate financial tips
  useEffect(() => {
    const tipInterval = setInterval(() => {
      setCurrentTip(prev => (prev + 1) % financialTips.length);
    }, 10000);
    
    return () => clearInterval(tipInterval);
  }, [financialTips.length]);

  return (
    <div className="home-container">
      <Navbar onLogout={onLogout} />
      
      <div className="dashboard-welcome">
        <h1>Welcome to Your Financial Dashboard</h1>
        <p className="welcome-subtitle">Manage your finances with ease and security</p>
        <p className="current-time">{currentTime.toLocaleString()}</p>
      </div>
      
      <div className="dashboard-grid">
        <div className="dashboard-card financial-tip-card">
          <h2>💡 Financial Tip</h2>
          <div className="tip-content">
            <p>{financialTips[currentTip]}</p>
          </div>
          <div className="tip-navigation">
            <span className="tip-counter">{currentTip + 1}/{financialTips.length}</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HomePage;