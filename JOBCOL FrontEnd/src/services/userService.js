import api from './api';

export const updateUserPhoto = async (userId, formData) => {
    const response = await api.put(
        `/users/${userId}/photo`,
        formData,
        {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
        }
    );

    return response.data;
};