import api from "./api";

export const getAllOffers = async () => {
    const token = localStorage.getItem("token");

    const response = await api.get("/offers", {
        headers: {
        Authorization: `Bearer ${token}`
        }
    });

    return response.data;
};

export const getMyOffers = async (userId) => {
    const token = localStorage.getItem("token");

    const response = await api.get(`/offers/employer/${userId}`, {
        headers: {
        Authorization: `Bearer ${token}`
        }
    });

    return response.data;
};

export const createOffer = async (offerData) => {
    const token = localStorage.getItem("token");
    const response = await api.post('/offers', offerData, {
        headers: {
        Authorization: `Bearer ${token}`
        }
    });
    return response.data;
};

export const updateOffer = async (id, offerData) => {
    const token = localStorage.getItem("token");
    const response = await api.put(`/offers/${id}`, offerData, {
        headers: {
        Authorization: `Bearer ${token}`
        }
    });
    return response.data;
};

export const deleteOffer = async (id) => {
    const token = localStorage.getItem("token");
    const response = await api.delete(`/offers/${id}`, {
        headers: {
        Authorization: `Bearer ${token}`
        }
    });
    return response.data;
};

export const closeOffer = async (id) => {
    const token = localStorage.getItem("token");
    const response = await api.patch(`/offers/${id}/close`, {}, {
        headers: {
        Authorization: `Bearer ${token}`
        }
    });
    return response.data;
};