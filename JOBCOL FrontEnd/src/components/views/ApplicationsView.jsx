import React, { useState, useEffect } from 'react';
import { Building2, Calendar, User, CheckCircle2, Loader2, X, FolderOpen, ChevronRight, ArrowLeft, Star, Award } from 'lucide-react';
import Button from '../Button';
import { getPostulationsByWorker, getPostulationsByEmployer, updatePostulationStatus } from '../../services/postulationService';
import { getCurrentUser, getUserById } from '../../services/authService';
import { getWorkerProfile } from '../../services/profileService';
import { getWhatsAppLink } from '../../services/messageService';
import { createContract } from '../../services/contractService';
import { createNotification } from '../../services/notificationService';
import { getContractsByUser } from '../../services/contractService';
import { getReviewsByUser } from '../../services/reviewService';
import './ApplicationsView.css';

export default function ApplicationsView() {
  const [postulations, setPostulations] = useState([]);
  const [activeFilter, setActiveFilter] = useState('Todas');
  const [user, setUser] = useState(null);
  const [role, setRole] = useState(null);
  const [selectedCandidate, setSelectedCandidate] = useState(null);
  const [candidateProfile, setCandidateProfile] = useState(null);
  const [candidateUser, setCandidateUser] = useState(null);
  const [profileLoading, setProfileLoading] = useState(false);
  const [whatsAppLink, setWhatsAppLink] = useState('');
  const [candidateReviews, setCandidateReviews] = useState([]);
  const [showContractModal, setShowContractModal] = useState(false);
  const [selectedPostulation, setSelectedPostulation] = useState(null);
  const [creatingContract, setCreatingContract] = useState(false);
  const [contractSuccess, setContractSuccess] = useState(false);
  // Employer folder view
  const [selectedOfferId, setSelectedOfferId] = useState(null);
  const [workerProfiles, setWorkerProfiles] = useState({}); // cache: workerId -> profile
  const [contractForm, setContractForm] = useState({
    startDate: '', endDate: '', agreedAmount: '', useOfferPrice: true
  });

  useEffect(() => {
    const loadUser = async () => {
      try {
        const data = await getCurrentUser();
        setUser(data);
        setRole(data.role);
      } catch (err) { console.error("Error cargando usuario", err); }
    };
    loadUser();
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const userData = await getCurrentUser();
        setUser(userData);
        if (role === "TRABAJADOR") {
          setPostulations(await getPostulationsByWorker());
        } else if (role === "EMPLEADOR") {
          setPostulations(await getPostulationsByEmployer(userData.id));
        }
      } catch (error) { console.error("Error cargando postulaciones", error); }
    };
    if (role) fetchData();
  }, [role]);

  const reloadPostulations = async () => {
    try {
      const userData = await getCurrentUser();
      if (role === "TRABAJADOR") setPostulations(await getPostulationsByWorker());
      else if (role === "EMPLEADOR") setPostulations(await getPostulationsByEmployer(userData.id));
    } catch (err) { console.error(err); }
  };

  // Group postulations by offer for employer
  const offerGroups = React.useMemo(() => {
    if (role !== "EMPLEADOR") return {};
    const groups = {};
    postulations.forEach(p => {
      const offerId = p.offer?.id;
      if (!offerId) return;
      if (!groups[offerId]) {
        groups[offerId] = { offer: p.offer, postulations: [] };
      }
      groups[offerId].postulations.push(p);
    });
    // Sort candidates within each group by calification score (desc)
    Object.values(groups).forEach(g => {
      g.postulations.sort((a, b) => (b.calification || 0) - (a.calification || 0));
    });
    return groups;
  }, [postulations, role]);

  const selectedGroup = selectedOfferId ? offerGroups[selectedOfferId] : null;

  const filteredData = postulations.filter(p => {
    if (activeFilter === 'Todas') return true;
    if (activeFilter === 'En Revisión') return p.status === 'PENDING';
    if (activeFilter === 'Aceptadas') return p.status === 'ACCEPTED';
    if (activeFilter === 'Finalizadas') return p.status === 'FINISHED';
    return true;
  });

  const stats = [
    { label: 'Total', count: postulations.length, color: 'text-primary' },
    { label: 'En Revisión', count: postulations.filter(p => p.status === 'PENDING').length, color: 'text-warning' },
    { label: 'Aceptadas', count: postulations.filter(p => p.status === 'ACCEPTED').length, color: 'text-success' },
    { label: 'Finalizadas', count: postulations.filter(p => p.status === 'FINISHED').length, color: 'text-info' }
  ];

  const filters = ['Todas', 'En Revisión', 'Aceptadas', 'Finalizadas'];

  const handleStatusChange = async (id, status) => {
    try {
      await updatePostulationStatus(id, status);
      setPostulations(prev => prev.map(p => p.id === id ? { ...p, status } : p));
    } catch (err) { console.error("Error actualizando estado", err); }
  };

  const openContractModal = (postulation) => {
    setSelectedPostulation(postulation);
    setContractForm({
      startDate: '', endDate: '',
      agreedAmount: Number(postulation.offer?.salaryRange || 0),
      useOfferPrice: true
    });
    setShowContractModal(true);
  };

  const handleCreateContract = async () => {
    if (creatingContract) return;
    try {
      if (!selectedPostulation) return;
      setCreatingContract(true);
      const payload = {
        id: null, startDate: contractForm.startDate, endDate: contractForm.endDate || null,
        agreedAmount: Number(contractForm.agreedAmount), status: "PENDING",
        postulation: {
          id: selectedPostulation.id, status: selectedPostulation.status,
          applicationDate: selectedPostulation.applicationDate,
          workerId: selectedPostulation.workerId,
          offer: selectedPostulation.offer, contractId: selectedPostulation.contractId
        },
      };
      await createContract(payload);
      try {
        await createNotification(selectedPostulation.workerId, "¡Contrato creado!",
          `Se ha creado un contrato para "${selectedPostulation.offer?.title}". Revisa tus contratos.`, "CONTRATO_GENERADO");
      } catch (e) { console.warn(e); }
      setPostulations(prev => prev.map(p => p.id === selectedPostulation.id ? { ...p, contractId: 999 } : p));
      setShowContractModal(false);
      setSelectedPostulation(null);
      setContractForm({ startDate: '', endDate: '', agreedAmount: '', useOfferPrice: true });
      setContractSuccess(true);
    } catch (err) { console.error("Error creando contrato", err);
    } finally { setCreatingContract(false); }
  };

  const handleViewCandidate = async (postulation) => {
    try {
      setProfileLoading(true);
      setSelectedCandidate(postulation);
      const profile = await getWorkerProfile(postulation.workerId);
      setCandidateProfile(profile);
      if (profile.userId) {
        const u = await getUserById(profile.userId);
        setCandidateUser(u);
        const wa = await getWhatsAppLink(profile.userId, `Hola ${u.firstName}, vi tu postulación en JobCol`);
        setWhatsAppLink(wa);
      }
      // Load reviews
      try {
        const reviews = await getReviewsByUser(postulation.workerId);
        setCandidateReviews(Array.isArray(reviews) ? reviews : []);
      } catch (e) { setCandidateReviews([]); }
    } catch (err) { console.error("Error cargando perfil", err);
    } finally { setProfileLoading(false); }
  };

  const closeCandidate = () => { setSelectedCandidate(null); setCandidateProfile(null); setCandidateUser(null); setWhatsAppLink(''); setCandidateReviews([]); };

  // Pre-fetch worker profiles when entering a folder
  const handleOpenFolder = async (offerId) => {
    setSelectedOfferId(Number(offerId));
    const group = offerGroups[offerId];
    if (!group) return;
    // Fetch profiles for workers we don't have yet
    for (const p of group.postulations) {
      if (!workerProfiles[p.workerId]) {
        try {
          const profile = await getWorkerProfile(p.workerId);
          setWorkerProfiles(prev => ({ ...prev, [p.workerId]: profile }));
        } catch (e) { /* silent */ }
      }
    }
  };

  const getWorkerRating = (workerId) => workerProfiles[workerId]?.averageRating || null;

  const getScoreColor = (score) => {
    if (score >= 80) return '#16a34a';
    if (score >= 60) return '#d97706';
    if (score >= 40) return '#ea580c';
    return '#dc2626';
  };

  // ====== RENDER ======
  return (
    <div className="apps-view-wrapper">
      <div className="page-header">
        <h1 className="page-title">{role === "EMPLEADOR" ? "Candidatos" : "Mis Postulaciones"}</h1>
        <p className="page-subtitle">{role === "EMPLEADOR" ? "Gestiona los candidatos de tus ofertas" : "Sigue el estado de tus postulaciones"}</p>
      </div>

      <div className="stats-grid">
        {stats.map(stat => (
          <div key={stat.label} className="stat-card">
            <h2 className={`stat-count ${stat.color}`}>{stat.count}</h2>
            <p className="stat-label">{stat.label}</p>
          </div>
        ))}
      </div>

      {/* ===== EMPLOYER: FOLDER VIEW ===== */}
      {role === "EMPLEADOR" && !selectedOfferId && (
        <div className="offer-folders">
          <h3 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '1rem', color: 'var(--text-main)' }}>
            Ofertas publicadas ({Object.keys(offerGroups).length})
          </h3>
          {Object.keys(offerGroups).length === 0 ? (
            <p style={{ color: 'var(--text-muted)' }}>No hay postulaciones aún</p>
          ) : (
            Object.entries(offerGroups).map(([offerId, group]) => (
              <div key={offerId} className="offer-folder-card" onClick={() => handleOpenFolder(offerId)}>
                <div className="folder-left">
                  <div className="folder-icon-wrapper"><FolderOpen size={22} /></div>
                  <div>
                    <h4 className="folder-title">{group.offer?.title || "Oferta"}</h4>
                    <p className="folder-meta">{group.offer?.category} · {group.offer?.location} · ${group.offer?.salaryRange?.toLocaleString('es-CO')}</p>
                  </div>
                </div>
                <div className="folder-right">
                  <span className="folder-count">{group.postulations.length} candidato{group.postulations.length !== 1 ? 's' : ''}</span>
                  <ChevronRight size={20} color="#94a3b8" />
                </div>
              </div>
            ))
          )}
        </div>
      )}

      {/* ===== EMPLOYER: INSIDE FOLDER ===== */}
      {role === "EMPLEADOR" && selectedOfferId && selectedGroup && (
        <div>
          <button className="back-btn" onClick={() => setSelectedOfferId(null)}>
            <ArrowLeft size={18} /> Volver a ofertas
          </button>
          <div style={{ background: '#f8fafc', borderRadius: '12px', padding: '1rem 1.25rem', marginBottom: '1.5rem', border: '1px solid var(--border-color)' }}>
            <h3 style={{ margin: 0, fontSize: '1.15rem', fontWeight: 700 }}>{selectedGroup.offer?.title}</h3>
            <p style={{ margin: '0.25rem 0 0', color: '#64748b', fontSize: '0.9rem' }}>
              {selectedGroup.offer?.category} · {selectedGroup.offer?.location} · ${selectedGroup.offer?.salaryRange?.toLocaleString('es-CO')}
            </p>
            <p style={{ margin: '0.5rem 0 0', fontSize: '0.85rem', color: '#94a3b8' }}>
              {selectedGroup.postulations.length} candidato{selectedGroup.postulations.length !== 1 ? 's' : ''} — Ordenados por calificación del test
            </p>
          </div>

          <div className="applications-list">
            {selectedGroup.postulations.map((p, idx) => (
              <div key={p.id} className="app-card">
                <div className="app-card-header">
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                    <span className="candidate-rank">#{idx + 1}</span>
                    <h3 className="app-title">Candidato #{p.workerId}</h3>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    {p.calification != null && (
                      <span className="test-score-badge" style={{ color: getScoreColor(p.calification) }}>
                        <Award size={14} /> {p.calification}%
                      </span>
                    )}
                    <span className={`badge ${p.status === 'PENDING' ? 'badge-warning' : p.status === 'ACCEPTED' ? 'badge-success' : p.status === 'REJECTED' ? 'badge-danger' : 'badge-secondary'}`}>
                      {p.status}
                    </span>
                  </div>
                </div>

                <div className="app-details">
                  <div className="detail-row">
                    <div className="detail-item"><Calendar size={16} /><span>{new Date(p.applicationDate).toLocaleDateString('es-CO')}</span></div>
                    {p.calification != null && (
                      <div className="detail-item"><Award size={14} color="#7c3aed" /><span>Test: <strong>{p.calification}%</strong></span></div>
                    )}
                    {getWorkerRating(p.workerId) != null && (
                      <div className="detail-item"><Star size={14} color="#fbbf24" fill="#fbbf24" /><span>Rating: <strong>{getWorkerRating(p.workerId).toFixed(1)}</strong>/5</span></div>
                    )}
                  </div>
                </div>

                <div className="app-actions" style={{ gap: '0.75rem' }}>
                  <Button variant="secondary" className="action-btn" onClick={() => handleViewCandidate(p)}>
                    <User size={16} /> Ver Perfil
                  </Button>
                  {p.status === "PENDING" && (
                    <Button variant="primary" className="action-btn" onClick={() => { handleStatusChange(p.id, "ACCEPTED"); }}>
                      Aceptar
                    </Button>
                  )}
                  {p.status === "ACCEPTED" && !p.contractId && (
                    <>
                      <Button variant="secondary" className="action-btn" onClick={() => handleStatusChange(p.id, "REJECTED")}>Cancelar</Button>
                      <Button variant="primary" className="action-btn" onClick={() => openContractModal(p)}>Crear Contrato</Button>
                    </>
                  )}
                  {p.contractId && (
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', background: '#dcfce7', color: '#166534', padding: '0.35rem 0.75rem', borderRadius: '8px', fontSize: '0.8rem', fontWeight: 600 }}>
                      <CheckCircle2 size={14} /> Contrato creado
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ===== WORKER VIEW (unchanged) ===== */}
      {role === "TRABAJADOR" && (
        <>
          <div className="filter-pills">
            {filters.map(filter => (
              <button key={filter} className={`pill ${activeFilter === filter ? 'active' : ''}`} onClick={() => setActiveFilter(filter)}>{filter}</button>
            ))}
          </div>
          <div className="applications-list">
            {filteredData.map(p => (
              <div key={p.id} className="app-card">
                <div className="app-card-header">
                  <h3 className="app-title">{p.offer?.title || "Oferta"}</h3>
                  <span className={`badge ${p.status === 'PENDING' ? 'badge-warning' : p.status === 'ACCEPTED' ? 'badge-success' : p.status === 'REJECTED' ? 'badge-danger' : 'badge-secondary'}`}>{p.status}</span>
                </div>
                <div className="app-details">
                  <div className="detail-item"><Building2 size={16} /><span>{p.offer?.category} • {p.offer?.location}</span></div>
                  <div className="detail-row">
                    <div className="detail-item"><Calendar size={16} /><span>{new Date(p.applicationDate).toLocaleDateString()}</span></div>
                    <div className="detail-item"><span>Salario: ${p.offer?.salaryRange}</span></div>
                  </div>
                </div>
                <div className="tracking-box">
                  <h4 className="tracking-title">Seguimiento</h4>
                  <div className="tracking-step"><div className="step-dot active"></div><span>Solicitud enviada</span></div>
                  {p.contractId && (<div className="tracking-step"><div className="step-dot active"></div><span>Contrato creado</span></div>)}
                </div>
                <div className="app-actions">
                  {p.contractId && (
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', background: '#dcfce7', color: '#166534', padding: '0.35rem 0.75rem', borderRadius: '8px', fontSize: '0.8rem', fontWeight: 600 }}>
                      <CheckCircle2 size={14} /> Ya tienes un contrato asociado
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        </>
      )}

      {/* ===== CANDIDATE PROFILE MODAL ===== */}
      {selectedCandidate && (
        <div className="candidate-modal-overlay">
          <div className="candidate-modal">
            <div className="candidate-modal-header">
              <div>
                <h2>{candidateUser ? `${candidateUser.firstName} ${candidateUser.lastName}` : "Candidato"}</h2>
                <p>Perfil del trabajador</p>
              </div>
              <button className="close-btn" onClick={closeCandidate}>✕</button>
            </div>
            {profileLoading ? (
              <div className="loading-profile">Cargando perfil...</div>
            ) : (
              <>
                <div className="candidate-profile-content">
                  {/* Test score highlight */}
                  {selectedCandidate.calification != null && (
                    <div style={{ background: 'linear-gradient(135deg, rgba(79,70,229,0.06), rgba(124,58,237,0.04))', borderRadius: '12px', padding: '1rem 1.25rem', border: '1px solid rgba(79,70,229,0.12)', display: 'flex', alignItems: 'center', gap: '1rem' }}>
                      <div style={{ width: 50, height: 50, borderRadius: '50%', background: `conic-gradient(${getScoreColor(selectedCandidate.calification)} ${selectedCandidate.calification}%, #e2e8f0 0)`, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                        <div style={{ width: 40, height: 40, borderRadius: '50%', background: 'white', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '0.75rem', fontWeight: 700, color: getScoreColor(selectedCandidate.calification) }}>
                          {selectedCandidate.calification}%
                        </div>
                      </div>
                      <div>
                        <div style={{ fontWeight: 700, fontSize: '0.95rem', color: 'var(--text-main)' }}>Calificación del Test</div>
                        <div style={{ fontSize: '0.8rem', color: '#64748b' }}>Evaluación adaptativa con IA</div>
                      </div>
                    </div>
                  )}
                  <div className="candidate-section">
                    <h4>Información General</h4>
                    <div className="candidate-info-grid">
                      <div><span>Nombre</span><strong>{candidateUser ? `${candidateUser.firstName} ${candidateUser.lastName}` : "N/A"}</strong></div>
                      <div><span>Correo</span><strong>{candidateUser?.email || "N/A"}</strong></div>
                      <div><span>Teléfono</span><strong>{candidateUser?.phone || "N/A"}</strong></div>
                      <div><span>Ubicación</span><strong>{candidateProfile?.location || "N/A"}</strong></div>
                    </div>
                  </div>
                  <div className="candidate-section"><h4>Experiencia</h4><p>{candidateProfile?.experience || "Sin experiencia registrada."}</p></div>
                  <div className="candidate-section">
                    <h4>Habilidades</h4>
                    <div className="skills-container">
                      {(candidateProfile?.skills?.split(",") || []).map(skill => (<span key={skill} className="skill-badge">{skill.trim()}</span>))}
                    </div>
                  </div>

                {/* Últimas reseñas */}
                <div className="candidate-section">
                  <h4>⭐ Reseñas del Candidato</h4>
                  {(() => {
                    const filteredReviews = candidateReviews.filter(
                      r => r.comment && r.comment.trim().length > 0
                    );
                    if (filteredReviews.length === 0) {
                      return (
                        <p style={{ color: '#94a3b8', fontSize: '0.9rem', fontStyle: 'italic' }}>
                          Este candidato aún no tiene reseñas con comentarios.
                        </p>
                      );
                    }
                    return (
                      <div className="candidate-reviews-list">
                        {filteredReviews.map((review, idx) => (
                          <div key={idx} className="candidate-review-card">
                            <div className="candidate-review-header">
                              <div className="candidate-review-stars">
                                {Array.from({ length: 5 }, (_, i) => (
                                  <Star key={i} size={14} fill={i < review.rating ? '#fbbf24' : 'none'} color={i < review.rating ? '#fbbf24' : '#d1d5db'} />
                                ))}
                              </div>
                              <span className="candidate-review-date">
                                {review.reviewDate ? new Date(review.reviewDate).toLocaleDateString('es-CO') : ''}
                              </span>
                            </div>
                            <p className="candidate-review-text">{review.comment}</p>
                            {review.authorType && (
                              <span className="candidate-review-author">
                                {review.authorType === 'EMPLEADOR' ? '👔 Empleador' : '🔧 Trabajador'}
                              </span>
                            )}
                          </div>
                        ))}
                      </div>
                    );
                  })()}
                </div>
                </div>
                <div className="candidate-modal-actions">
                  {candidateUser?.phone && (
                    <a href={whatsAppLink} target="_blank" rel="noopener noreferrer" style={{ textDecoration: 'none' }}>
                      <button className="whatsapp-btn">💬 Contactar WhatsApp</button>
                    </a>
                  )}
                  {selectedCandidate.status === "PENDING" && (
                    <>
                      <Button variant="primary" onClick={() => { handleStatusChange(selectedCandidate.id, "ACCEPTED"); closeCandidate(); }}>Aceptar</Button>
                      <Button variant="secondary" onClick={() => { handleStatusChange(selectedCandidate.id, "REJECTED"); closeCandidate(); }}>Rechazar</Button>
                    </>
                  )}
                </div>
              </>
            )}
          </div>
        </div>
      )}

      {/* ===== CONTRACT CREATION MODAL ===== */}
      {showContractModal && (
        <div className="candidate-modal-overlay">
          <div className="candidate-modal" style={{ position: 'relative' }}>
            {creatingContract && (
              <div style={{ position: 'absolute', inset: 0, background: 'rgba(255,255,255,0.92)', backdropFilter: 'blur(4px)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 10, borderRadius: '16px', flexDirection: 'column', gap: '0.75rem' }}>
                <Loader2 size={40} style={{ color: 'var(--primary)', animation: 'spin 1s linear infinite' }} />
                <p style={{ fontWeight: 600, margin: 0 }}>Creando contrato...</p>
              </div>
            )}
            <div className="candidate-modal-header">
              <div><h2>Crear Contrato</h2><p>Para: <strong>{selectedPostulation?.offer?.title}</strong></p></div>
              <button className="close-btn" onClick={() => { setShowContractModal(false); setSelectedPostulation(null); }}>✕</button>
            </div>
            <div className="candidate-profile-content">
              <div className="candidate-section">
                <h4>📅 Fechas del contrato</h4>
                <div className="candidate-info-grid">
                  <div><span style={{ fontSize: '0.8rem', fontWeight: 600 }}>Fecha de inicio *</span>
                    <input type="datetime-local" value={contractForm.startDate} onChange={e => setContractForm(p => ({ ...p, startDate: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '8px', border: '1px solid var(--border-color)', fontSize: '0.9rem' }} />
                  </div>
                  <div><span style={{ fontSize: '0.8rem', fontWeight: 600 }}>Fecha fin estimada</span>
                    <input type="datetime-local" value={contractForm.endDate} onChange={e => setContractForm(p => ({ ...p, endDate: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '8px', border: '1px solid var(--border-color)', fontSize: '0.9rem' }} />
                  </div>
                </div>
              </div>
              <div className="candidate-section">
                <h4>💰 Monto acordado</h4>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.75rem' }}>
                  <label style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', cursor: 'pointer', fontSize: '0.85rem' }}>
                    <div onClick={() => { const nv = !contractForm.useOfferPrice; setContractForm(p => ({ ...p, useOfferPrice: nv, agreedAmount: nv ? Number(selectedPostulation?.offer?.salaryRange || 0) : '' })); }}
                      style={{ width: 40, height: 22, borderRadius: 11, background: contractForm.useOfferPrice ? '#7c3aed' : '#d1d5db', position: 'relative', cursor: 'pointer', transition: 'all 0.2s' }}>
                      <div style={{ width: 18, height: 18, borderRadius: '50%', background: 'white', position: 'absolute', top: 2, left: contractForm.useOfferPrice ? 20 : 2, transition: 'all 0.2s', boxShadow: '0 1px 3px rgba(0,0,0,0.2)' }} />
                    </div>
                    Usar salario de la oferta
                  </label>
                </div>
                <input type="number" placeholder="Monto en COP" disabled={contractForm.useOfferPrice} value={contractForm.agreedAmount}
                  onChange={e => setContractForm(p => ({ ...p, agreedAmount: e.target.value }))}
                  style={{ width: '100%', padding: '0.6rem', borderRadius: '8px', border: '1px solid var(--border-color)', fontSize: '0.95rem', fontWeight: 600, background: contractForm.useOfferPrice ? '#f3f4f6' : 'white' }} />
              </div>
            </div>
            <div className="candidate-modal-actions">
              <Button variant="secondary" onClick={() => { setShowContractModal(false); setSelectedPostulation(null); }} disabled={creatingContract}>Cancelar</Button>
              <Button variant="primary" onClick={handleCreateContract} disabled={!contractForm.startDate || creatingContract}>{creatingContract ? 'Creando...' : 'Crear Contrato'}</Button>
            </div>
          </div>
        </div>
      )}

      {/* ===== SUCCESS MODAL ===== */}
      {contractSuccess && (
        <div className="candidate-modal-overlay">
          <div className="candidate-modal" style={{ maxWidth: 420, textAlign: 'center', padding: '2.5rem' }}>
            <div style={{ width: 72, height: 72, borderRadius: '50%', background: 'linear-gradient(135deg, #dcfce7, #bbf7d0)', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1.25rem' }}>
              <CheckCircle2 size={36} color="#16a34a" />
            </div>
            <h2 style={{ fontSize: '1.5rem', fontWeight: 700, marginBottom: '0.5rem' }}>¡Contrato creado!</h2>
            <p style={{ color: '#6b7280', fontSize: '0.95rem', marginBottom: '2rem', lineHeight: 1.6 }}>
              El contrato ha sido creado exitosamente. El trabajador recibirá una notificación.
            </p>
            <Button variant="primary" onClick={() => { setContractSuccess(false); reloadPostulations(); }} style={{ width: '100%' }}>Continuar</Button>
          </div>
        </div>
      )}
    </div>
  );
}