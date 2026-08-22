import axios from "axios";

const api = axios.create({
// Trocando o localhost pelo seu domínio de produção + a rota /api
baseURL: "https://lasanhaspecs.duckdns.org/api",
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export default api;