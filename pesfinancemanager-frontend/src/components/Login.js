import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "../styles/styles.css";

const Login = ({ switchToSignup, onLoginSuccess }) => {
  const [credentials, setCredentials] = useState({
    username: "",
    password: "",
  });

  const navigate = useNavigate();

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
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
      alert("Invalid credentials. Please try again.");
    }
  };

  return (
    <div className="container">
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="username"
          placeholder="Username"
          onChange={handleChange}
          required
        />
        <input
          type="password"
          name="password"
          placeholder="Password"
          onChange={handleChange}
          required
        />
        <button type="submit">Login</button>
      </form>
      <p className="switch-link">
        Don't have an account?
        <button onClick={switchToSignup} className="link-button">
          Sign Up
        </button>
      </p>
    </div>
  );
};

export default Login;