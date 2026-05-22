import React, { useState } from 'react';
import { Search, MoreVertical, Send, Paperclip } from 'lucide-react';
import { useToast } from '../../context/ToastContext';
import './MessagesView.css';

const conversations = [
  {
    id: 1,
    name: 'Constructora Pérez',
    avatar: 'C',
    unread: 2,
    preview: '¿Estás disponible mañana para reparar una fuga?',
    time: 'Hace 10 min',
    online: true,
    active: true
  },
  {
    id: 2,
    name: 'Carlos Rodríguez - Plomero',
    avatar: 'C',
    unread: 0,
    preview: 'Perfecto, estaré a las 10am en tu casa.',
    time: 'Hace 2 horas',
    online: false,
    active: false
  },
  {
    id: 3,
    name: 'María - Servicios de Aseo',
    avatar: 'M',
    unread: 1,
    preview: '¿Cuántas horas necesitas el servicio?',
    time: 'Ayer',
    online: true,
    active: false
  }
];

const initialMessages = [
  { id: 1, type: 'received', text: 'Hola! Gracias por postularte a nuestra oferta de Desarrollador Full Stack.', time: '10:30 AM' },
  { id: 2, type: 'sent', text: '¡Hola! Gracias por contactarme. Estoy muy interesado en la posición.', time: '10:35 AM' },
  { id: 3, type: 'received', text: 'Nos gustaría agendar una entrevista contigo. ¿Tienes disponibilidad esta semana?', time: '10:40 AM' },
  { id: 4, type: 'sent', text: 'Sí, estaría disponible el miércoles o jueves por la tarde.', time: '10:45 AM' },
  { id: 5, type: 'received', text: 'Perfecto! Te confirmamos para el miércoles a las 3pm. Te enviaremos el link de la videollamada.', time: '10:50 AM' }
];

export default function MessagesView() {
  const [messages, setMessages] = useState(initialMessages);
  const [inputText, setInputText] = useState('');
  const { showToast } = useToast();

  const handleSend = () => {
    if (!inputText.trim()) return;
    const newMsg = {
      id: Date.now(),
      type: 'sent',
      text: inputText,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };
    setMessages([...messages, newMsg]);
    setInputText('');
  };

  const handleAttachment = () => {
    showToast('Adjuntar archivos estará disponible pronto', 'info');
  };

  return (
    <div className="messages-view-wrapper">
      <div className="page-header">
        <h1 className="page-title">Mensajes</h1>
        <p className="page-subtitle">Conversa con empleadores y candidatos</p>
      </div>

      <div className="messages-layout card">
        <div className="conversations-list">
          <div className="conversations-search">
            <div className="search-input-wrapper msg-search">
              <Search className="search-icon" size={18} />
              <input type="text" placeholder="Buscar conversaciones..." />
            </div>
          </div>

          <div className="threads">
            {conversations.map(conv => (
              <div key={conv.id} className={`thread-item ${conv.active ? 'active' : ''}`}>
                <div className="thread-avatar">
                  {conv.avatar}
                </div>
                <div className="thread-info">
                  <div className="thread-header-row">
                    <span className="thread-name">{conv.name}</span>
                    {conv.unread > 0 && <span className="unread-badge">{conv.unread}</span>}
                  </div>
                  <p className="thread-preview">{conv.preview}</p>
                  <div className="thread-time-row">
                    <span className={`status-dot ${conv.online ? 'online' : 'offline'}`}></span>
                    <span className="thread-time">{conv.time}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="chat-window">
          <div className="chat-header">
            <div className="chat-header-info">
              <div className="thread-avatar">C</div>
              <div>
                <h3 className="chat-partner-name">Constructora Pérez</h3>
                <p className="chat-partner-status">En línea</p>
              </div>
            </div>
            <button className="icon-btn">
              <MoreVertical size={20} />
            </button>
          </div>

          <div className="chat-history">
            {messages.map(msg => (
              <div key={msg.id} className={`message-bubble ${msg.type}`}>
                <p>{msg.text}</p>
                <span className="message-time">{msg.time}</span>
              </div>
            ))}
          </div>

          <div className="chat-input-area">
            <button className="icon-btn text-muted" onClick={handleAttachment}>
              <Paperclip size={20} />
            </button>
            <input 
              type="text" 
              placeholder="Escribe un mensaje..." 
              className="chat-input-field" 
              value={inputText}
              onChange={(e) => setInputText(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSend()}
            />
            <button className="send-btn" onClick={handleSend} disabled={!inputText.trim()}>
              <Send size={18} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
