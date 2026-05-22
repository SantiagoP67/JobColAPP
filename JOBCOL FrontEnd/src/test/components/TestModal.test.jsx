import React from 'react';
import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import TestModal from '../../components/TestModal';

vi.mock('../../services/aiTestService', () => ({
  generateQuestion: vi.fn(),
  evaluateOpenAnswer: vi.fn(),
}));
vi.mock('../../context/ToastContext', () => ({
  useToast: () => ({ showToast: vi.fn() }),
}));

import { generateQuestion, evaluateOpenAnswer } from '../../services/aiTestService';

const closedQ = {
  question: '¿Cuál es la herramienta más segura para cortar madera?',
  type: 'closed',
  options: ['Sierra circular con guarda', 'Cuchillo', 'Machete', 'Tijeras'],
  correctIndex: 0,
};
const openQ = {
  question: '¿Cómo verificas la seguridad eléctrica?',
  type: 'open',
  rubric: 'Debe mencionar multímetro',
};

const renderTM = (ov = {}) => {
  const p = { isOpen: true, onClose: vi.fn(), onComplete: vi.fn(), jobCategory: 'Electricidad', ...ov };
  return { ...render(<TestModal {...p} />), props: p };
};

describe('TestModal (20 pruebas)', () => {
  beforeEach(() => { vi.clearAllMocks(); vi.useFakeTimers({ shouldAdvanceTime: true }); generateQuestion.mockResolvedValue(closedQ); });
  afterEach(() => { vi.useRealTimers(); });

  it('TST-001: Componente se instancia', async () => { const { container } = renderTM(); await waitFor(() => expect(container).toBeTruthy()); });
  it('TST-002: Muestra carga inicial', () => { generateQuestion.mockReturnValue(new Promise(() => {})); renderTM(); expect(screen.getByText(/Generando pregunta/i)).toBeInTheDocument(); });
  it('TST-003: Sin feedback inicial', async () => { renderTM(); await waitFor(() => expect(screen.getByText(closedQ.question)).toBeInTheDocument()); expect(screen.queryByText(/Respuesta correcta/)).not.toBeInTheDocument(); });
  it('TST-004: Botón habilitado tras selección', async () => { renderTM(); await waitFor(() => expect(screen.getByText(closedQ.question)).toBeInTheDocument()); fireEvent.click(screen.getByText('Sierra circular con guarda')); expect(screen.getByText('Enviar Respuesta')).not.toBeDisabled(); });
  it('TST-005: Muestra "de 3"', async () => { renderTM(); await waitFor(() => expect(screen.getByText(closedQ.question)).toBeInTheDocument()); expect(screen.getByText(/de 3/)).toBeInTheDocument(); });
  it('TST-006: Título visible', () => { renderTM(); expect(screen.getByText('Test de Aptitud con IA')).toBeInTheDocument(); });
  it('TST-007: Categoría visible', () => { renderTM({ jobCategory: 'Plomería' }); expect(screen.getByText(/Plomería/)).toBeInTheDocument(); });
  it('TST-008: Barra progreso', () => { const { container } = renderTM(); expect(container.querySelector('.test-progress-bar')).toBeInTheDocument(); });
  it('TST-009: Llama generateQuestion', async () => { renderTM(); await waitFor(() => expect(generateQuestion).toHaveBeenCalledWith('Electricidad', 1, [])); });
  it('TST-010: 4 opciones', async () => { renderTM(); await waitFor(() => expect(screen.getByText(closedQ.question)).toBeInTheDocument()); closedQ.options.forEach(o => expect(screen.getByText(o)).toBeInTheDocument()); });
  it('TST-011: Seleccionar opción', async () => { const { container } = renderTM(); await waitFor(() => expect(screen.getByText(closedQ.question)).toBeInTheDocument()); fireEvent.click(screen.getByText('Sierra circular con guarda')); expect(screen.getByText('Sierra circular con guarda').closest('.test-option')).toHaveClass('selected'); });
  it('TST-012: Textarea para open', async () => { generateQuestion.mockResolvedValue(openQ); renderTM(); await waitFor(() => expect(screen.getByText(openQ.question)).toBeInTheDocument()); expect(screen.getByPlaceholderText(/Escribe tu respuesta/i)).toBeInTheDocument(); });
  it('TST-013: Botón deshabilitado sin selección', async () => { renderTM(); await waitFor(() => expect(screen.getByText(closedQ.question)).toBeInTheDocument()); expect(screen.getByText('Enviar Respuesta')).toBeDisabled(); fireEvent.click(screen.getByText('Sierra circular con guarda')); expect(screen.getByText('Enviar Respuesta')).not.toBeDisabled(); });
  it('TST-014: Open <10 chars disabled', async () => { generateQuestion.mockResolvedValue(openQ); renderTM(); await waitFor(() => expect(screen.getByText(openQ.question)).toBeInTheDocument()); fireEvent.change(screen.getByPlaceholderText(/Escribe/i), { target: { value: 'corta' } }); expect(screen.getByText('Enviar Respuesta')).toBeDisabled(); });
  it('TST-015: Open ≥10 chars enabled', async () => { generateQuestion.mockResolvedValue(openQ); renderTM(); await waitFor(() => expect(screen.getByText(openQ.question)).toBeInTheDocument()); fireEvent.change(screen.getByPlaceholderText(/Escribe/i), { target: { value: 'Respuesta larga suficiente' } }); expect(screen.getByText('Enviar Respuesta')).not.toBeDisabled(); });
  it('TST-016: Respuesta correcta feedback', async () => { renderTM(); await waitFor(() => expect(screen.getByText(closedQ.question)).toBeInTheDocument()); fireEvent.click(screen.getByText('Sierra circular con guarda')); fireEvent.click(screen.getByText('Enviar Respuesta')); await waitFor(() => expect(screen.getByText(/Respuesta correcta/i)).toBeInTheDocument()); });
  it('TST-017: Respuesta incorrecta feedback', async () => { renderTM(); await waitFor(() => expect(screen.getByText(closedQ.question)).toBeInTheDocument()); fireEvent.click(screen.getByText('Cuchillo')); fireEvent.click(screen.getByText('Enviar Respuesta')); await waitFor(() => expect(screen.getByText(/Respuesta incorrecta/i)).toBeInTheDocument()); });
  it('TST-018: Llama evaluateOpenAnswer', async () => { generateQuestion.mockResolvedValue(openQ); evaluateOpenAnswer.mockResolvedValue({ score: 80, feedback: 'Bien' }); renderTM(); await waitFor(() => expect(screen.getByText(openQ.question)).toBeInTheDocument()); fireEvent.change(screen.getByPlaceholderText(/Escribe/i), { target: { value: 'Verifico con multímetro la conexión' } }); fireEvent.click(screen.getByText('Enviar Respuesta')); await waitFor(() => expect(evaluateOpenAnswer).toHaveBeenCalled()); });

  it('TST-019: Finalización llama onComplete', async () => {
    const { props } = renderTM();
    // Q1
    await waitFor(() => expect(screen.getByText(closedQ.question)).toBeInTheDocument());
    fireEvent.click(screen.getByText('Sierra circular con guarda'));
    fireEvent.click(screen.getByText('Enviar Respuesta'));
    act(() => vi.advanceTimersByTime(2500));
    // Q2
    await waitFor(() => expect(generateQuestion).toHaveBeenCalledTimes(2));
    await waitFor(() => expect(screen.getByText('Enviar Respuesta')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Sierra circular con guarda'));
    fireEvent.click(screen.getByText('Enviar Respuesta'));
    act(() => vi.advanceTimersByTime(2500));
    // Q3
    await waitFor(() => expect(generateQuestion).toHaveBeenCalledTimes(3));
    await waitFor(() => expect(screen.getByText('Enviar Respuesta')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Sierra circular con guarda'));
    fireEvent.click(screen.getByText('Enviar Respuesta'));
    act(() => vi.advanceTimersByTime(2500));
    await waitFor(() => expect(props.onComplete).toHaveBeenCalledWith(expect.objectContaining({ score: 100, totalQuestions: 3 })));
  });

  it('TST-020: Reset al cerrar', async () => { const { props } = renderTM(); await waitFor(() => expect(screen.getByText(closedQ.question)).toBeInTheDocument()); fireEvent.click(screen.getByText('Cancelar')); expect(props.onClose).toHaveBeenCalled(); });
});
