import { useState } from "react";
import axios from "axios";
import "../styles/styles.css";

const Signup = ({ switchToLogin }) => {
  const [user, setUser] = useState({ 
    username: "", 
    email: "", 
    password: "",
    income: "" 
});


  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
};

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const userData = { ...user, income: parseFloat(user.income) };
      console.log("Sending data:", user);
      const response = await axios.post(
        "http://localhost:8080/api/users/register",
        userData
      );
      console.log("Response received:", response.data);
      alert(response.data.message);
    } catch (error) {
      console.error("Signup failed", error);
    }
  };

  return (
    <div className="container">
      <h2>Sign Up</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="username"
          placeholder="Username"
          onChange={handleChange}
          required
        />
        <input
          type="email"
          name="email"
          placeholder="Email"
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
        <input
          type="number"
          name="income"
          placeholder="Income"
          onChange={handleChange}
          required
        />
        <button type="submit">Sign Up</button>
      </form>
      <p className="switch-link">
        Already have an account?
        <button onClick={switchToLogin} className="link-button">
          Login
        </button>
      </p>
    </div>
  );
};

export default Signup;
