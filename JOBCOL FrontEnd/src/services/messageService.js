import api from './api';

export const getWhatsAppLink = async (receiverId, message) => {

  const response = await api.get(
    `/messages/whatsapp-link/${receiverId}`,
    {
      params: { message }
    }
  );

  return response.data;
};