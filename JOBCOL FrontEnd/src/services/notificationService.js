import api from "./api";

export const getUserNotifications = async (userId) => {
    const response = await api.get(`/notifications/user/${userId}`);
    return response.data;
};

export const getUnreadNotifications = async (userId) => {
    const response = await api.get(`/notifications/user/${userId}/unread`);
    return response.data;
};

export const countUnread = async (userId) => {
    const response = await api.get(`/notifications/user/${userId}/count-unread`);
    return response.data;
};

export const markAsRead = async (notificationId) => {
    const response = await api.put(`/notifications/${notificationId}/read`);
    return response.data;
};

export const markAllAsRead = async (userId) => {
    const response = await api.put(`/notifications/user/${userId}/read-all`);
    return response.data;
};

export const createNotification = async (userId, title, message, type) => {
    const response = await api.post(
        `/notifications/create?userId=${userId}&title=${encodeURIComponent(title)}&message=${encodeURIComponent(message)}&type=${type}`
    );
    return response.data;
};
