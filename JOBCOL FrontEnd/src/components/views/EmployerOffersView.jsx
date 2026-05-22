import React, { useEffect, useState } from "react";
import {
  Plus,
  MapPin,
  Briefcase,
  DollarSign,
  Calendar,
  X,
  Pencil,
  Trash2,
  XCircle,
  Loader2
} from "lucide-react";

import Button from "../Button";

import {
  getMyOffers,
  createOffer,
  updateOffer,
  deleteOffer,
  closeOffer
} from "../../services/offerService";

import { getCurrentUser } from "../../services/authService";
import { useToast } from "../../context/ToastContext";

import "./EmployerOffersView.css";

const CATEGORIES = [
  'Plomería', 'Electricidad', 'Construcción', 'Aseo', 'Pintura',
  'Jardinería', 'Carpintería', 'Domicilios', 'Mecánica', 'Otro'
];

const LOCATIONS = [
  'Bogotá', 'Medellín', 'Cali', 'Barranquilla', 'Cartagena',
  'Bucaramanga', 'Pereira', 'Manizales', 'Santa Marta', 'Ibagué',
  'Cúcuta', 'Villavicencio', 'Pasto', 'Montería', 'Neiva'
];

export default function EmployerOffersView() {

  const { showToast } = useToast();
  const [offers, setOffers] = useState([]);
  const [loading, setLoading] = useState(true);

  const [showForm, setShowForm] = useState(false);
  const [editingOffer, setEditingOffer] = useState(null);
  const [confirmDelete, setConfirmDelete] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const [formData, setFormData] = useState({
    title: "",
    description: "",
    category: "",
    location: "",
    salaryRange: "",
  });

  useEffect(() => {
    loadOffers();
  }, []);

  const loadOffers = async () => {
    try {
      const user = await getCurrentUser();
      const data = await getMyOffers(user.id);
      // Ordenar por fecha de publicación descendente
      const sorted = [...data].sort((a, b) => {
        if (!a.publicationDate) return 1;
        if (!b.publicationDate) return -1;
        return new Date(b.publicationDate) - new Date(a.publicationDate);
      });
      setOffers(sorted);
    } catch (err) {
      console.error("Error cargando ofertas", err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData(prev => ({
      ...prev,
      [e.target.name]: e.target.value
    }));
  };

  const resetForm = () => {
    setFormData({
      title: "",
      description: "",
      category: "",
      location: "",
      salaryRange: "",
    });
    setEditingOffer(null);
  };

  const openCreateForm = () => {
    resetForm();
    setShowForm(true);
  };

  const openEditForm = (offer) => {
    setFormData({
      title: offer.title || "",
      description: offer.description || "",
      category: offer.category || "",
      location: offer.location || "",
      salaryRange: offer.salaryRange?.toString() || "",
    });
    setEditingOffer(offer);
    setShowForm(true);
  };

  const handleCreateOffer = async () => {
    if (submitting) return;
    try {
      setSubmitting(true);
      const user = await getCurrentUser();
      const payload = {
        ...formData,
        salaryRange: Number(formData.salaryRange),
        employerId: user.id,
        status: "OPEN"
      };

      const created = await createOffer(payload);
      setOffers(prev => [created, ...prev]);
      showToast("Oferta creada correctamente", "success");
      resetForm();
      setShowForm(false);
    } catch (err) {
      console.error("Error creando oferta", err);
      showToast("Error al crear oferta", "error");
    } finally {
      setSubmitting(false);
    }
  };

  const handleUpdateOffer = async () => {
    if (submitting) return;
    try {
      setSubmitting(true);
      const user = await getCurrentUser();
      const payload = {
        ...formData,
        salaryRange: Number(formData.salaryRange),
        employerId: user.id,
        status: editingOffer.status
      };

      const updated = await updateOffer(editingOffer.id, payload);
      setOffers(prev => prev.map(o => o.id === editingOffer.id ? updated : o));
      showToast("Oferta actualizada correctamente", "success");
      resetForm();
      setShowForm(false);
    } catch (err) {
      console.error("Error actualizando oferta", err);
      showToast("Error al actualizar oferta", "error");
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteOffer = async (id) => {
    try {
      await deleteOffer(id);
      setOffers(prev => prev.filter(o => o.id !== id));
      showToast("Oferta eliminada", "success");
      setConfirmDelete(null);
    } catch (err) {
      console.error("Error eliminando oferta", err);
      showToast("Error al eliminar oferta", "error");
    }
  };

  const handleCloseOffer = async (id) => {
    try {
      const updated = await closeOffer(id);
      setOffers(prev => prev.map(o => o.id === id ? updated : o));
      showToast("Oferta cerrada", "success");
    } catch (err) {
      console.error("Error cerrando oferta", err);
      showToast("Error al cerrar oferta", "error");
    }
  };

  const handleSubmitForm = () => {
    // Validate required fields
    if (!formData.title.trim()) {
      showToast("El título es obligatorio", "error");
      return;
    }
    if (!formData.category.trim()) {
      showToast("La categoría es obligatoria", "error");
      return;
    }
    if (!formData.description.trim()) {
      showToast("La descripción es obligatoria", "error");
      return;
    }
    if (!formData.location.trim()) {
      showToast("La ubicación es obligatoria", "error");
      return;
    }
    if (!formData.salaryRange || Number(formData.salaryRange) <= 0) {
      showToast("El pago estimado es obligatorio", "error");
      return;
    }

    if (editingOffer) {
      handleUpdateOffer();
    } else {
      handleCreateOffer();
    }
  };

  if (loading) return <p>Cargando ofertas...</p>;

  return (
    <div className="employer-offers-view">

      <div className="offers-header">
        <div>
          <h1 className="offers-title">Mis Ofertas</h1>
          <p className="offers-subtitle">
            Gestiona y publica nuevas oportunidades laborales
          </p>
        </div>

        <Button
          variant="primary"
          className="create-offer-btn"
          onClick={openCreateForm}
        >
          <Plus size={18} />
          Crear Oferta
        </Button>
      </div>


      {showForm && (
        <div className="offer-modal-overlay">
          <div className="offer-modal">

            <div className="offer-modal-header">
              <div>
                <h2>{editingOffer ? 'Editar Oferta' : 'Crear Nueva Oferta'}</h2>
                <p>Completa la información de la vacante</p>
              </div>

              <button
                className="close-btn"
                onClick={() => { setShowForm(false); resetForm(); }}
              >
                <X size={20} />
              </button>
            </div>

            <div className="offer-form-grid">

              <div className="form-group">
                <label>Título <span style={{color:'#ef4444'}}>*</span></label>
                <input
                  type="text"
                  name="title"
                  placeholder="Ej: Electricista residencial"
                  value={formData.title}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label>Categoría <span style={{color:'#ef4444'}}>*</span></label>
                <select
                  name="category"
                  value={formData.category}
                  onChange={handleChange}
                  className="dash-select"
                  style={{ width: '100%' }}
                >
                  <option value="">Selecciona una categoría</option>
                  {CATEGORIES.map(cat => (
                    <option key={cat} value={cat}>{cat}</option>
                  ))}
                </select>
              </div>

              <div className="form-group full-width">
                <label>Descripción <span style={{color:'#ef4444'}}>*</span></label>
                <textarea
                  name="description"
                  rows="5"
                  placeholder="Describe las tareas y requisitos..."
                  value={formData.description}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label>Ubicación <span style={{color:'#ef4444'}}>*</span></label>
                <select
                  name="location"
                  value={formData.location}
                  onChange={handleChange}
                  className="dash-select"
                  style={{ width: '100%' }}
                >
                  <option value="">Selecciona una ciudad</option>
                  {LOCATIONS.map(loc => (
                    <option key={loc} value={loc}>{loc}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Pago estimado (COP) <span style={{color:'#ef4444'}}>*</span></label>
                <input
                  type="number"
                  name="salaryRange"
                  placeholder="250000"
                  value={formData.salaryRange}
                  onChange={handleChange}
                />
              </div>

            </div>

            {submitting && (
              <div style={{
                position: 'absolute', inset: 0, background: 'rgba(255,255,255,0.9)',
                backdropFilter: 'blur(4px)', display: 'flex', alignItems: 'center',
                justifyContent: 'center', zIndex: 10, borderRadius: '16px',
                flexDirection: 'column', gap: '0.75rem'
              }}>
                <Loader2 size={36} style={{ color: 'var(--primary)', animation: 'spin 1s linear infinite' }} />
                <p style={{ fontWeight: 600, color: 'var(--text-main)', margin: 0 }}>
                  {editingOffer ? 'Guardando cambios...' : 'Publicando oferta...'}
                </p>
              </div>
            )}

            <div className="offer-modal-actions">
              <Button
                variant="secondary"
                onClick={() => { setShowForm(false); resetForm(); }}
                disabled={submitting}
              >
                Cancelar
              </Button>

              <Button
                variant="primary"
                onClick={handleSubmitForm}
                disabled={submitting}
              >
                {submitting ? 'Procesando...' : editingOffer ? 'Guardar Cambios' : 'Publicar Oferta'}
              </Button>
            </div>

          </div>
        </div>
      )}

      {/* Confirmación de eliminar */}
      {confirmDelete && (
        <div className="offer-modal-overlay">
          <div className="offer-modal" style={{ maxWidth: 400 }}>
            <div style={{ textAlign: 'center', padding: '1.5rem' }}>
              <div style={{
                width: 56, height: 56, borderRadius: '50%',
                background: '#fef2f2', display: 'flex', alignItems: 'center',
                justifyContent: 'center', margin: '0 auto 1rem'
              }}>
                <Trash2 size={28} color="#ef4444" />
              </div>
              <h3 style={{ margin: '0 0 0.5rem' }}>¿Eliminar oferta?</h3>
              <p style={{ color: '#6b7280', fontSize: '0.9rem', marginBottom: '1.5rem' }}>
                Esta acción no se puede deshacer. Se eliminará permanentemente la oferta "{confirmDelete.title}".
              </p>
              <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'center' }}>
                <Button variant="secondary" onClick={() => setConfirmDelete(null)}>
                  Cancelar
                </Button>
                <Button
                  variant="primary"
                  onClick={() => handleDeleteOffer(confirmDelete.id)}
                  style={{ background: '#ef4444' }}
                >
                  Eliminar
                </Button>
              </div>
            </div>
          </div>
        </div>
      )}


      {offers.length === 0 ? (

        <div className="empty-state">
          <Briefcase size={48} />
          <h3>No has creado ofertas aún</h3>
          <p>Publica tu primera oferta y encuentra trabajadores rápidamente.</p>
        </div>

      ) : (

        <div className="offers-grid">
          {offers.map(offer => (

            <div
              key={offer.id}
              className="offer-card"
            >

              <div className="offer-card-header">
                <div>
                  <h3>{offer.title}</h3>
                  <span className={`status-badge ${offer.status?.toLowerCase()}`}>
                    {offer.status}
                  </span>
                </div>
              </div>

              <p className="offer-description">
                {offer.description}
              </p>

              <div className="offer-meta">
                <div>
                  <MapPin size={16} />
                  {offer.location}
                </div>
                <div>
                  <Briefcase size={16} />
                  {offer.category}
                </div>
                <div>
                  <DollarSign size={16} />
                  ${offer.salaryRange?.toLocaleString()}
                </div>
                <div>
                  <Calendar size={16} />
                  {
                    offer.publicationDate
                      ? new Date(offer.publicationDate).toLocaleDateString()
                      : "Hoy"
                  }
                </div>
              </div>

              {/* Acciones CRUD */}
              <div className="offer-card-actions">
                <Button
                  variant="secondary"
                  className="btn-sm"
                  onClick={() => openEditForm(offer)}
                  style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.8rem', padding: '0.35rem 0.75rem' }}
                >
                  <Pencil size={14} /> Editar
                </Button>

                {offer.status === "OPEN" && (
                  <Button
                    variant="secondary"
                    className="btn-sm"
                    onClick={() => handleCloseOffer(offer.id)}
                    style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.8rem', padding: '0.35rem 0.75rem' }}
                  >
                    <XCircle size={14} /> Cerrar
                  </Button>
                )}

                <Button
                  variant="secondary"
                  className="btn-sm"
                  onClick={() => setConfirmDelete(offer)}
                  style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.8rem', padding: '0.35rem 0.75rem', color: '#ef4444' }}
                >
                  <Trash2 size={14} /> Eliminar
                </Button>
              </div>

            </div>
          ))}
        </div>
      )}

    </div>
  );
}