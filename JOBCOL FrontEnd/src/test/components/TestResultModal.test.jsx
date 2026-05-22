import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import TestResultModal from '../../components/TestResultModal';

vi.mock('../../context/ToastContext', () => ({
  useToast: () => ({ showToast: vi.fn() }),
}));

const passResult = { score: 75, details: [100, 80, 45], totalQuestions: 3 };
const failResult = { score: 30, details: [0, 40, 50], totalQuestions: 3 };

const renderTRM = (result = passResult, ov = {}) => {
  const p = { isOpen: true, onClose: vi.fn(), result, ...ov };
  return { ...render(<TestResultModal {...p} />), props: p };
};

describe('TestResultModal (10 pruebas)', () => {
  beforeEach(() => vi.clearAllMocks());

  it('TRM-001: Retorna null sin result', () => {
    const { container } = render(<TestResultModal isOpen={true} onClose={vi.fn()} result={null} />);
    expect(container.querySelector('.test-result-wrapper')).not.toBeInTheDocument();
  });

  it('TRM-002: Muestra score como porcentaje', () => {
    renderTRM();
    const scoreElements = screen.getAllByText(/75/);
    expect(scoreElements.length).toBeGreaterThan(0);
  });

  it('TRM-003: Título aprobado ≥50%', () => {
    renderTRM(passResult);
    expect(screen.getByText('¡Excelente!')).toBeInTheDocument();
  });

  it('TRM-004: Título reprobado <50%', () => {
    renderTRM(failResult);
    expect(screen.getByText('Sigue intentando')).toBeInTheDocument();
  });

  it('TRM-005: Mensaje aprobado', () => {
    renderTRM(passResult);
    expect(screen.getByText(/completado el test exitosamente/i)).toBeInTheDocument();
  });

  it('TRM-006: Mensaje reprobado', () => {
    renderTRM(failResult);
    expect(screen.getByText(/No alcanzaste el puntaje ideal/i)).toBeInTheDocument();
  });

  it('TRM-007: Desglose por pregunta', () => {
    renderTRM();
    expect(screen.getByText(/Pregunta 1/)).toBeInTheDocument();
    expect(screen.getByText(/Pregunta 2/)).toBeInTheDocument();
    expect(screen.getByText(/Pregunta 3/)).toBeInTheDocument();
  });

  it('TRM-008: Nivel básico en pregunta 1', () => {
    renderTRM();
    expect(screen.getByText(/básico/i)).toBeInTheDocument();
  });

  it('TRM-009: Promedio final visible', () => {
    renderTRM();
    expect(screen.getByText('Promedio Final')).toBeInTheDocument();
  });

  it('TRM-010: Botón Continuar invoca onClose', () => {
    const { props } = renderTRM();
    fireEvent.click(screen.getByText('Continuar'));
    expect(props.onClose).toHaveBeenCalled();
  });
});
