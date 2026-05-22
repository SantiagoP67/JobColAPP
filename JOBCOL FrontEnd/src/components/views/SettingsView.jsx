import React, { useState } from 'react';
import Button from '../Button';
import { useToast } from '../../context/ToastContext';
import './SettingsView.css';

export default function SettingsView() {
  const [activeTab, setActiveTab] = useState('seguridad');
  const { showToast } = useToast();

  const [notifs, setNotifs] = useState({
    email: true,
    push: true,
    postulaciones: true,
    mensajes: true
  });

  const handleToggle = (key) => setNotifs(prev => ({ ...prev, [key]: !prev[key] }));

  const handleUpdatePassword = () => {
    showToast('Contraseña actualizada exitosamente', 'success');
  };

  const handleSavePreferences = () => {
    showToast('Preferencias guardadas exitosamente', 'success');
  };

  return (
    <div className="settings-view-wrapper">
      <div className="page-header m-0">
        <h1 className="page-title">Configuración</h1>
        <p className="page-subtitle">Gestiona tu cuenta y preferencias</p>
      </div>

      <div className="settings-tabs-container">
        <button 
          className={`settings-tab ${activeTab === 'seguridad' ? 'active' : ''}`}
          onClick={() => setActiveTab('seguridad')}
        >
          Seguridad
        </button>
        <button 
          className={`settings-tab ${activeTab === 'notificaciones' ? 'active' : ''}`}
          onClick={() => setActiveTab('notificaciones')}
        >
          Notificaciones
        </button>
      </div>

      <div className="settings-content">
        {activeTab === 'seguridad' && (
          <>
            <div className="card settings-card">
              <h3 className="card-title">Cambiar Contraseña</h3>
              <p className="card-subtitle">Actualiza tu contraseña para mantener tu cuenta segura</p>
              
              <div className="settings-form">
                <div className="form-group">
                  <label>Contraseña Actual</label>
                  <input type="password" />
                </div>
                <div className="form-group">
                  <label>Nueva Contraseña</label>
                  <input type="password" />
                </div>
                <div className="form-group">
                  <label>Confirmar Nueva Contraseña</label>
                  <input type="password" />
                </div>
                <Button className="mt-4" onClick={handleUpdatePassword}>Actualizar Contraseña</Button>
              </div>
            </div>

            <div className="card settings-card">
              <h3 className="card-title">Información de la Cuenta</h3>
              <p className="card-subtitle">Detalles de tu cuenta en JobCol</p>
              
              <div className="account-info">
                <div className="info-block">
                  <span className="info-label">Email</span>
                  <span className="info-value">davidortiz37575@gmail.com</span>
                </div>
              </div>
            </div>
          </>
        )}

        {activeTab === 'notificaciones' && (
          <div className="card settings-card">
            <h3 className="card-title">Preferencias de Notificaciones</h3>
            <p className="card-subtitle mb-4">Controla cómo y cuándo recibes notificaciones</p>
            
            <div className="notification-list">
              <div className="notification-item">
                <div className="notification-text">
                  <h4>Notificaciones por Email</h4>
                  <p>Recibe actualizaciones importantes por correo</p>
                </div>
                <label className="toggle-switch">
                  <input type="checkbox" checked={notifs.email} onChange={() => handleToggle('email')} />
                  <span className="slider round"></span>
                </label>
              </div>

              <div className="notification-item">
                <div className="notification-text">
                  <h4>Notificaciones Push</h4>
                  <p>Recibe notificaciones en tiempo real</p>
                </div>
                <label className="toggle-switch">
                  <input type="checkbox" checked={notifs.push} onChange={() => handleToggle('push')} />
                  <span className="slider round"></span>
                </label>
              </div>

              <div className="notification-item">
                <div className="notification-text">
                  <h4>Postulaciones</h4>
                  <p>Notificaciones sobre nuevas postulaciones o cambios de estado</p>
                </div>
                <label className="toggle-switch">
                  <input type="checkbox" checked={notifs.postulaciones} onChange={() => handleToggle('postulaciones')} />
                  <span className="slider round"></span>
                </label>
              </div>

              <div className="notification-item">
                <div className="notification-text">
                  <h4>Mensajes</h4>
                  <p>Notificaciones cuando recibes un nuevo mensaje</p>
                </div>
                <label className="toggle-switch">
                  <input type="checkbox" checked={notifs.mensajes} onChange={() => handleToggle('mensajes')} />
                  <span className="slider round"></span>
                </label>
              </div>
            </div>

            <Button className="mt-8" onClick={handleSavePreferences}>Guardar Preferencias</Button>
          </div>
        )}
      </div>
    </div>
  );
}
