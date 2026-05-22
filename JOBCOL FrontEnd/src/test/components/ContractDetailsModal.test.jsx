import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import ContractDetailsModal from '../../components/ContractDetailsModal';

vi.mock('../../context/ToastContext', () => ({
  useToast: () => ({ showToast: vi.fn() }),
}));

const baseContract = (status = 'ACTIVE') => ({
  status,
  agreedAmount: 2500000,
  startDate: '2026-01-15',
  endDate: '2026-03-15',
  postulation: {
    offer: {
      title: 'Servicio de Plomería',
      category: 'Plomería',
      description: 'Reparación de tuberías',
    },
  },
});

const renderCDM = (status = 'ACTIVE', ov = {}) => {
  const p = { isOpen: true, onClose: vi.fn(), contract: baseContract(status), ...ov };
  return { ...render(<ContractDetailsModal {...p} />), props: p };
};

describe('ContractDetailsModal (15 pruebas)', () => {
  beforeEach(() => vi.clearAllMocks());

  it('CON-001: Retorna null sin contrato', () => {
    const { container } = render(<ContractDetailsModal isOpen={true} onClose={vi.fn()} contract={null} />);
    expect(container.innerHTML).toBe('');
  });

  it('CON-002: Muestra título', () => { renderCDM(); expect(screen.getByText('Servicio de Plomería')).toBeInTheDocument(); });
  it('CON-003: Badge PENDIENTE', () => { renderCDM('PENDING'); expect(screen.getByText('PENDIENTE')).toBeInTheDocument(); });
  it('CON-004: Badge ACTIVO', () => { renderCDM('ACTIVE'); expect(screen.getByText('ACTIVO')).toBeInTheDocument(); });
  it('CON-005: Badge FINALIZANDO', () => { renderCDM('PENDING_FINISH'); expect(screen.getByText('FINALIZANDO')).toBeInTheDocument(); });
  it('CON-006: Badge FINALIZADO', () => { renderCDM('FINISHED'); expect(screen.getByText('FINALIZADO')).toBeInTheDocument(); });
  it('CON-007: Badge RECHAZADO', () => { renderCDM('REJECTED'); expect(screen.getByText('RECHAZADO')).toBeInTheDocument(); });

  it('CON-008: Formato de fecha', () => {
    renderCDM();
    // The date should be formatted in es-CO locale (dd/mm/yyyy)
    const dateElements = screen.getAllByText(/\d{2}\/\d{2}\/\d{4}/);
    expect(dateElements.length).toBeGreaterThan(0);
  });

  it('CON-009: Fecha nula muestra "Sin fecha"', () => {
    const contract = { ...baseContract(), startDate: null, endDate: null };
    render(<ContractDetailsModal isOpen={true} onClose={vi.fn()} contract={contract} />);
    const sinFecha = screen.getAllByText('Sin fecha');
    expect(sinFecha.length).toBeGreaterThan(0);
  });

  it('CON-010: Formato de moneda COP', () => {
    renderCDM();
    // Should show COP-formatted amount
    const moneyEl = screen.getByText(/2\.500\.000|2,500,000|\$\s*2/);
    expect(moneyEl).toBeInTheDocument();
  });

  it('CON-011: Monto nulo muestra "$0"', () => {
    const contract = { ...baseContract(), agreedAmount: null };
    render(<ContractDetailsModal isOpen={true} onClose={vi.fn()} contract={contract} />);
    expect(screen.getByText(/\$\s*0/)).toBeInTheDocument();
  });

  it('CON-012: Timeline 5 pasos', () => {
    const { container } = renderCDM('ACTIVE');
    const steps = container.querySelectorAll('.c-timeline-item');
    expect(steps.length).toBe(5);
  });

  it('CON-013: Timeline rechazado - mensaje especial', () => {
    renderCDM('REJECTED');
    expect(screen.getByText(/Contrato Rechazado/)).toBeInTheDocument();
  });

  it('CON-014: Muestra categoría', () => { renderCDM(); expect(screen.getByText('Plomería')).toBeInTheDocument(); });

  it('CON-015: Botón cerrar', () => {
    const { props } = renderCDM();
    fireEvent.click(screen.getByText('Cerrar'));
    expect(props.onClose).toHaveBeenCalled();
  });
});
