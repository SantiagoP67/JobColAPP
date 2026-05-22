import React, { useState, useEffect, useRef } from 'react';
import { Pencil, MapPin, Mail, Star, Save, Camera } from 'lucide-react';
import Button from '../Button';
import { useToast } from '../../context/ToastContext';
import './ProfileView.css';
import { getCurrentUser } from '../../services/authService';
import { getWorkerProfile } from '../../services/profileService';
import { getEmployerProfile } from '../../services/employerProfileService';
import { updateUserPhoto } from '../../services/userService';
import { getPostulationsByWorker, getPostulationsByEmployer } from '../../services/postulationService';
import { getContractsByUser } from '../../services/contractService';

export default function ProfileView() {
  const { showToast } = useToast();
  const [profileVisible, setProfileVisible] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const [user, setUser] = useState(null);
  const [profile, setProfile] = useState(null);
  const [realStats, setRealStats] = useState({ postulations: 0, contracts: 0 });
  const fileInputRef = useRef(null);

  useEffect(() => { loadProfile(); }, []);

  const loadProfile = async () => {
    try {
      const userData = await getCurrentUser();
      setUser(userData);
      if (userData.role === "TRABAJADOR") {
        const workerProfile = await getWorkerProfile(userData.id);
        setProfile(workerProfile);
        try {
          const posts = await getPostulationsByWorker();
          const contracts = await getContractsByUser(userData.id);
          setRealStats({ postulations: posts.length, contracts: contracts.length });
        } catch (e) { console.warn(e); }
      } else if (userData.role === "EMPLEADOR") {
        const employerProfile = await getEmployerProfile(userData.id);
        setProfile(employerProfile);
        try {
          const posts = await getPostulationsByEmployer(userData.id);
          const contracts = await getContractsByUser(userData.id);
          setRealStats({ postulations: posts.length, contracts: contracts.length });
        } catch (e) { console.warn(e); }
      }
    } catch (err) {
      console.error("Error cargando perfil", err);
      showToast("Error cargando perfil", "error");
    }
  };

  const handleEditToggle = () => {
    if (isEditing) showToast('Perfil guardado exitosamente', 'success');
    setIsEditing(!isEditing);
  };

  const handleImageChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    try {
      const formData = new FormData();
      formData.append("file", file);
      const updatedUser = await updateUserPhoto(user.id, formData);
      setUser(updatedUser);
      showToast("Foto actualizada correctamente", "success");
    } catch (error) {
      console.error(error);
      showToast("Error subiendo imagen", "error");
    }
  };

  if (!user) return <p>Cargando usuario...</p>;
  if (!profile) return <p>Cargando perfil...</p>;

  return (
    <div className="profile-view-wrapper">
      <div className="profile-header-container">
        <div className="page-header m-0">
          <h1 className="page-title">Mi Perfil</h1>
          <p className="page-subtitle">Gestiona tu información profesional</p>
        </div>
        <Button className="edit-profile-btn" variant={isEditing ? 'primary' : 'secondary'} onClick={handleEditToggle}>
          {isEditing ? (<><Save size={18} /> Guardar Perfil</>) : (<><Pencil size={18} /> Editar Perfil</>)}
        </Button>
      </div>

      <div className="profile-content">
        <div className="card profile-card-identity">
          <div className="profile-avatar-wrapper">
            <div className="profile-avatar-large" onClick={() => fileInputRef.current.click()}>
              {user.imgUrl ? (
                <img src={user.imgUrl} alt="Foto de perfil" className="profile-avatar-image" />
              ) : (user.username?.charAt(0).toUpperCase())}
              <div className="avatar-overlay"><Camera size={20} /></div>
            </div>
            <input type="file" accept="image/*" ref={fileInputRef} style={{ display: 'none' }} onChange={handleImageChange} />
          </div>
          <div className="profile-info-main">
            <h2 className="profile-name">{user.firstName} {user.lastName}</h2>
            <div className="profile-info-row"><span className="text-gray-500">@{user.username}</span></div>
            <div className="profile-info-row"><Mail size={16} /> {user.email}</div>
            <div className="profile-info-row"><MapPin size={16} /> {profile.location || "Sin ubicación"}</div>
            <div className="profile-rating-row">
              <span className="rating-badge"><Star size={16} fill="currentColor" /> {profile.averageRating || 0}</span>
              {user.role === "EMPLEADOR" && (
                <span className="experience-badge">{profile.totalJobsPosted || 0} trabajos publicados</span>
              )}
            </div>
          </div>
        </div>

        <div className="card">
          <h3 className="card-title">Descripción</h3>
          <p className="about-text">
            {user.role === "EMPLEADOR" ? profile.description || "Sin descripción" : profile.experience || "Sin información"}
          </p>
        </div>

        {user.role === "TRABAJADOR" && (
          <div className="card">
            <h3 className="card-title m-0">Habilidades</h3>
            <p className="card-subtitle">Tus habilidades principales</p>
            <div className="skills-container">
              {(profile.skills || "").split(",").map(skill => (
                <span key={skill} className="skill-pill">{skill}</span>
              ))}
            </div>
          </div>
        )}

        {user.role === "TRABAJADOR" && (
          <div className="card privacy-card">
            <div>
              <h3 className="card-title m-0">Configuración de Privacidad</h3>
              <p className="card-subtitle mt-2">Perfil Visible</p>
              <p className="privacy-desc">Permite que los empleadores vean tu perfil</p>
            </div>
            <label className="toggle-switch">
              <input type="checkbox" checked={profileVisible} onChange={() => setProfileVisible(!profileVisible)} />
              <span className="slider round"></span>
            </label>
          </div>
        )}

        <div className="card">
          <h3 className="card-title">Estadísticas</h3>
          <div className="profile-stats-grid">
            {user.role === "TRABAJADOR" ? (
              <>
                <div className="p-stat-box bg-blue-50">
                  <span className="p-stat-number text-blue-600">{realStats.postulations}</span>
                  <span className="p-stat-label">Postulaciones</span>
                </div>
                <div className="p-stat-box bg-purple-50">
                  <span className="p-stat-number text-purple-600">{realStats.contracts}</span>
                  <span className="p-stat-label">Contratos</span>
                </div>
                <div className="p-stat-box bg-yellow-50">
                  <span className="p-stat-number text-yellow-600">{profile.averageRating || 0}</span>
                  <span className="p-stat-label">Calificación</span>
                </div>
              </>
            ) : (
              <>
                <div className="p-stat-box bg-blue-50">
                  <span className="p-stat-number text-blue-600">{profile.totalJobsPosted || 0}</span>
                  <span className="p-stat-label">Ofertas</span>
                </div>
                <div className="p-stat-box bg-purple-50">
                  <span className="p-stat-number text-purple-600">{realStats.contracts}</span>
                  <span className="p-stat-label">Contratos</span>
                </div>
                <div className="p-stat-box bg-yellow-50">
                  <span className="p-stat-number text-yellow-600">{profile.averageRating || 0}</span>
                  <span className="p-stat-label">Calificación</span>
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}