import React from 'react';
import {
  Calendar,
  DollarSign,
  Briefcase,
  CheckCircle,
  Clock,
  AlertCircle,
  XCircle
} from 'lucide-react';

import Modal from './Modal';
import Button from './Button';
import './ContractDetailsModal.css';

// 🔥 Formateo de fecha
const formatDate = (date) => {
  if (!date) return "Sin fecha";

  return new Date(date).toLocaleDateString("es-CO", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric"
  });
};

// 💰 Formato moneda
const formatMoney = (amount) => {
  if (!amount) return "$0";
  return amount.toLocaleString("es-CO", {
    style: "currency",
    currency: "COP"
  });
};

// Timeline steps dinámicos según status
const getTimelineSteps = (status) => {
  const steps = [
    {
      label: 'Contrato Creado',
      description: 'El empleador propuso el contrato.',
      icon: Briefcase
    },
    {
      label: 'Contrato Aceptado',
      description: 'Ambas partes firmaron el acuerdo.',
      icon: CheckCircle
    },
    {
      label: 'Servicio en Progreso',
      description: 'El trabajador está realizando la labor.',
      icon: Clock
    },
    {
      label: 'Solicitud de Finalización',
      description: 'Una de las partes solicitó finalizar.',
      icon: AlertCircle
    },
    {
      label: 'Contrato Finalizado',
      description: 'El servicio fue completado y calificado.',
      icon: CheckCircle
    }
  ];

  const statusIndex = {
    'PENDING': 0,
    'ACTIVE': 2,
    'PENDING_FINISH': 3,
    'FINISHED': 4,
    'REJECTED': -1
  };

  const currentIdx = statusIndex[status] ?? -1;

  return steps.map((step, i) => ({
    ...step,
    status: currentIdx === -1 
      ? 'rejected' 
      : i < currentIdx 
        ? 'completed' 
        : i === currentIdx 
          ? 'active' 
          : 'pending'
  }));
};

export default function ContractDetailsModal({ isOpen, onClose, contract }) {
  if (!contract) return null;

  const status = contract.status;
  const offer = contract.postulation?.offer;
  const timelineSteps = getTimelineSteps(status);

  const getStatusBadge = () => {
    switch (status) {
      case "PENDING":
        return <span className="badge badge-warning">PENDIENTE</span>;
      case "ACTIVE":
        return <span className="badge badge-success">ACTIVO</span>;
      case "PENDING_FINISH":
        return <span className="badge badge-info">FINALIZANDO</span>;
      case "FINISHED":
        return <span className="badge badge-secondary">FINALIZADO</span>;
      case "REJECTED":
        return <span className="badge badge-danger">RECHAZADO</span>;
      default:
        return <span className="badge">DESCONOCIDO</span>;
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} className="contract-modal">

      {/* HEADER */}
      <div className="contract-modal-header">
        <h2 className="contract-modal-title">
          {offer?.title || contract.title || 'Detalles del Contrato'}
        </h2>
        {getStatusBadge()}
      </div>

      {/* BODY */}
      <div className="contract-modal-body">

        {/* META INFO */}
        <div className="contract-meta-grid">

          <div className="c-meta-item">
            <span className="c-meta-label">
              <Briefcase size={16}/> Categoría
            </span>
            <span className="c-meta-value">
              {offer?.category || 'Sin categoría'}
            </span>
          </div>

          <div className="c-meta-item">
            <span className="c-meta-label">
              <DollarSign size={16}/> Monto Acordado
            </span>
            <span className="c-meta-value font-bold">
              {formatMoney(contract.agreedAmount)}
            </span>
          </div>

          <div className="c-meta-item">
            <span className="c-meta-label">
              <Calendar size={16}/> Fecha Inicio
            </span>
            <span className="c-meta-value">
              {formatDate(contract.startDate)}
            </span>
          </div>

          <div className="c-meta-item">
            <span className="c-meta-label">
              <Calendar size={16}/> Fecha Fin Estimada
            </span>
            <span className="c-meta-value">
              {formatDate(contract.endDate)}
            </span>
          </div>

        </div>

        {/* DESCRIPCIÓN */}
        <div className="contract-section">
          <h3>Descripción del Servicio Acordado</h3>
          <p>{offer?.description || contract.description || 'Sin descripción detallada.'}</p>
        </div>

        {/* TIMELINE DINÁMICA */}
        <div className="contract-section">
          <h3>Timeline del Contrato</h3>

          {status === "REJECTED" ? (
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.75rem',
              padding: '1rem',
              background: '#fef2f2',
              borderRadius: '10px',
              border: '1px solid #fecaca'
            }}>
              <XCircle size={24} color="#ef4444" />
              <div>
                <div style={{ fontWeight: 600, color: '#ef4444' }}>Contrato Rechazado</div>
                <div style={{ fontSize: '0.85rem', color: '#6b7280' }}>
                  Este contrato fue rechazado por una de las partes.
                </div>
              </div>
            </div>
          ) : (
            <div className="c-timeline">
              {timelineSteps.map((step, index) => {
                const IconComponent = step.icon;
                return (
                  <div key={index} className={`c-timeline-item ${step.status}`}>
                    <div className="c-timeline-dot">
                      {step.status === 'completed' && <CheckCircle size={14} />}
                      {step.status === 'active' && (
                        <div style={{
                          width: 8,
                          height: 8,
                          borderRadius: '50%',
                          background: '#7c3aed',
                          animation: 'pulse 1.5s infinite'
                        }} />
                      )}
                    </div>
                    <div className="c-timeline-content">
                      <h4>{step.label}</h4>
                      <p>{step.description}</p>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

      </div>

      <div className="contract-modal-footer">
        <Button variant="ghost" onClick={onClose}>
          Cerrar
        </Button>
      </div>

    </Modal>
  );
}