import api from "./api";

export const createProfile = async (data) => {
    const token = localStorage.getItem("token");

    const response = await api.post("/profiles", data, {
        headers: {
        Authorization: `Bearer ${token}`
        }
    });

    return response.data;
};


export const getWorkerProfile = async (userId) => {
    const token = localStorage.getItem("token");

    const response = await api.get(`/profiles/user/${userId}`, {
        headers: {
        Authorization: `Bearer ${token}`
        }
    });

    return response.data;
};