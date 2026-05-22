import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import ReviewModal from '../../components/ReviewModal';

vi.mock('../../context/ToastContext', () => ({
  useToast: () => ({ showToast: vi.fn() }),
}));

const renderRM = (ov = {}) => {
  const p = { isOpen: true, onClose: vi.fn(), onSubmit: vi.fn().mockResolvedValue({}), targetName: 'Carlos López', userRole: 'EMPLEADOR', ...ov };
  return { ...render(<ReviewModal {...p} />), props: p };
};

describe('ReviewModal (15 pruebas)', () => {
  beforeEach(() => vi.clearAllMocks());

  it('REV-001: Componente se instancia', () => { const { container } = renderRM(); expect(container).toBeTruthy(); });

  it('REV-002: Rating por defecto es 5', () => {
    renderRM();
    expect(screen.getByText(/Excelente/)).toBeInTheDocument();
  });

  it('REV-003: Se puede cambiar rating', () => {
    const { container } = renderRM();
    const stars = container.querySelectorAll('.star-btn-new');
    fireEvent.click(stars[1]); // click star 2
    expect(screen.getByText(/Malo/)).toBeInTheDocument();
  });

  it('REV-004: Texto Excelente con rating=5', () => {
    renderRM();
    expect(screen.getByText('🤩 Excelente')).toBeInTheDocument();
  });

  it('REV-005: Texto Malo con rating=2', () => {
    const { container } = renderRM();
    const stars = container.querySelectorAll('.star-btn-new');
    fireEvent.click(stars[1]);
    expect(screen.getByText('😕 Malo')).toBeInTheDocument();
  });

  it('REV-006: Textarea de comentario', () => {
    renderRM();
    const textarea = screen.getByPlaceholderText(/Escribe tu opinión/i);
    fireEvent.change(textarea, { target: { value: 'Excelente trabajo' } });
    expect(textarea.value).toBe('Excelente trabajo');
  });

  it('REV-007: Upload de imagen para trabajador', () => {
    renderRM({ userRole: 'TRABAJADOR' });
    expect(screen.getByText(/Evidencia fotográfica/i)).toBeInTheDocument();
  });

  it('REV-008: Sin upload para empleador', () => {
    renderRM({ userRole: 'EMPLEADOR' });
    expect(screen.queryByText(/Evidencia fotográfica/i)).not.toBeInTheDocument();
  });

  it('REV-009: Error si trabajador no sube imagen', async () => {
    const { props } = renderRM({ userRole: 'TRABAJADOR' });
    const submitBtn = screen.getByText('Enviar Calificación');
    fireEvent.click(submitBtn);
    expect(screen.getByText(/Debes subir una foto/i)).toBeInTheDocument();
    expect(props.onSubmit).not.toHaveBeenCalled();
  });

  it('REV-010: Preview al seleccionar imagen', () => {
    renderRM({ userRole: 'TRABAJADOR' });
    const file = new File(['test'], 'photo.jpg', { type: 'image/jpeg' });
    const input = document.querySelector('input[type="file"]');
    fireEvent.change(input, { target: { files: [file] } });
    expect(screen.getByAltText('preview')).toBeInTheDocument();
  });

  it('REV-011: Eliminar imagen', () => {
    const { container } = renderRM({ userRole: 'TRABAJADOR' });
    const file = new File(['test'], 'photo.jpg', { type: 'image/jpeg' });
    const input = document.querySelector('input[type="file"]');
    fireEvent.change(input, { target: { files: [file] } });
    expect(screen.getByAltText('preview')).toBeInTheDocument();
    const removeBtn = container.querySelector('.review-remove-image');
    fireEvent.click(removeBtn);
    expect(screen.queryByAltText('preview')).not.toBeInTheDocument();
  });

  it('REV-012: Envío exitoso llama onSubmit', async () => {
    const { props } = renderRM({ userRole: 'EMPLEADOR' });
    fireEvent.click(screen.getByText('Enviar Calificación'));
    await waitFor(() => expect(props.onSubmit).toHaveBeenCalledWith(expect.objectContaining({ rating: 5 })));
  });

  it('REV-013: Estado de carga al enviar', async () => {
    const { props } = renderRM({ userRole: 'EMPLEADOR', onSubmit: vi.fn(() => new Promise(() => {})) });
    fireEvent.click(screen.getByText('Enviar Calificación'));
    await waitFor(() => expect(screen.getByText(/Enviando calificación/i)).toBeInTheDocument());
  });

  it('REV-014: Muestra targetName', () => {
    renderRM();
    expect(screen.getByText(/Carlos López/)).toBeInTheDocument();
  });

  it('REV-015: Botón cancelar', () => {
    const { props } = renderRM();
    fireEvent.click(screen.getByText('Cancelar'));
    expect(props.onClose).toHaveBeenCalled();
  });
});
