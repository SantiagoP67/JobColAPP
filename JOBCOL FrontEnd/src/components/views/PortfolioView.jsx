import React, { useState } from 'react';
import { Image as ImageIcon, Video, Briefcase, Plus, MoreVertical } from 'lucide-react';
import Button from '../Button';
import { useToast } from '../../context/ToastContext';
import './PortfolioView.css';

const initialPosts = [
  {
    id: 1,
    author: 'Carlos Rodríguez',
    role: 'Plomero',
    avatar: 'https://i.pravatar.cc/150?img=11',
    time: 'Hace 2 horas',
    content: '¡Instalación de lavamanos completada! Cliente muy contento con el resultado. Trabajo realizado en Chapinero Alto. Disponible para más trabajos esta semana. 🔧',
    image: 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&q=80&w=800'
  }
];

export default function PortfolioView() {
  const [posts, setPosts] = useState(initialPosts);
  const [newPostText, setNewPostText] = useState('');
  const { showToast } = useToast();

  const handleCreatePost = () => {
    if (!newPostText.trim()) return;

    const newPost = {
      id: Date.now(),
      author: 'davidortiz37575',
      role: 'Desarrollador Full Stack',
      avatar: 'D', 
      time: 'Justo ahora',
      content: newPostText,
      image: null
    };

    setPosts([newPost, ...posts]);
    setNewPostText('');
    showToast('Publicación creada con éxito', 'success');
  };

  const handleMediaClick = (type) => {
    showToast(`Agregar ${type} estará disponible pronto`, 'info');
  };

  return (
    <div className="portfolio-view-wrapper">
      <div className="portfolio-header-container">
        <div>
          <h1 className="page-title">Mis Trabajos y Evidencias</h1>
          <p className="page-subtitle">Comparte trabajos realizados y conecta con personas que buscan tus servicios</p>
        </div>
        <Button className="new-post-btn" onClick={handleCreatePost}>
          <Plus size={18} /> Nueva Publicación
        </Button>
      </div>

      <div className="portfolio-content">
        {/* Create Post Card */}
        <div className="card create-post-card">
          <div className="create-post-input-area">
            <div className="avatar-small">d</div>
            <input 
              type="text" 
              placeholder="¿Qué quieres compartir hoy?" 
              className="create-post-input"
              value={newPostText}
              onChange={(e) => setNewPostText(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleCreatePost()}
            />
          </div>
          <div className="create-post-actions">
            <button className="post-action-btn text-blue-500" onClick={() => handleMediaClick('Foto')}>
              <ImageIcon size={18} /> Foto
            </button>
            <button className="post-action-btn text-purple-500" onClick={() => handleMediaClick('Video')}>
              <Video size={18} /> Video
            </button>
            <button className="post-action-btn text-green-500" onClick={() => handleMediaClick('Experiencia')}>
              <Briefcase size={18} /> Experiencia
            </button>
          </div>
        </div>

        {/* Portfolio Posts List */}
        <div className="portfolio-posts">
          {posts.map(post => (
            <div key={post.id} className="feed-post card animate-fade-in">
              <div className="post-header">
                <div className="post-author">
                  <div className={`post-avatar ${post.avatar === 'D' ? 'avatar-small' : ''}`}>
                    {post.avatar === 'D' ? 'D' : <img src={post.avatar} alt={post.author} />}
                  </div>
                  <div>
                    <h3 className="author-name">{post.author}</h3>
                    <p className="author-role">{post.role}</p>
                    <p className="post-time">{post.time}</p>
                  </div>
                </div>
                <button className="icon-btn" onClick={() => showToast('Opciones de publicación', 'info')}><MoreVertical size={20} /></button>
              </div>
              <div className="post-content">
                <p>{post.content}</p>
                {post.image && (
                  <div className="post-image">
                    <img src={post.image} alt="Evidencia de trabajo" />
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
