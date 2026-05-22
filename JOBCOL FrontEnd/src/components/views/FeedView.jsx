import React, { useState, useEffect } from 'react';
import {
  MoreVertical,
  TrendingUp,
  Users,
  Heart,
  MessageCircle,
  Share2,
  Send
} from 'lucide-react';

import { useToast } from '../../context/ToastContext';
import {
  getAllPosts,
  createPost,
  likePost
} from '../../services/postService';

import { getCurrentUser } from '../../services/authService';

import './FeedView.css';

const tags = [
  '#Plomería',
  '#Aseo',
  '#Pintura',
  '#Electricidad',
  '#Jardinería',
  '#Carpintería',
  '#Domicilios',
  '#Construcción'
];

const popularJobs = [
  { name: 'Plomería', count: 127 },
  { name: 'Aseo', count: 98 },
  { name: 'Electricidad', count: 85 },
  { name: 'Pintura', count: 76 },
  { name: 'Jardinería', count: 64 },
];

const FALLBACK_POSTS = [
  {
    id: 'fb-1',
    description: '¡Instalación de lavamanos completada!',
    username: 'Carlos Rodríguez',
    createdAt: new Date(Date.now() - 7200000).toISOString(),
    mediaUrls: [
      'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&q=80&w=800'
    ],
    likes: 24
  }
];

function PostActions({ post, userId }) {

  const [likes, setLikes] = useState(post.likes || 0);
  const [isLiked, setIsLiked] = useState(false);

  const { showToast } = useToast();

  const handleLike = async () => {

    setIsLiked(!isLiked);

    setLikes(prev => isLiked ? prev - 1 : prev + 1);

    try {

      if (
        userId &&
        post.id &&
        !String(post.id).startsWith('fb-')
      ) {
        await likePost(post.id, userId);
      }

    } catch (err) {

      console.error("Error al dar like", err);
    }
  };

  const handleShare = () => {

    showToast('Enlace copiado al portapapeles', 'info');
  };

  return (
    <div
      className="post-actions"
      style={{
        display: 'flex',
        gap: '1.5rem',
        marginTop: '1rem',
        paddingTop: '1rem',
        borderTop: '1px solid var(--border-color)'
      }}
    >
      <button
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.5rem',
          background: 'none',
          border: 'none',
          cursor: 'pointer',
          color: isLiked ? '#ef4444' : 'var(--text-muted)'
        }}
        onClick={handleLike}
      >
        <Heart
          size={20}
          fill={isLiked ? "currentColor" : "none"}
        />

        <span>{likes}</span>
      </button>

      <button
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.5rem',
          background: 'none',
          border: 'none',
          cursor: 'pointer',
          color: 'var(--text-muted)'
        }}
      >
        <MessageCircle size={20} />
        <span>0</span>
      </button>

      <button
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.5rem',
          background: 'none',
          border: 'none',
          cursor: 'pointer',
          color: 'var(--text-muted)'
        }}
        onClick={handleShare}
      >
        <Share2 size={20} />
        <span>Compartir</span>
      </button>
    </div>
  );
}

export default function FeedView() {

  const { showToast } = useToast();

  const [posts, setPosts] = useState([]);
  const [user, setUser] = useState(null);

  const [newPostText, setNewPostText] = useState('');

  const [posting, setPosting] = useState(false);

  const [selectedFiles, setSelectedFiles] = useState([]);

  const [previewUrls, setPreviewUrls] = useState([]);

  useEffect(() => {

    loadUser();
    loadPosts();

  }, []);

  const loadUser = async () => {

    try {

      const data = await getCurrentUser();

      setUser(data);

    } catch (err) {

      console.error("Error cargando usuario", err);
    }
  };

  const loadPosts = async () => {

    try {

      const data = await getAllPosts();

      if (data && data.length > 0) {

        const sorted = [...data].sort((a, b) => {

          if (!a.createdAt) return 1;
          if (!b.createdAt) return -1;

          return new Date(b.createdAt) - new Date(a.createdAt);
        });

        setPosts(sorted);

      } else {

        setPosts(FALLBACK_POSTS);
      }

    } catch (err) {

      console.error("Error cargando posts", err);

      setPosts(FALLBACK_POSTS);
    }
  };

  const handleFileChange = (e) => {

    const files = Array.from(e.target.files);

    setSelectedFiles(files);

    const previews = files.map(file =>
      URL.createObjectURL(file)
    );

    setPreviewUrls(previews);
  };

  const handleCreatePost = async () => {

    if (!newPostText.trim() || !user) return;

    setPosting(true);

    try {

      const newPost = await createPost(
        user.id,
        newPostText.trim(),
        selectedFiles
      );

      setPosts(prev => [newPost, ...prev]);

      setNewPostText('');

      setSelectedFiles([]);

      setPreviewUrls([]);

      showToast('Publicación creada', 'success');

    } catch (err) {

      console.error("Error creando post", err);

      showToast('Error al publicar', 'error');

    } finally {

      setPosting(false);
    }
  };

  const formatPostTime = (dateStr) => {

    if (!dateStr) return 'Reciente';

    const diff =
      (new Date() - new Date(dateStr)) / 1000;

    if (diff < 3600) {
      return 'Hace unos minutos';
    }

    if (diff < 86400) {
      return `Hace ${Math.floor(diff / 3600)} horas`;
    }

    return `Hace ${Math.floor(diff / 86400)} días`;
  };

  return (
    <div className="feed-view-wrapper">

      <div className="page-header">

        <h1 className="page-title">
          Publicaciones
        </h1>

        <p className="page-subtitle">
          Descubre trabajos y conecta con profesionales
        </p>
      </div>

      <div className="feed-layout">

        <div className="feed-main">

          <div className="feed-create-post card">

            <div
              style={{
                display: 'flex',
                gap: '0.75rem',
                alignItems: 'flex-start'
              }}
            >

              <div
                className="post-avatar"
                style={{
                  width: 40,
                  height: 40,
                  borderRadius: '50%',
                  background: 'linear-gradient(135deg, #7c3aed, #a855f7)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  color: 'white',
                  fontWeight: 700,
                  fontSize: '1rem',
                  flexShrink: 0
                }}
              >
                {user?.username?.charAt(0)?.toUpperCase() || 'U'}
              </div>

              <div style={{ flex: 1 }}>

                <textarea
                  placeholder="¿Qué quieres compartir con la comunidad?"
                  value={newPostText}
                  onChange={(e) => setNewPostText(e.target.value)}
                  style={{
                    width: '100%',
                    border: '1px solid var(--border-color, #e5e7eb)',
                    borderRadius: '10px',
                    padding: '0.75rem',
                    resize: 'none',
                    minHeight: '60px',
                    fontSize: '0.9rem',
                    fontFamily: 'inherit',
                    background: 'var(--bg-secondary, #f8f9fc)'
                  }}
                  rows={2}
                />

                <div style={{ marginTop: '0.75rem' }}>

                  <div className="file-upload-container">

                    <label className="custom-file-upload">

                      📷 Subir imágenes

                      <input
                        type="file"
                        multiple
                        accept="image/*"
                        onChange={handleFileChange}
                        className="hidden-file-input"
                      />
                    </label>

                    {selectedFiles.length > 0 && (
                      <p className="selected-files-text">
                        {selectedFiles.length} archivo(s) seleccionado(s)
                      </p>
                    )}
                  </div>

                  {previewUrls.length > 0 && (

                    <div
                      style={{
                        display: 'flex',
                        gap: '0.5rem',
                        marginTop: '0.75rem',
                        flexWrap: 'wrap'
                      }}
                    >

                      {previewUrls.map((url, index) => (

                        <img
                          key={index}
                          src={url}
                          alt="preview"
                          style={{
                            width: 90,
                            height: 90,
                            objectFit: 'cover',
                            borderRadius: '8px'
                          }}
                        />
                      ))}
                    </div>
                  )}
                </div>

                <div
                  style={{
                    display: 'flex',
                    justifyContent: 'flex-end',
                    marginTop: '0.5rem'
                  }}
                >

                  <button
                    onClick={handleCreatePost}
                    disabled={!newPostText.trim() || posting}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '0.4rem',
                      background: newPostText.trim()
                        ? 'linear-gradient(135deg, #7c3aed, #a855f7)'
                        : '#d1d5db',
                      color: 'white',
                      border: 'none',
                      borderRadius: '8px',
                      padding: '0.5rem 1rem',
                      cursor: newPostText.trim()
                        ? 'pointer'
                        : 'not-allowed',
                      fontWeight: 600,
                      fontSize: '0.85rem'
                    }}
                  >
                    <Send size={16} />

                    {posting
                      ? 'Publicando...'
                      : 'Publicar'}
                  </button>
                </div>
              </div>
            </div>

            <div
              className="feed-tags"
              style={{ marginTop: '0.75rem' }}
            >
              {tags.map(tag => (
                <span
                  key={tag}
                  className="feed-tag"
                >
                  {tag}
                </span>
              ))}
            </div>
          </div>

          <div className="feed-posts">

            {posts.map(post => (

              <div
                key={post.id}
                className="feed-post card"
              >

                <div className="post-header">

                  <div className="post-author">

                    <div
                      className="post-avatar"
                      style={{
                        width: 44,
                        height: 44,
                        borderRadius: '50%',
                        background: 'linear-gradient(135deg, #6366f1, #a855f7)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: 'white',
                        fontWeight: 700,
                        fontSize: '1.1rem'
                      }}
                    >
                      {(post.username || 'U')
                        .charAt(0)
                        .toUpperCase()}
                    </div>

                    <div>
                      <h3 className="author-name">
                        {post.username || 'Usuario'}
                      </h3>

                      <p className="post-time">
                        {formatPostTime(post.createdAt)}
                      </p>
                    </div>
                  </div>

                  <button className="icon-btn">
                    <MoreVertical size={20} />
                  </button>
                </div>

                <div className="post-content">

                  <p>{post.description}</p>

                  {post.mediaUrls &&
                    post.mediaUrls.length > 0 && (

                    <div
                      style={{
                        display: 'grid',
                        gridTemplateColumns:
                          'repeat(auto-fit, minmax(200px, 1fr))',
                        gap: '0.5rem',
                        marginTop: '1rem'
                      }}
                    >

                      {post.mediaUrls.map((url, index) => (

                        <img
                          key={index}
                          src={url}
                          alt="Post media"
                          style={{
                            width: '100%',
                            borderRadius: '12px',
                            objectFit: 'cover',
                            maxHeight: '300px'
                          }}
                        />
                      ))}
                    </div>
                  )}
                </div>

                <PostActions
                  post={post}
                  userId={user?.id}
                />
              </div>
            ))}
          </div>
        </div>

        <div className="feed-sidebar">

          <div className="card sidebar-widget">

            <h3 className="widget-title">
              <TrendingUp size={18} />
              Oficios Populares
            </h3>

            <div className="popular-list">

              {popularJobs.map(job => (

                <div
                  key={job.name}
                  className="popular-item"
                >

                  <span className="job-name">
                    {job.name}
                  </span>

                  <span className="job-count">
                    {job.count} publicaciones
                  </span>
                </div>
              ))}
            </div>
          </div>

          <div className="card sidebar-widget">

            <h3 className="widget-title">
              <Users size={18} />
              Personas Cerca de Ti
            </h3>

            <div className="nearby-placeholder">
              <p className="text-muted text-sm">
                Próximamente...
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}