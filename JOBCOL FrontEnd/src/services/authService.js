import api from "./api";

export const register = async (data) => {
    const response = await api.post("/auth/register", data);

    console.log("REGISTER RESPONSE:", response.data);

    const token = response.data.accessToken;

    console.log("TOKEN:", token);

    if (token) {
        localStorage.setItem("token", token);
        console.log("TOKEN GUARDADO");
    } else {
        console.warn("NO HAY ACCESS TOKEN EN RESPUESTA");
    }

    return response.data;
};

export const login = async ({ username, password }) => {
    const response = await api.post("/auth/login", {
        username,
        password
    });

    const data = response.data;

    const token = data?.accessToken || data?.token;

    if (token) {
        localStorage.setItem("token", token);
    }

    if (data?.email) {
        localStorage.setItem("email", data.email);
    }

    return data;
};

export const verifyCode = async (code) => {
    const email = localStorage.getItem("email");

    if (!email) {
        throw new Error("No hay email guardado en sesión");
    }

    const response = await api.post("/auth/verify-code", {
        email,
        code
    });

    return response.data;
};

export function getUserFromToken() {
    const token = localStorage.getItem("token");
    if (!token) return null;

    const payload = JSON.parse(atob(token.split('.')[1]));

    return {
        email: payload.email,
        username: payload.preferred_username,
        roles: payload.realm_access?.roles || []
    };
}

export function getAppRole(user) {
    if (!user) return null;

    if (user.roles.includes("ADMIN")) return "ADMIN";
    if (user.roles.includes("EMPLEADOR")) return "EMPLEADOR";
    if (user.roles.includes("TRABAJADOR")) return "TRABAJADOR";

    return "TRABAJADOR"; 
}

export const getCurrentUser = async () => {
    const token = localStorage.getItem("token");

    const response = await api.get("/auth/me", {
        headers: {
        Authorization: `Bearer ${token}`
        }
    });

    return response.data;
};

export const getUserById = async (id) => {

    const token = localStorage.getItem("token");

    const response = await api.get(`/users/${id}`, {
        headers: {
        Authorization: `Bearer ${token}`
        }
    });
    return response.data;
};

export const forgotPassword = async (email) => {
    const response = await api.post("/auth/forgot-password", { email });
    return response.data;
};

export const resetPassword = async (email, code, newPassword) => {
    const response = await api.post("/auth/reset-password", { email, code, newPassword });
    return response.data;
};