import React from 'react';
import { Building2, MapPin, Clock, DollarSign, Briefcase, Bookmark, Share2, Lock, Star } from 'lucide-react';
import Modal from './Modal';
import Button from './Button';
import { useToast } from '../context/ToastContext';
import './JobDetailsModal.css';

export default function JobDetailsModal({ isOpen, onClose, job, onApply, isSaved, onSave, applying, isApplied = false, isClosed = false, employerRating = null }) {
  const { showToast } = useToast();

  if (!job) return null;

  const handleApply = () => {
    if (isClosed) {
      showToast("Esta oferta se encuentra cerrada por el momento", "error");
      return;
    }
    if (isApplied) {
      showToast("Ya te has postulado a esta oferta", "info");
      return;
    }
    if (onApply) {
      onApply();
    }
    onClose();
  };

  const handleSave = () => {
    if (onSave) {
      onSave();
      if (!isSaved) {
        showToast('Empleo guardado en favoritos', 'info');
      } else {
        showToast('Empleo eliminado de favoritos', 'info');
      }
    }
  };

  const getButtonLabel = () => {
    if (isClosed) return "Oferta Cerrada";
    if (isApplied) return "Ya Postulado ✓";
    if (applying) return "Postulando...";
    return "Postularme Ahora";
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} className="job-details-modal">
      <div className="job-modal-header">
        <div className="job-modal-title-area">
          <h2 className="job-modal-title">{job.title}</h2>
          <div className="job-modal-company">
            <Building2 size={18} />
            <span>{job.company || 'Empresa Confidencial'}</span>
            {employerRating != null && (
              <span style={{
                display: 'inline-flex', alignItems: 'center', gap: '0.25rem',
                background: 'linear-gradient(135deg, #fef9c3, #fef3c7)',
                color: '#92400e', padding: '0.2rem 0.5rem', borderRadius: '6px',
                fontSize: '0.8rem', fontWeight: 700, marginLeft: '0.5rem',
                border: '1px solid #fde68a'
              }}>
                <Star size={13} fill="#f59e0b" color="#f59e0b" />
                {employerRating}
              </span>
            )}
          </div>
        </div>
        <div className="job-modal-actions-top">
          <button className="icon-btn" onClick={handleSave} title="Guardar">
            <Bookmark size={20} fill={isSaved ? "currentColor" : "none"} className={isSaved ? "text-blue-600" : ""} />
          </button>
          <button className="icon-btn" onClick={() => showToast('Enlace copiado', 'info')} title="Compartir">
            <Share2 size={20} />
          </button>
        </div>
      </div>

      {isClosed && (
        <div style={{
          background: '#fef2f2', border: '1px solid #fecaca', borderRadius: '8px',
          padding: '0.75rem 1rem', display: 'flex', alignItems: 'center', gap: '0.5rem',
          color: '#dc2626', fontSize: '0.9rem', fontWeight: 500, marginBottom: '0.5rem'
        }}>
          <Lock size={16} /> Esta oferta se encuentra cerrada y no acepta más postulaciones.
        </div>
      )}

      {isApplied && !isClosed && (
        <div style={{
          background: '#f0fdf4', border: '1px solid #bbf7d0', borderRadius: '8px',
          padding: '0.75rem 1rem', display: 'flex', alignItems: 'center', gap: '0.5rem',
          color: '#16a34a', fontSize: '0.9rem', fontWeight: 500, marginBottom: '0.5rem'
        }}>
          ✅ Ya te has postulado a esta oferta.
        </div>
      )}

      <div className="job-modal-meta">
        <div className="meta-badge"><Briefcase size={16} /> {job.type || 'Por Servicio'}</div>
        <div className="meta-badge"><MapPin size={16} /> {job.location || 'Ubicación no especificada'}</div>
        <div className="meta-badge font-semibold text-main"><DollarSign size={16} /> {job.salary || 'A convenir'}</div>
        <div className="meta-badge text-muted"><Clock size={16} /> Publicado {job.timePosted || 'recientemente'}</div>
      </div>

      <div className="job-modal-body">
        <div className="job-section">
          <h3>Descripción del Empleo</h3>
          <p>{job.description}</p>
        </div>

        <div className="job-section">
          <h3>Requisitos Adicionales</h3>
          <ul className="job-list">
            <li>Disponibilidad inmediata.</li>
            <li>Herramientas propias de trabajo.</li>
            <li>Experiencia demostrable o referencias de trabajos anteriores.</li>
            <li>Actitud de servicio y cumplimiento de horarios.</li>
          </ul>
        </div>

        <div className="job-section">
          <h3>Beneficios</h3>
          <ul className="job-list">
            <li>Pago puntual al finalizar la jornada/servicio.</li>
            <li>Oportunidad de contratos recurrentes basados en el desempeño.</li>
            <li>Ambiente de respeto y seguridad asegurado por JobCol.</li>
          </ul>
        </div>
      </div>

      <div className="job-modal-footer">
        <Button variant="secondary" onClick={onClose}>Cerrar</Button>
        <Button 
          variant={isApplied || isClosed ? "secondary" : "primary"} 
          onClick={handleApply} 
          disabled={applying || isApplied || isClosed}
        >
          {getButtonLabel()}
        </Button>
      </div>
    </Modal>
  );
}
