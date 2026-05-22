import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import JobDetailsModal from '../../components/JobDetailsModal';

vi.mock('../../context/ToastContext', () => ({
  useToast: () => ({ showToast: vi.fn() }),
}));

const mockJob = {
  title: 'Electricista Industrial',
  company: 'ElectroCol',
  location: 'Medellín',
  salary: '$2.500.000/mes',
  type: 'Tiempo Completo',
  description: 'Se requiere electricista certificado',
  timePosted: 'Hace 1 día',
};

const renderJDM = (ov = {}) => {
  const p = { isOpen: true, onClose: vi.fn(), job: mockJob, onApply: vi.fn(), isSaved: false, onSave: vi.fn(), applying: false, isApplied: false, isClosed: false, ...ov };
  return { ...render(<JobDetailsModal {...p} />), props: p };
};

describe('JobDetailsModal (14 pruebas)', () => {
  beforeEach(() => vi.clearAllMocks());

  it('JDM-001: Retorna null sin job', () => { const { container } = render(<JobDetailsModal isOpen={true} onClose={vi.fn()} job={null} />); expect(container.innerHTML).toBe(''); });
  it('JDM-002: Muestra título', () => { renderJDM(); expect(screen.getByText('Electricista Industrial')).toBeInTheDocument(); });
  it('JDM-003: Muestra empresa', () => { renderJDM(); expect(screen.getByText('ElectroCol')).toBeInTheDocument(); });
  it('JDM-004: Muestra ubicación', () => { renderJDM(); expect(screen.getByText('Medellín')).toBeInTheDocument(); });
  it('JDM-005: Muestra salario', () => { renderJDM(); expect(screen.getByText(/2\.500\.000/)).toBeInTheDocument(); });
  it('JDM-006: Muestra descripción', () => { renderJDM(); expect(screen.getByText('Se requiere electricista certificado')).toBeInTheDocument(); });
  it('JDM-007: Botón "Postularme Ahora"', () => { renderJDM(); expect(screen.getByText('Postularme Ahora')).toBeInTheDocument(); });
  it('JDM-008: Botón "Ya Postulado ✓"', () => { renderJDM({ isApplied: true }); expect(screen.getByText('Ya Postulado ✓')).toBeInTheDocument(); });

  it('JDM-009: Banner oferta cerrada', () => {
    renderJDM({ isClosed: true });
    expect(screen.getByText(/cerrada y no acepta/i)).toBeInTheDocument();
  });

  it('JDM-010: Banner ya postulado', () => {
    renderJDM({ isApplied: true });
    expect(screen.getByText(/Ya te has postulado/i)).toBeInTheDocument();
  });

  it('JDM-011: Click guardar invoca onSave', () => {
    const { props } = renderJDM();
    fireEvent.click(screen.getByTitle('Guardar'));
    expect(props.onSave).toHaveBeenCalled();
  });

  it('JDM-012: Click postularme invoca onApply y onClose', () => {
    const { props } = renderJDM();
    fireEvent.click(screen.getByText('Postularme Ahora'));
    expect(props.onApply).toHaveBeenCalled();
    expect(props.onClose).toHaveBeenCalled();
  });

  it('JDM-013: Click cerrar invoca onClose', () => {
    const { props } = renderJDM();
    fireEvent.click(screen.getByText('Cerrar'));
    expect(props.onClose).toHaveBeenCalled();
  });

  it('JDM-014: Secciones de requisitos y beneficios', () => {
    renderJDM();
    expect(screen.getByText('Requisitos Adicionales')).toBeInTheDocument();
    expect(screen.getByText('Beneficios')).toBeInTheDocument();
  });
});
