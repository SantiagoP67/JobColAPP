import React, { useState } from "react";
import { Briefcase } from "lucide-react";
import Modal from "./Modal";
import Input from "./Input";
import Button from "./Button";
import { createProfile } from "../services/profileService";
import "./AuthModal.css"; // reutilizamos estilos

const LOCATIONS = [
  'Bogotá', 'Medellín', 'Cali', 'Barranquilla', 'Cartagena',
  'Bucaramanga', 'Pereira', 'Manizales', 'Santa Marta', 'Ibagué',
  'Cúcuta', 'Villavicencio', 'Pasto', 'Montería', 'Neiva'
];

const SKILL_OPTIONS = [
  'Plomería', 'Electricidad', 'Construcción', 'Aseo', 'Pintura',
  'Jardinería', 'Carpintería', 'Domicilios', 'Mecánica', 'Cocina',
  'Soldadura', 'Albañilería', 'Cerrajería', 'Tapicería'
];

export default function CreateProfileModal({ isOpen, onClose }) {

  const [selectedSkills, setSelectedSkills] = useState([]);
  const [experience, setExperience] = useState("");
  const [location, setLocation] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const toggleSkill = (skill) => {
    setSelectedSkills(prev =>
      prev.includes(skill)
        ? prev.filter(s => s !== skill)
        : [...prev, skill]
    );
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (selectedSkills.length === 0) {
      setError("Selecciona al menos una habilidad");
      return;
    }

    if (!location) {
      setError("Selecciona tu ubicación");
      return;
    }

    setLoading(true);

    try {
      await createProfile({
        skills: selectedSkills.join(", "),
        experience,
        location,
        visible: true
      });

      onClose();
      window.location.href = "/dashboard";

    } catch (err) {
      console.error(err);
      setError("Error al crear el perfil");
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
        <h2 className="auth-title">Crea tu perfil</h2>
        <p style={{ color: 'var(--text-muted, #6b7280)', fontSize: '0.9rem', marginTop: '0.25rem' }}>
          Completa tu información para comenzar a postularte
        </p>
      </div>

      <form onSubmit={handleSubmit} className="auth-form">

        {/* Habilidades como chips seleccionables */}
        <div>
          <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.5rem', color: 'var(--text-primary, #333)' }}>
            Habilidades <span style={{ color: '#6b7280', fontWeight: 400 }}>({selectedSkills.length} seleccionadas)</span>
          </label>
          <div style={{
            display: 'flex',
            flexWrap: 'wrap',
            gap: '0.4rem'
          }}>
            {SKILL_OPTIONS.map(skill => (
              <button
                key={skill}
                type="button"
                onClick={() => toggleSkill(skill)}
                style={{
                  padding: '0.35rem 0.75rem',
                  borderRadius: '20px',
                  border: `1.5px solid ${selectedSkills.includes(skill) ? '#7c3aed' : 'var(--border-color, #d1d5db)'}`,
                  background: selectedSkills.includes(skill) ? '#f5f3ff' : 'white',
                  color: selectedSkills.includes(skill) ? '#7c3aed' : 'var(--text-primary, #333)',
                  fontSize: '0.8rem',
                  fontWeight: selectedSkills.includes(skill) ? 600 : 400,
                  cursor: 'pointer',
                  transition: 'all 0.15s ease'
                }}
              >
                {selectedSkills.includes(skill) ? '✓ ' : ''}{skill}
              </button>
            ))}
          </div>
        </div>

        {/* Experiencia */}
        <div>
          <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.35rem', color: 'var(--text-primary, #333)' }}>
            Experiencia
          </label>
          <textarea
            value={experience}
            onChange={(e) => setExperience(e.target.value)}
            placeholder="Describe brevemente tu experiencia laboral..."
            rows={3}
            required
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

        {/* Ubicación */}
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

        <Button
          type="submit"
          variant="primary"
          className="w-full mt-4"
          disabled={loading}
        >
          {loading ? "Creando..." : "Crear Perfil"}
        </Button>

      </form>

    </Modal>
  );
}