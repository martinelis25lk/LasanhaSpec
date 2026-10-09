import axios from "axios";

// Única fonte da URL do backend. Defina VITE_API_URL no .env
export const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

const api = axios.create({
  baseURL: API_URL,
  timeout: 10000, // falha em 10s em vez de ficar carregando pra sempre
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export default api;