import React from 'react';
import { Bookmark, Building2, MapPin, Trash2 } from 'lucide-react';
import Modal from './Modal';
import Button from './Button';
import './SavedJobsModal.css';

export default function SavedJobsModal({ isOpen, onClose, savedJobs = [], onRemove, onViewDetails }) {
  if (!isOpen) return null;

  return (
    <Modal isOpen={isOpen} onClose={onClose} className="saved-jobs-modal">
      <div className="saved-jobs-header">
        <h2 className="saved-jobs-title">
          <Bookmark className="text-primary" size={24} /> 
          Mis Empleos Guardados
        </h2>
        <p className="saved-jobs-subtitle">
          {savedJobs.length > 0
            ? `Tienes ${savedJobs.length} empleo${savedJobs.length > 1 ? 's' : ''} guardado${savedJobs.length > 1 ? 's' : ''}`
            : 'Ofertas que marcaste para revisar más tarde'}
        </p>
      </div>

      <div className="saved-jobs-body">
        {savedJobs.length > 0 ? (
          <div className="saved-list">
            {savedJobs.map(job => (
              <div key={job.id} className="saved-job-card">
                <div className="saved-job-info">
                  <h3 className="saved-job-title">{job.title}</h3>
                  <div className="saved-job-meta">
                    <span><Building2 size={14}/> {job.company}</span>
                    <span><MapPin size={14}/> {job.location}</span>
                  </div>
                  <div className="saved-job-salary">{job.salary}</div>
                </div>
                <div className="saved-job-actions">
                  <Button
                    variant="primary"
                    className="btn-sm"
                    onClick={() => onViewDetails && onViewDetails(job)}
                  >
                    Ver Detalles
                  </Button>
                  <button
                    className="icon-btn text-danger ml-2"
                    title="Eliminar guardado"
                    onClick={() => onRemove && onRemove(job.id)}
                  >
                    <Trash2 size={18} />
                  </button>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="empty-saved-state">
            <Bookmark size={48} className="text-muted opacity-50 mb-4" />
            <h3>No tienes empleos guardados</h3>
            <p>Explora las ofertas y guarda las que te interesen para verlas aquí.</p>
          </div>
        )}
      </div>

      <div className="saved-jobs-footer">
        <Button variant="ghost" onClick={onClose} className="w-full">Cerrar</Button>
      </div>
    </Modal>
  );
}
