import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/financialProfileForm.css';

const FinancialProfileForm = () => {
  const userId = localStorage.getItem('userId');
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    monthlyIncome: 0,
    monthlyExpenses: 0,
    savingsAmount: 0,
    riskTolerance: 'MEDIUM',
    investmentTimeframeMonths: 12
  });
  
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/investments/profile/${userId}`, {
          withCredentials: true
        });
        if (response.data) {
          setFormData({
            monthlyIncome: response.data.monthlyIncome,
            monthlyExpenses: response.data.monthlyExpenses,
            savingsAmount: response.data.savingsAmount,
            riskTolerance: response.data.riskTolerance,
            investmentTimeframeMonths: response.data.investmentTimeframeMonths
          });
        }
        setLoading(false);
      } catch (error) {
        console.error("Error fetching profile:", error);
        setLoading(false);
      }
    };
    fetchProfile();
  }, [userId]);
  
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: name === 'riskTolerance' ? value : parseFloat(value)
    });
  };
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post(`http://localhost:8080/api/investments/profile/${userId}`, formData, {
        withCredentials: true
      });
      navigate('/investment-suggestions');
    } catch (error) {
      console.error("Error saving profile:", error);
      alert("Failed to save your financial profile. Please try again.");
    }
  };
  
  if (loading) {
    return <div className="loading">Loading your profile...</div>;
  }
  
  return (
    <div className="finance-container">
      <h2>Your Financial Profile</h2>
      <p>This information helps us provide personalized investment recommendations.</p>
      
      <form onSubmit={handleSubmit}>
        {[
          { label: 'Monthly Income', name: 'monthlyIncome' },
          { label: 'Monthly Expenses', name: 'monthlyExpenses' },
          { label: 'Current Savings', name: 'savingsAmount' },
          { label: 'Investment Timeframe (Months)', name: 'investmentTimeframeMonths' }
        ].map((field) => (
          <div key={field.name} className="form-group">
            <label>{field.label}</label>
            <input
              type="number"
              name={field.name}
              value={formData[field.name]}
              onChange={handleChange}
              min="0"
              step="0.01"
              required
            />
          </div>
        ))}
        
        <div className="form-group">
          <label>Risk Tolerance</label>
          <select
            name="riskTolerance"
            value={formData.riskTolerance}
            onChange={handleChange}
            required
          >
            <option value="LOW">Low - Safety First</option>
            <option value="MEDIUM">Medium - Balanced Approach</option>
            <option value="HIGH">High - Growth Focused</option>
          </select>
        </div>
        
        <button type="submit" className="button primary">Save Profile</button>
      </form>
    </div>
  );
};

export default FinancialProfileForm;