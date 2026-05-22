import api from "./api";

export const createEmployerProfile = async (data) => {
    const token = localStorage.getItem("token");

    return api.post("/employer-profiles", data, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
};

export const getEmployerProfile = async (userId) => {
    const token = localStorage.getItem("token");

    const response = await api.get(`/employer-profiles/${userId}`, {
        headers: {
        Authorization: `Bearer ${token}`
        }
    });

    return response.data;
};