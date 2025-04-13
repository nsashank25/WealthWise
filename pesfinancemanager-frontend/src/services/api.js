import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api";

export async function registerUser(user) {
    try {
        const response = await axios.post(`${API_BASE_URL}/users/register`, user);
        console.log(response.data.message);
    } catch (error) {
        console.error("Registration failed", error);
    }
}

export async function loginUser(username, password) {
    try {
        const response = await axios.post(`${API_BASE_URL}/users/login`, {
            username,
            password
        });
        console.log(response.data.message);
    } catch (error) {
        console.error("Login failed", error);
    }
}

const API_BASE_URL_EXPENSE = "http://localhost:8080/api/expenses";

export const getExpenses = (userId) => axios.get(`${API_BASE_URL_EXPENSE}/${userId}`);
export const addExpense = (userId, expense) => axios.post(`${API_BASE_URL_EXPENSE}/${userId}/add`, expense);

// Set up a function to get authentication headers (if needed)
const getAuthHeaders = () => {
    const token = localStorage.getItem("token"); // Assuming token is stored in localStorage
    return token ? { Authorization: `Bearer ${token}` } : {};
  };

export const fetchIncomeVsExpensesReport = async (userId) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/reports/${userId}`, {
        headers: getAuthHeaders(),
      });
      return response.data;
    } catch (error) {
      console.error("Error fetching income vs expenses report:", error);
      throw error;
    }
  };

export const undoLastExpenseOperation = async () => {
  return await axios.post('http://localhost:8080/api/expenses/undo');
};

export const redoLastExpenseOperation = async () => {
  return await axios.post('http://localhost:8080/api/expenses/redo');
};