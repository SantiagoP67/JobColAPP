import React, { useState, useRef, useEffect } from 'react';
import {
  Briefcase,
  Search,
  LayoutGrid,
  FileText,
  Handshake,
  Newspaper,
  Bell,
  User,
  Image as ImageIcon,
  Settings,
  LogOut
} from 'lucide-react';

import { useNavigate } from 'react-router-dom';

import JobSearch from '../components/views/JobSearch';
import FeedView from '../components/views/FeedView';
import ApplicationsView from '../components/views/ApplicationsView';
import ContractsView from '../components/views/ContractsView';
import NotificationsView from '../components/views/NotificationsView';
import ProfileView from '../components/views/ProfileView';
import PortfolioView from '../components/views/PortfolioView';
import SettingsView from '../components/views/SettingsView';

import { getUserFromToken, getAppRole, getCurrentUser } from "../services/authService";
import { countUnread } from "../services/notificationService";

import './Dashboard.css';
import EmployerOffersView from '../components/views/EmployerOffersView';

import { useSearchParams } from 'react-router-dom';

export default function Dashboard() {

  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [user, setUser] = useState(null);
  const [searchParams] = useSearchParams();
  const initialTab = searchParams.get("tab");

  const [activeTab, setActiveTab] = useState(
    initialTab || 'search'
  );
  const dropdownRef = useRef(null);
  const navigate = useNavigate();

  // Notification badge count
  const [unreadCount, setUnreadCount] = useState(0);

    useEffect(() => {

      const token = localStorage.getItem("token");

      if (!token) {

        navigate(`/?redirect=${encodeURIComponent(window.location.pathname + window.location.search)}`);

      }

    }, []);

    useEffect(() => {
    const fetchUser = async () => {
      try {
        const data = await getCurrentUser();
        setUser(data);
      } catch (err) {
        console.error("Error cargando usuario:", err);
      }
    };

    fetchUser();
  }, []);

  const role = user?.role;

  // Poll unread notifications
  useEffect(() => {
    if (!user?.id) return;

    const fetchUnread = async () => {
      try {
        const count = await countUnread(user.id);
        setUnreadCount(count);
      } catch (err) {
        // silently ignore
      }
    };

    fetchUnread();
    const interval = setInterval(fetchUnread, 30000); // every 30 seconds

    return () => clearInterval(interval);
  }, [user?.id]);

  useEffect(() => {
    window.history.pushState(null, "", window.location.href);

    const handleBack = () => {
      window.history.pushState(null, "", window.location.href);
    };

    window.addEventListener("popstate", handleBack);

    return () => {
      window.removeEventListener("popstate", handleBack);
    };
  }, []);

  
  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsDropdownOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleMenuClick = (tab) => {
    setActiveTab(tab);
    setIsDropdownOpen(false);
  };

  
  const renderContent = () => {

    if (role === "TRABAJADOR") {
      switch(activeTab) {
        case 'search': return <JobSearch />; 
        case 'applications': return <ApplicationsView />;
        case 'contracts': return <ContractsView />;
        case 'feed': return <FeedView />;
        case 'notifications': return <NotificationsView onNavigate={setActiveTab} />;
        case 'profile': return <ProfileView />;
        case 'portfolio': return <PortfolioView />;
        case 'settings': return <SettingsView />;
        default: return <JobSearch />;
      }
    }

    if (role === "EMPLEADOR") {
      switch(activeTab) {
        case 'jobs': return <EmployerOffersView />; 
        case 'candidates': return <ApplicationsView />;
        case 'contracts': return <ContractsView />;
        case 'feed': return <FeedView />;
        case 'notifications': return <NotificationsView onNavigate={setActiveTab} />;
        case 'profile': return <ProfileView />;
        case 'settings': return <SettingsView />;
        default: return <FeedView />;
      }
    }

    if (role === "ADMIN") {
      switch(activeTab) {
        case 'users': return <div>Gestión de usuarios</div>;
        case 'reports': return <div>Reportes</div>;
        default: return <div>Panel de administrador</div>;
      }
    }

    return <div>Sin acceso</div>;
  };

  return (
    <div className="dashboard">

      <header className="dash-header">
        <div className="dash-container dash-header-inner">

          {/* LOGO */}
          <div className="dash-logo-group">
            <div className="logo cursor-pointer" onClick={() => navigate('/dashboard')}>
              <div className="logo-icon small">
                <Briefcase size={20} color="white" />
              </div>
              <span className="logo-text text-lg">JobCol</span>
            </div>

            
            <span className="role-badge">
              {role === "TRABAJADOR" && "Trabajador"}
              {role === "EMPLEADOR" && "Empleador"}
              {role === "ADMIN" && "Administrador"}
            </span>
          </div>

          
          <nav className="dash-nav">

            {role === "TRABAJADOR" && (
              <>
                <button className={`nav-item ${activeTab === 'search' ? 'active' : ''}`} onClick={() => setActiveTab('search')}>
                  <Search size={20} />
                  <span>Buscar Trabajo</span>
                </button>

                <button className={`nav-item ${activeTab === 'applications' ? 'active' : ''}`} onClick={() => setActiveTab('applications')}>
                  <FileText size={20} />
                  <span>Postulaciones</span>
                </button>

                <button className={`nav-item ${activeTab === 'contracts' ? 'active' : ''}`} onClick={() => setActiveTab('contracts')}>
                  <Handshake size={20} />
                  <span>Contratos</span>
                </button>

                <button className={`nav-item ${activeTab === 'feed' ? 'active' : ''}`} onClick={() => setActiveTab('feed')}>
                  <Newspaper size={20} />
                  <span>Publicaciones</span>
                </button>
              </>
            )}

            
            {role === "EMPLEADOR" && (
              <>
                <button className={`nav-item ${activeTab === 'jobs' ? 'active' : ''}`} onClick={() => setActiveTab('jobs')}>
                  <Briefcase size={20} />
                  <span>Mis Ofertas</span>
                </button>

                <button className={`nav-item ${activeTab === 'candidates' ? 'active' : ''}`} onClick={() => setActiveTab('candidates')}>
                  <User size={20} />
                  <span>Candidatos</span>
                </button>

                <button className={`nav-item ${activeTab === 'contracts' ? 'active' : ''}`} onClick={() => setActiveTab('contracts')}>
                  <Handshake size={20} />
                  <span>Contratos</span>
                </button>

                <button className={`nav-item ${activeTab === 'feed' ? 'active' : ''}`} onClick={() => setActiveTab('feed')}>
                  <Newspaper size={20} />
                  <span>Publicaciones</span>
                </button>
              </>
            )}

            {role === "ADMIN" && (
              <>
                <button className={`nav-item ${activeTab === 'users' ? 'active' : ''}`} onClick={() => setActiveTab('users')}>
                  <User size={20} />
                  <span>Usuarios</span>
                </button>

                <button className={`nav-item ${activeTab === 'reports' ? 'active' : ''}`} onClick={() => setActiveTab('reports')}>
                  <LayoutGrid size={20} />
                  <span>Reportes</span>
                </button>
              </>
            )}

          </nav>

          
          <div className="dash-user">

            <button
              className={`icon-btn ${activeTab === 'notifications' ? 'active-icon' : ''}`}
              onClick={() => {
                setActiveTab('notifications');
                setUnreadCount(0); // optimistic clear
              }}
              style={{ position: 'relative' }}
            >
              <Bell size={20} />
              {unreadCount > 0 && (
                <span className="notification-dot" style={{
                  width: 'auto',
                  minWidth: '18px',
                  height: '18px',
                  fontSize: '0.65rem',
                  fontWeight: 700,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  padding: '0 4px',
                  top: '-4px',
                  right: '-6px'
                }}>
                  {unreadCount > 9 ? '9+' : unreadCount}
                </span>
              )}
            </button>

            <div className="avatar-dropdown-container" ref={dropdownRef}>
              <div className="avatar" onClick={() => setIsDropdownOpen(!isDropdownOpen)}>
                {user?.username?.charAt(0)?.toUpperCase() || "U"}
              </div>

              {isDropdownOpen && (
                <div className="profile-dropdown">

                  <div className="dropdown-header">
                    <p className="dropdown-name">{user?.username}</p>
                    <p className="dropdown-email">{user?.email}</p>
                  </div>

                  <div className="dropdown-options">
                    <button className="dropdown-item" onClick={() => handleMenuClick('profile')}>
                      <User size={16} /> Mi Perfil
                    </button>

                    <button className="dropdown-item" onClick={() => handleMenuClick('portfolio')}>
                      <ImageIcon size={16} /> Mi Portfolio
                    </button>

                    <button className="dropdown-item" onClick={() => handleMenuClick('settings')}>
                      <Settings size={16} /> Configuración
                    </button>
                  </div>

                  <div className="dropdown-footer">
                    <button
                      className="dropdown-item text-danger"
                      onClick={() => {
                        localStorage.removeItem("token");
                        navigate('/');
                      }}
                    >
                      <LogOut size={16} /> Cerrar Sesión
                    </button>
                  </div>

                </div>
              )}
            </div>

          </div>

        </div>
      </header>

      <main className="dash-main dash-container">
        {renderContent()}
      </main>

    </div>
  );
}