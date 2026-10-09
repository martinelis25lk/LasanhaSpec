import axios from "axios";
import { API_URL } from "./api";

type LoginRequest = {
  email: string;
  password: string;
};

type RegisterRequest = {
  username: string;
  email: string;
  password: string;
};

export const login = async (data: LoginRequest) => {
  const response = await axios.post(`${API_URL}/auth/login`, data, { timeout: 10000 });
  return response.data;
};

export const register = async (data: RegisterRequest) => {
  const response = await axios.post(`${API_URL}/auth/register`, data, { timeout: 10000 });
  return response.data;
};