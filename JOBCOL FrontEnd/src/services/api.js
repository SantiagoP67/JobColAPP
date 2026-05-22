import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  console.log("📡 TOKEN ENVIADO:", token);

  const publicRoutes = [
    "/auth/login",
    "/auth/register",
    "/auth/verify-code",
    "/auth/forgot-password",
    "/auth/reset-password"
  ];

  const isPublic = publicRoutes.some(route =>
    config.url.includes(route)
  );

  if (token && token !== "undefined" && !isPublic) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export default api; 