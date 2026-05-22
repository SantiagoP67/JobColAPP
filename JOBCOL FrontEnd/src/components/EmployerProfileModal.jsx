import React, { useState } from "react";
import { Briefcase, Building2, User } from "lucide-react";
import Modal from "./Modal";
import Input from "./Input";
import Button from "./Button";
import { createEmployerProfile } from "../services/employerProfileService";
import "./AuthModal.css";

const LOCATIONS = [
  'Bogotá', 'Medellín', 'Cali', 'Barranquilla', 'Cartagena',
  'Bucaramanga', 'Pereira', 'Manizales', 'Santa Marta', 'Ibagué',
  'Cúcuta', 'Villavicencio', 'Pasto', 'Montería', 'Neiva'
];

export default function EmployerProfileModal({ isOpen, onClose }) {

  const [companyName, setCompanyName] = useState("");
  const [description, setDescription] = useState("");
  const [location, setLocation] = useState("");
  const [isIndependent, setIsIndependent] = useState(false);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    setLoading(true);
    setError("");

    try {
      const payload = {
        companyName: isIndependent ? "Independiente" : companyName,
        description,
        location
      };

      const response = await createEmployerProfile(payload);

      onClose();
      window.location.href = "/dashboard";

    } catch (err) {
      console.error("Error:", err);

      const msg =
        err.response?.data?.message ||
        err.response?.data ||
        err.message ||
        "Error guardando perfil";

      setError(msg);

    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose}>

      <div className="auth-header">
        <div className="auth-logo-icon">
          <Briefcase size={24} color="white" />
        </div>
        <h2 className="auth-title">Perfil de Empleador</h2>
        <p style={{ color: 'var(--text-muted, #6b7280)', fontSize: '0.9rem', marginTop: '0.25rem' }}>
          Configura tu perfil para empezar a publicar ofertas
        </p>
      </div>

      <form onSubmit={handleSubmit} className="auth-form">

        {/* Tipo de empleador - Cards visuales */}
        <div style={{ marginBottom: '0.5rem' }}>
          <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.6rem', color: 'var(--text-primary, #333)' }}>
            Tipo de empleador
          </label>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
            
            {/* Card Empresa */}
            <div
              onClick={() => setIsIndependent(false)}
              style={{
                border: `2px solid ${!isIndependent ? '#7c3aed' : 'var(--border-color, #e5e7eb)'}`,
                borderRadius: '12px',
                padding: '1rem',
                cursor: 'pointer',
                transition: 'all 0.2s ease',
                background: !isIndependent ? '#f5f3ff' : 'white',
                boxShadow: !isIndependent ? '0 0 0 3px rgba(124, 58, 237, 0.1)' : 'none',
                textAlign: 'center'
              }}
            >
              <div style={{
                width: 44,
                height: 44,
                borderRadius: '12px',
                background: !isIndependent ? 'linear-gradient(135deg, #7c3aed, #a855f7)' : '#e5e7eb',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                margin: '0 auto 0.6rem',
                transition: 'all 0.2s ease'
              }}>
                <Building2 size={22} color={!isIndependent ? 'white' : '#6b7280'} />
              </div>
              <div style={{ fontWeight: 600, fontSize: '0.9rem', color: !isIndependent ? '#7c3aed' : 'var(--text-primary, #333)' }}>
                Empresa
              </div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted, #6b7280)', marginTop: '0.15rem' }}>
                Represento una empresa
              </div>
            </div>

            {/* Card Independiente */}
            <div
              onClick={() => setIsIndependent(true)}
              style={{
                border: `2px solid ${isIndependent ? '#7c3aed' : 'var(--border-color, #e5e7eb)'}`,
                borderRadius: '12px',
                padding: '1rem',
                cursor: 'pointer',
                transition: 'all 0.2s ease',
                background: isIndependent ? '#f5f3ff' : 'white',
                boxShadow: isIndependent ? '0 0 0 3px rgba(124, 58, 237, 0.1)' : 'none',
                textAlign: 'center'
              }}
            >
              <div style={{
                width: 44,
                height: 44,
                borderRadius: '12px',
                background: isIndependent ? 'linear-gradient(135deg, #7c3aed, #a855f7)' : '#e5e7eb',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                margin: '0 auto 0.6rem',
                transition: 'all 0.2s ease'
              }}>
                <User size={22} color={isIndependent ? 'white' : '#6b7280'} />
              </div>
              <div style={{ fontWeight: 600, fontSize: '0.9rem', color: isIndependent ? '#7c3aed' : 'var(--text-primary, #333)' }}>
                Independiente
              </div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted, #6b7280)', marginTop: '0.15rem' }}>
                Trabajo por cuenta propia
              </div>
            </div>

          </div>
        </div>

        {!isIndependent && (
          <Input
            label="Nombre de la empresa"
            value={companyName}
            onChange={(e) => setCompanyName(e.target.value)}
            required
            placeholder="Ej: Constructora ABC"
          />
        )}

        <div>
          <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.35rem', color: 'var(--text-primary, #333)' }}>
            Descripción
          </label>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Describe brevemente a qué te dedicas..."
            rows={3}
            style={{
              width: '100%',
              padding: '0.65rem 0.75rem',
              border: '1px solid var(--border-color, #e5e7eb)',
              borderRadius: '8px',
              fontSize: '0.9rem',
              fontFamily: 'inherit',
              resize: 'none',
              background: 'var(--bg-secondary, #f8f9fc)'
            }}
          />
        </div>

        <div>
          <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.35rem', color: 'var(--text-primary, #333)' }}>
            Ubicación
          </label>
          <select
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            required
            className="dash-select"
            style={{ width: '100%', padding: '0.65rem 0.75rem' }}
          >
            <option value="">Selecciona tu ciudad</option>
            {LOCATIONS.map(loc => (
              <option key={loc} value={loc}>{loc}</option>
            ))}
          </select>
        </div>

        {error && <p style={{ color: "red", fontSize: '0.85rem' }}>{error}</p>}

        <Button type="submit" variant="primary" className="w-full" disabled={loading}>
          {loading ? "Guardando..." : "Crear Perfil de Empleador"}
        </Button>

      </form>
    </Modal>
  );
}