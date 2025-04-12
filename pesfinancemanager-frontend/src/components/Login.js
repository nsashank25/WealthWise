import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "../styles/styles.css";

const Login = ({ switchToSignup, onLoginSuccess }) => {
  const [credentials, setCredentials] = useState({
    username: "",
    password: "",
  });
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
    // Clear any error when user starts typing again
    if (error) setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError("");
    
    try {
      const response = await axios.post(
        "http://localhost:8080/api/users/login",
        credentials,
        {
          withCredentials: true,
        }
      );
      console.log("Login successful:", response.data);
      localStorage.setItem("isLoggedIn", "true");
      localStorage.setItem("userId", response.data.userId);
      // update parent component state
      onLoginSuccess();
      navigate("/home");
    } catch (error) {
      console.error("Login failed", error);
      setError("Invalid credentials. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="auth-container">
        <div className="brand">
          <div className="brand-icon">💰</div>
          <h2>Login to Your Account</h2>
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <div className="input-group">
              <input
                type="text"
                id="username"
                name="username"
                placeholder="Enter your username"
                onChange={handleChange}
                required
              />
            </div>
          </div>
          
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <div className="input-group">
              <input
                type="password"
                id="password"
                name="password"
                placeholder="Enter your password"
                onChange={handleChange}
                required
              />
            </div>
          </div>
          
          <div className="form-options">
            <a href="#" className="forgot-password">Forgot password?</a>
          </div>
          
          {error && <div className="error-message">{error}</div>}
          
          <button type="submit" disabled={isLoading}>
            {isLoading ? "Logging in..." : "Login"}
          </button>
        </form>
        
        <p className="switch-link">
          Don't have an account?
          <button onClick={switchToSignup} className="link-button">
            Sign Up
          </button>
        </p>
      </div>
    </div>
  );
};

export default Login;