import React, { useState, useEffect } from 'react';
import {
  Users,
  MessageSquare,
  FileText,
  Star,
  Check,
  Bell,
  Handshake,
  Loader2,
  ChevronRight
} from 'lucide-react';

import Button from '../Button';

import { getCurrentUser } from '../../services/authService';

import {
  getUserNotifications,
  markAsRead,
  markAllAsRead
} from '../../services/notificationService';

import './NotificationsView.css';

const ICON_MAP = {
  POSTULACION_RECIBIDA: <Users size={20} className="text-primary" />,
  POSTULACION_ACEPTADA: <Check size={20} className="text-success" />,
  POSTULACION_RECHAZADA: <MessageSquare size={20} className="text-danger" />,
  CONTRATO_GENERADO: <Handshake size={20} className="text-purple" />,
  CONTRATO_FINALIZADO: <FileText size={20} className="text-info" />,
  NUEVA_RESEA_A: <Star size={20} className="text-warning" />,
  SISTEMA: <Bell size={20} className="text-primary" />,
  SEGURIDAD: <Bell size={20} className="text-danger" />,
  INFO: <Bell size={20} className="text-primary" />,
};

const BG_MAP = {
  POSTULACION_RECIBIDA: 'bg-primary-light',
  POSTULACION_ACEPTADA: 'bg-success-light',
  POSTULACION_RECHAZADA: 'bg-danger-light',
  CONTRATO_GENERADO: 'bg-purple-light',
  CONTRATO_FINALIZADO: 'bg-info-light',
  NUEVA_RESEA_A: 'bg-warning-light',
  SISTEMA: 'bg-primary-light',
  SEGURIDAD: 'bg-danger-light',
  INFO: 'bg-primary-light',
};

// Tabs del dashboard
const NAV_MAP = {
  POSTULACION_RECIBIDA: 'candidates',
  POSTULACION_ACEPTADA: 'applications',
  POSTULACION_RECHAZADA: 'applications',
  CONTRATO_GENERADO: 'contracts',
  CONTRATO_FINALIZADO: 'contracts',
  NUEVA_RESEA_A: 'contracts',
  SISTEMA: 'search',
  INFO: 'search',
};

const NAV_LABELS = {
  POSTULACION_RECIBIDA: 'Ver candidatos',
  POSTULACION_ACEPTADA: 'Ver postulaciones',
  POSTULACION_RECHAZADA: 'Ver postulaciones',
  CONTRATO_GENERADO: 'Ir a contratos',
  CONTRATO_FINALIZADO: 'Ir a contratos',
  NUEVA_RESEA_A: 'Ver contratos',
  SISTEMA: 'Buscar trabajo',
  INFO: 'Buscar trabajo',
};

// Tipos ocultos
const HIDDEN_NOTIFICATION_TYPES = ['SEGURIDAD'];

function timeAgo(dateStr) {

  if (!dateStr) return '';

  const diff = (new Date() - new Date(dateStr)) / 1000;

  if (diff < 60) return 'Hace unos segundos';

  if (diff < 3600) {
    return `Hace ${Math.floor(diff / 60)} min`;
  }

  if (diff < 86400) {
    return `Hace ${Math.floor(diff / 3600)} horas`;
  }

  if (diff < 604800) {
    return `Hace ${Math.floor(diff / 86400)} días`;
  }

  return new Date(dateStr).toLocaleDateString('es-CO');
}

export default function NotificationsView({ onNavigate }) {

  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('Todas');
  const [user, setUser] = useState(null);

  useEffect(() => {
    loadNotifications();
  }, []);

  const loadNotifications = async () => {

    try {

      setLoading(true);

      const userData = await getCurrentUser();

      setUser(userData);

      const data = await getUserNotifications(userData.id);

      const sorted = [...data].sort((a, b) => {

        if (!a.createdAt) return 1;
        if (!b.createdAt) return -1;

        return new Date(b.createdAt) - new Date(a.createdAt);
      });

      setNotifications(sorted);

    } catch (err) {

      console.error('Error cargando notificaciones:', err);

    } finally {

      setLoading(false);
    }
  };

  const handleMarkAllRead = async () => {

    if (!user) return;

    try {

      await markAllAsRead(user.id);

      setNotifications(prev =>
        prev.map(n => ({
          ...n,
          read: true
        }))
      );

    } catch (err) {

      console.error('Error marcando como leídas:', err);
    }
  };

  const handleClickNotification = async (notif) => {

    // Marcar como leída
    if (!notif.read) {

      try {

        await markAsRead(notif.id);

        setNotifications(prev =>
          prev.map(n =>
            n.id === notif.id
              ? { ...n, read: true }
              : n
          )
        );

      } catch (err) {

        console.error('Error marcando notificación:', err);
      }
    }

    // Navegar
    const targetTab = NAV_MAP[notif.type];

    if (targetTab && onNavigate) {
      onNavigate(targetTab);
    }
  };

  // =========================
  // FILTRAR NOTIFICACIONES
  // =========================

  const filteredNotifications = notifications.filter((n) => {

    // Ocultar seguridad/autenticación
    if (HIDDEN_NOTIFICATION_TYPES.includes(n.type)) {
      return false;
    }

    // Mostrar solo no leídas
    if (filter === 'No leídas') {
      return !n.read;
    }

    return true;
  });

  // =========================
  // CONTADOR DE NO LEÍDAS
  // =========================

  const unreadCount = notifications.filter(
    (n) =>
      !n.read &&
      !HIDDEN_NOTIFICATION_TYPES.includes(n.type)
  ).length;

  // =========================
  // LOADING
  // =========================

  if (loading) {

    return (
      <div
        className="notifications-view-wrapper"
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '300px'
        }}
      >
        <Loader2
          size={32}
          style={{
            color: 'var(--primary)',
            animation: 'spin 1s linear infinite'
          }}
        />
      </div>
    );
  }

  // =========================
  // VIEW
  // =========================

  return (

    <div className="notifications-view-wrapper">

      <div className="notifications-header">

        <div>

          <h1 className="page-title">
            Centro de Notificaciones
          </h1>

          <p className="page-subtitle">

            {unreadCount > 0
              ? `Tienes ${unreadCount} notificación${unreadCount > 1 ? 'es' : ''} sin leer`
              : 'No tienes notificaciones pendientes'
            }

          </p>

        </div>

        {unreadCount > 0 && (

          <Button
            variant="secondary"
            className="mark-read-btn"
            onClick={handleMarkAllRead}
          >
            <Check size={18} />
            {' '}
            Marcar todas como leídas
          </Button>
        )}

      </div>

      {/* FILTRO */}

      <div className="notifications-filter">

        <select
          className="dash-select"
          value={filter}
          onChange={(e) => setFilter(e.target.value)}
        >
          <option>Todas</option>
          <option>No leídas</option>
        </select>

      </div>

      {/* LISTA */}

      <div className="notifications-list">

        {filteredNotifications.length === 0 ? (

          <div
            style={{
              textAlign: 'center',
              padding: '3rem',
              color: 'var(--text-muted)'
            }}
          >

            <Bell
              size={48}
              style={{
                opacity: 0.3,
                marginBottom: '1rem'
              }}
            />

            <p
              style={{
                fontSize: '1.1rem',
                fontWeight: 500
              }}
            >

              {filter === 'No leídas'
                ? 'No tienes notificaciones sin leer'
                : 'No tienes notificaciones aún'
              }

            </p>

          </div>

        ) : (

          filteredNotifications.map(notif => {

            const hasNav = !!NAV_MAP[notif.type];

            return (

              <div
                key={notif.id}
                className={`
                  notification-card
                  ${!notif.read ? 'unread' : ''}
                  ${hasNav ? 'clickable' : ''}
                `}
                onClick={() => handleClickNotification(notif)}
                style={{ cursor: 'pointer' }}
              >

                <div className="notification-content-left">

                  <div
                    className={`
                      notification-icon-wrapper
                      ${BG_MAP[notif.type] || 'bg-primary-light'}
                    `}
                  >

                    {ICON_MAP[notif.type] ||
                      <Bell size={20} className="text-primary" />
                    }

                  </div>

                  <div className="notification-text">

                    <h3 className="notification-title">
                      {notif.title}
                    </h3>

                    <p className="notification-description">
                      {notif.message}
                    </p>

                    <span className="notification-time">
                      {timeAgo(notif.createdAt)}
                    </span>

                  </div>

                </div>

                <div
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0.5rem'
                  }}
                >

                  {!notif.read && (
                    <span className="notification-badge-new">
                      Nueva
                    </span>
                  )}

                  {hasNav && (

                    <span className="notification-nav-hint">

                      {NAV_LABELS[notif.type]}

                      <ChevronRight size={14} />

                    </span>
                  )}

                </div>

              </div>
            );
          })
        )}

      </div>

    </div>
  );
}