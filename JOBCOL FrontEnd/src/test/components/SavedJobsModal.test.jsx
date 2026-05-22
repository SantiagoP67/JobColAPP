import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import SavedJobsModal from '../../components/SavedJobsModal';

vi.mock('../../context/ToastContext', () => ({
  useToast: () => ({ showToast: vi.fn() }),
}));

const jobs = [
  { id: 1, title: 'Plomero', company: 'ABC', location: 'Bogotá', salary: '$1M' },
  { id: 2, title: 'Electricista', company: 'XYZ', location: 'Cali', salary: '$1.5M' },
];

const renderSJM = (ov = {}) => {
  const p = { isOpen: true, onClose: vi.fn(), savedJobs: jobs, onRemove: vi.fn(), onViewDetails: vi.fn(), ...ov };
  return { ...render(<SavedJobsModal {...p} />), props: p };
};

describe('SavedJobsModal (10 pruebas)', () => {
  beforeEach(() => vi.clearAllMocks());

  it('SAV-001: No renderiza si isOpen=false', () => {
    const { container } = render(<SavedJobsModal isOpen={false} onClose={vi.fn()} />);
    expect(container.innerHTML).toBe('');
  });

  it('SAV-002: Muestra título', () => { renderSJM(); expect(screen.getByText(/Mis Empleos Guardados/i)).toBeInTheDocument(); });

  it('SAV-003: Estado vacío', () => {
    renderSJM({ savedJobs: [] });
    expect(screen.getByText(/No tienes empleos guardados/i)).toBeInTheDocument();
  });

  it('SAV-004: Contador de empleos', () => {
    renderSJM();
    expect(screen.getByText(/Tienes 2 empleos guardados/i)).toBeInTheDocument();
  });

  it('SAV-005: Lista de empleos', () => {
    renderSJM();
    expect(screen.getByText('Plomero')).toBeInTheDocument();
    expect(screen.getByText('Electricista')).toBeInTheDocument();
  });

  it('SAV-006: Botón ver detalles', () => {
    const { props } = renderSJM();
    const btns = screen.getAllByText('Ver Detalles');
    fireEvent.click(btns[0]);
    expect(props.onViewDetails).toHaveBeenCalledWith(jobs[0]);
  });

  it('SAV-007: Botón eliminar', () => {
    const { props } = renderSJM();
    const delBtns = screen.getAllByTitle('Eliminar guardado');
    fireEvent.click(delBtns[0]);
    expect(props.onRemove).toHaveBeenCalledWith(1);
  });

  it('SAV-008: Muestra título empleo', () => { renderSJM(); expect(screen.getByText('Plomero')).toBeInTheDocument(); });
  it('SAV-009: Muestra empresa empleo', () => { renderSJM(); expect(screen.getByText('ABC')).toBeInTheDocument(); });

  it('SAV-010: Botón cerrar', () => {
    const { props } = renderSJM();
    fireEvent.click(screen.getByText('Cerrar'));
    expect(props.onClose).toHaveBeenCalled();
  });
});
