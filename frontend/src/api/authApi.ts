import axios from "axios";

// Trocamos o localhost pela URL oficial da sua API na AWS
const API_URL = "https://lasanhaspecs.duckdns.org/api";

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
const response = await axios.post(`${API_URL}/auth/login`, data);
return response.data;
};

export const register = async (data: RegisterRequest) => {
const response = await axios.post(`${API_URL}/auth/register`, data);
return response.data;
};