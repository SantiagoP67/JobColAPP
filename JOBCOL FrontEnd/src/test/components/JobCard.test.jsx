import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import JobCard from '../../components/JobCard';

vi.mock('../../context/ToastContext', () => ({
  useToast: () => ({ showToast: vi.fn() }),
}));

const mockJob = {
  id: 1,
  title: 'Plomero Profesional',
  company: 'Servicios ABC',
  location: 'Bogotá',
  salary: '$1.200.000/mes',
  type: 'Tiempo Completo',
  description: 'Se necesita plomero con experiencia',
  timePosted: 'Hace 2 días',
};

const renderJC = (ov = {}) => {
  const p = { job: mockJob, onClick: vi.fn(), onApply: vi.fn(), onSave: vi.fn(), isSaved: false, applying: false, isApplied: false, isClosed: false, ...ov };
  return { ...render(<JobCard {...p} />), props: p };
};

describe('JobCard (15 pruebas)', () => {
  beforeEach(() => vi.clearAllMocks());

  it('JOB-001: Componente se instancia', () => { const { container } = renderJC(); expect(container).toBeTruthy(); });
  it('JOB-002: Muestra título', () => { renderJC(); expect(screen.getByText('Plomero Profesional')).toBeInTheDocument(); });
  it('JOB-003: Muestra empresa', () => { renderJC(); expect(screen.getByText('Servicios ABC')).toBeInTheDocument(); });
  it('JOB-004: Muestra ubicación', () => { renderJC(); expect(screen.getByText('Bogotá')).toBeInTheDocument(); });
  it('JOB-005: Muestra salario', () => { renderJC(); expect(screen.getByText('$1.200.000/mes')).toBeInTheDocument(); });
  it('JOB-006: Muestra tiempo publicado', () => { renderJC(); expect(screen.getByText('Hace 2 días')).toBeInTheDocument(); });
  it('JOB-007: Botón Postularme visible', () => { renderJC(); expect(screen.getByText('Postularme')).toBeInTheDocument(); });
  it('JOB-008: Botón "Postulado ✓" si isApplied', () => { renderJC({ isApplied: true }); expect(screen.getByText('Postulado ✓')).toBeInTheDocument(); });
  it('JOB-009: Botón "Oferta Cerrada" si isClosed', () => { renderJC({ isClosed: true }); expect(screen.getByText('Oferta Cerrada')).toBeInTheDocument(); });

  it('JOB-010: Click en guardar invoca onSave', () => {
    const { props } = renderJC();
    const btn = screen.getByTitle(/Guardar empleo/i);
    fireEvent.click(btn);
    expect(props.onSave).toHaveBeenCalled();
  });

  it('JOB-011: Icono bookmark filled si isSaved', () => {
    const { container } = renderJC({ isSaved: true });
    const btn = screen.getByTitle(/Quitar de guardados/i);
    expect(btn).toBeInTheDocument();
  });

  it('JOB-012: Click en tarjeta invoca onClick', () => {
    const { props } = renderJC();
    const card = screen.getByText('Plomero Profesional').closest('.job-card');
    fireEvent.click(card);
    expect(props.onClick).toHaveBeenCalled();
  });

  it('JOB-013: Click en postular invoca onApply', async () => {
    const { props } = renderJC();
    fireEvent.click(screen.getByText('Postularme'));
    expect(props.onApply).toHaveBeenCalled();
  });

  it('JOB-014: Badge de tipo visible', () => { renderJC(); expect(screen.getByText('Tiempo Completo')).toBeInTheDocument(); });

  it('JOB-015: Badge cerrada visible si isClosed', () => {
    renderJC({ isClosed: true });
    const elements = screen.getAllByText(/Cerrada/);
    expect(elements.length).toBeGreaterThanOrEqual(1);
  });
});
