import React from 'react';
import { CheckCircle, XCircle } from 'lucide-react';
import Modal from './Modal';
import Button from './Button';
import './TestResultModal.css';

export default function TestResultModal({ isOpen, onClose, result }) {
  if (!result) return null;

  const { score, details, totalQuestions } = result;
  const passed = score >= 50;
  const circumference = 2 * Math.PI * 42;
  const offset = circumference - (score / 100) * circumference;

  return (
    <Modal isOpen={isOpen} onClose={onClose} className="test-result-wrapper">

      <div className={`test-result-icon ${passed ? 'passed' : 'failed'}`}>
        {passed
          ? <CheckCircle size={36} color="white" />
          : <XCircle size={36} color="white" />
        }
      </div>

      <h2 className="test-result-title">
        {passed ? '¡Excelente!' : 'Sigue intentando'}
      </h2>

      <p className="test-result-subtitle">
        {passed
          ? 'Has completado el test exitosamente. Tu postulación ha sido registrada.'
          : 'No alcanzaste el puntaje ideal, pero tu postulación fue registrada igualmente.'}
      </p>

      <div className="test-result-score-container">
        <div className="test-result-score-circle">
          <svg width="100" height="100" viewBox="0 0 100 100">
            <circle className="score-bg" cx="50" cy="50" r="42" />
            <circle
              className={`score-fill ${passed ? 'passed' : 'failed'}`}
              cx="50" cy="50" r="42"
              strokeDasharray={circumference}
              strokeDashoffset={offset}
            />
          </svg>
          <span className="test-result-score-text">{score}%</span>
        </div>
      </div>

      {/* Per-question breakdown */}
      {details && details.length > 0 && (
        <div className="test-result-details">
          {details.map((qScore, i) => (
            <div className="test-result-detail-row" key={i}>
              <span className="test-result-detail-label">
                Pregunta {i + 1}
                {i === 0 && ' (básico)'}
                {i === 1 && ' (intermedio)'}
                {i === 2 && ' (avanzado)'}
              </span>
              <span className="test-result-detail-value" style={{ color: qScore >= 60 ? '#10b981' : '#ef4444', fontWeight: 700 }}>
                {qScore}% {qScore >= 60 ? '✅' : '❌'}
              </span>
            </div>
          ))}
          <div className="test-result-detail-row" style={{ borderTop: '1px solid #e5e7eb', paddingTop: '0.75rem', marginTop: '0.25rem' }}>
            <span className="test-result-detail-label" style={{ fontWeight: 700 }}>Promedio Final</span>
            <span className="test-result-detail-value" style={{ color: passed ? '#10b981' : '#ef4444', fontWeight: 700, fontSize: '1.1rem' }}>
              {score}%
            </span>
          </div>
        </div>
      )}

      <div className="test-result-actions">
        <Button variant="primary" onClick={onClose}>
          Continuar
        </Button>
      </div>

    </Modal>
  );
}
