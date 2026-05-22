import React from 'react';
import { Bookmark, MapPin, Clock, Building2, Lock, Star } from 'lucide-react';
import Button from './Button';
import { useToast } from '../context/ToastContext';
import './JobCard.css';

export default function JobCard({ job, onClick, onApply, isSaved = false, onSave, applying = false, isApplied = false, isClosed = false, employerRating = null }) {
  const { showToast } = useToast();

  const handleApply = async (e) => {
    if (e) e.stopPropagation();

    if (isClosed) {
      showToast("Esta oferta se encuentra cerrada por el momento", "error");
      return;
    }

    if (isApplied) {
      showToast("Ya te has postulado a esta oferta", "info");
      return;
    }

    try {
      if (onApply) {
        await onApply();
      }
    } catch (err) {
      console.error("Error al postular", err);
      showToast("Error al postularte", "error");
    }
  };

  const handleSave = (e) => {
    if (e) e.stopPropagation();

    if (onSave) {
      onSave();
    }

    if (!isSaved) {
      showToast('Empleo guardado en tus favoritos', 'info');
    } else {
      showToast('Empleo eliminado de favoritos', 'info');
    }
  };

  const getButtonLabel = () => {
    if (isClosed) return "Oferta Cerrada";
    if (isApplied) return "Postulado ✓";
    if (applying) return "Postulando...";
    return "Postularme";
  };

  const getButtonVariant = () => {
    if (isClosed || isApplied) return "secondary";
    return "primary";
  };

  return (
    <div className={`job-card animate-fade-in cursor-pointer ${isClosed ? 'job-card-closed' : ''}`} onClick={onClick}>
      
      <div className="job-card-header">
        <div className="job-title-group">
          <h3 className="job-title">{job.title}</h3>
          <div className="job-company">
            <Building2 size={16} />
            <span>{job.company}</span>
            {employerRating != null && (
              <span className="employer-rating-badge">
                <Star size={12} fill="#fbbf24" color="#fbbf24" />
                {employerRating}
              </span>
            )}
          </div>
        </div>

        <div className="job-actions">
          {isClosed && (
            <span className="job-badge job-badge-closed"><Lock size={12} /> Cerrada</span>
          )}
          {!isClosed && (
            <span className="job-badge">{job.type}</span>
          )}

          <button 
            className="icon-btn" 
            onClick={handleSave}
            title={isSaved ? "Quitar de guardados" : "Guardar empleo"}
          >
            <Bookmark 
              size={20} 
              fill={isSaved ? "currentColor" : "none"} 
              className={isSaved ? "text-primary text-blue-600" : ""} 
            />
          </button>
        </div>
      </div>

      <p className="job-description">{job.description}</p>

      <div className="job-meta">
        <div className="meta-item">
          <MapPin size={16} />
          <span>{job.location}</span>
        </div>

        <div className="meta-item font-medium text-main">
          <span>{job.salary}</span>
        </div>

        <div className="meta-item">
          <Clock size={16} />
          <span>{job.timePosted}</span>
        </div>
      </div>

      <div className="job-footer">
        <Button 
          variant={getButtonVariant()} 
          onClick={handleApply}
          disabled={isApplied || applying || isClosed}
        >
          {getButtonLabel()}
        </Button>
      </div>

    </div>
  );
}