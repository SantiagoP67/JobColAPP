import api from "./api";
import { getCurrentUser, getUserFromToken } from "./authService";

export const getPostulationsByWorker = async () => {
  const token = localStorage.getItem("token");

    
    const user = await getCurrentUser();

    const response = await api.get(`/postulations/worker/${user.id}`, {
        headers: {
        Authorization: `Bearer ${token}`
        }
    });

    return response.data;
};


export const createPostulation = async (
    offerId,
    workerId,
    calification = 0
    ) => {

    const payload = {

        id: null,

        status: "PENDING",

        applicationDate: null,

        workerId: workerId,

        calification: calification,

        offer: {
        id: offerId
        },

        contractId: null
    };

    const response = await api.post(
        "/postulations",
        payload
    );

    return response.data;
};

export const getPostulationsByEmployer = async (userId) => {
    const token = localStorage.getItem("token");

    const response = await api.get(
        `/postulations/employer/${userId}`,
        {
            headers: {
                Authorization: `Bearer ${token}`
            }
        }
    );

    return response.data;
};

export const updatePostulationStatus = async (postulationId, status) => {
    const token = localStorage.getItem("token");

    const response = await api.patch(
        `/postulations/${postulationId}/status?status=${status}`,
        {},
        {
        headers: {
            Authorization: `Bearer ${token}`
        }
        }
    );

    return response.data;
};