// import { useState } from "react";
// import axios from "axios";
// import "../styles/styles.css";

// const Signup = ({ switchToLogin }) => {
//   const [user, setUser] = useState({ 
//     username: "", 
//     email: "", 
//     password: "",
//     income: "" 
// });


//   const handleChange = (e) => {
//     setUser({ ...user, [e.target.name]: e.target.value });
// };

//   const handleSubmit = async (e) => {
//     e.preventDefault();
//     try {
//       const userData = { ...user, income: parseFloat(user.income) };
//       console.log("Sending data:", user);
//       const response = await axios.post(
//         "http://localhost:8080/api/users/register",
//         userData
//       );
//       console.log("Response received:", response.data);
//       alert(response.data.message);
//     } catch (error) {
//       console.error("Signup failed", error);
//     }
//   };

//   return (
//     <div className="container">
//       <h2>Sign Up</h2>
//       <form onSubmit={handleSubmit}>
//         <input
//           type="text"
//           name="username"
//           placeholder="Username"
//           onChange={handleChange}
//           required
//         />
//         <input
//           type="email"
//           name="email"
//           placeholder="Email"
//           onChange={handleChange}
//           required
//         />
//         <input
//           type="password"
//           name="password"
//           placeholder="Password"
//           onChange={handleChange}
//           required
//         />
//         <input
//           type="number"
//           name="income"
//           placeholder="Income"
//           onChange={handleChange}
//           required
//         />
//         <button type="submit">Sign Up</button>
//       </form>
//       <p className="switch-link">
//         Already have an account?
//         <button onClick={switchToLogin} className="link-button">
//           Login
//         </button>
//       </p>
//     </div>
//   );
// };

// export default Signup;

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
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
    // Clear errors when user starts typing again
    if (error) setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError("");
    setSuccess("");
    
    try {
      const userData = { ...user, income: parseFloat(user.income) };
      console.log("Sending data:", userData);
      
      const response = await axios.post(
        "http://localhost:8080/api/users/register",
        userData
      );
      
      console.log("Response received:", response.data);
      setSuccess(response.data.message || "Account created successfully!");
      
      // Clear form or prepare for login
      setTimeout(() => {
        switchToLogin();
      }, 2000);
      
    } catch (error) {
      console.error("Signup failed", error);
      setError(error.response?.data?.message || "Signup failed. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="auth-container">
        <div className="brand">
          <div className="brand-icon">💰</div>
          <h2>Create Your Account</h2>
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <div className="input-group">
              <input
                type="text"
                id="username"
                name="username"
                placeholder="Choose a username"
                onChange={handleChange}
                required
              />
            </div>
          </div>
          
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <div className="input-group">
              <input
                type="email"
                id="email"
                name="email"
                placeholder="Enter your email"
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
                placeholder="Create a password"
                onChange={handleChange}
                required
              />
            </div>
          </div>
          
          <div className="form-group">
            <label htmlFor="income">Monthly Income</label>
            <div className="input-group">
              <input
                type="number"
                id="income"
                name="income"
                placeholder="Enter your monthly income"
                onChange={handleChange}
                required
              />
            </div>
          </div>
          
          {error && <div className="error-message">{error}</div>}
          {success && <div style={{ color: "#00b894", marginTop: "10px" }}>{success}</div>}
          
          <button type="submit" disabled={isLoading}>
            {isLoading ? "Creating Account..." : "Sign Up"}
          </button>
        </form>
        
        <p className="switch-link">
          Already have an account?
          <button onClick={switchToLogin} className="link-button">
            Login
          </button>
        </p>
      </div>
    </div>
  );
};

export default Signup;