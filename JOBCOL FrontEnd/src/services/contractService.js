import api from "./api";


export const getContractsByUser = async (userId) => {
    const response = await api.get(`/contracts/user/${userId}`);
    return response.data;
};

export const finishContract = async (id) => {
    const response = await api.patch(`/contracts/${id}/finish`);
    return response.data;
};

export const createContract = async (data) => {

    const response = await api.post(
        '/contracts',
        data
    );

    return response.data;
};

export const acceptContract = async (id) => {

    const response = await api.put(
        `/contracts/${id}/accept`
    );

    return response.data;
};

export const rejectContract = async (id) => {

    const response = await api.put(
        `/contracts/${id}/reject`
    );

    return response.data;
};

export const requestFinishContract =
    async (contractId, userId) => {

    const response = await api.put(
        `/contracts/${contractId}/finish-request?userId=${userId}`
    );

    return response.data;
};

export const confirmFinishContract = async (
    contractId, userId) => {

    const response = await api.put(
        `/contracts/${contractId}/confirm-finish?userId=${userId}`
    );

    return response.data;
}
