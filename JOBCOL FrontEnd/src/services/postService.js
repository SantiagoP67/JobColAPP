import api from './api';

export const getAllPosts = async () => {

  const response = await api.get('/posts');

  return response.data;
};

export const createPost = async (
  userId,
  description,
  files = []
) => {

  const formData = new FormData();

  formData.append('userId', userId);
  formData.append('description', description);

  files.forEach(file => {
    formData.append('files', file);
  });

  const response = await api.post(
    '/posts',
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }
  );

  return response.data;
};

export const likePost = async (postId, userId) => {

  const response = await api.post(
    `/posts/${postId}/like?userId=${userId}`
  );

  return response.data;
};

export const commentPost = async (
  postId,
  userId,
  content
) => {

  const response = await api.post(
    `/posts/${postId}/comment?userId=${userId}&content=${encodeURIComponent(content)}`
  );

  return response.data;
};