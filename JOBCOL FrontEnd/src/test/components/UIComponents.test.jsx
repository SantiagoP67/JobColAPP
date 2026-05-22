import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import Modal from '../../components/Modal';
import Button from '../../components/Button';
import Input from '../../components/Input';

vi.mock('../../context/ToastContext', () => ({
  useToast: () => ({ showToast: vi.fn() }),
}));

describe('Modal (5 pruebas)', () => {
  beforeEach(() => { vi.clearAllMocks(); document.body.style.overflow = ''; });

  it('UI-001: No renderiza si isOpen=false', () => {
    const { container } = render(<Modal isOpen={false} onClose={vi.fn()}>Contenido</Modal>);
    expect(screen.queryByText('Contenido')).not.toBeInTheDocument();
  });

  it('UI-002: Renderiza children si isOpen=true', () => {
    render(<Modal isOpen={true} onClose={vi.fn()}>Contenido Visible</Modal>);
    expect(screen.getByText('Contenido Visible')).toBeInTheDocument();
  });

  it('UI-003: Click en overlay cierra modal', () => {
    const onClose = vi.fn();
    const { container } = render(<Modal isOpen={true} onClose={onClose}>X</Modal>);
    fireEvent.click(container.querySelector('.modal-overlay'));
    expect(onClose).toHaveBeenCalled();
  });

  it('UI-004: Click en contenido no cierra modal', () => {
    const onClose = vi.fn();
    const { container } = render(<Modal isOpen={true} onClose={onClose}>X</Modal>);
    fireEvent.click(container.querySelector('.modal-content'));
    expect(onClose).not.toHaveBeenCalled();
  });

  it('UI-005: Bloquea scroll del body al abrir', () => {
    render(<Modal isOpen={true} onClose={vi.fn()}>X</Modal>);
    expect(document.body.style.overflow).toBe('hidden');
  });
});

describe('Button (5 pruebas)', () => {
  it('UI-006: Renderiza children', () => {
    render(<Button>Click Me</Button>);
    expect(screen.getByText('Click Me')).toBeInTheDocument();
  });

  it('UI-007: Clase btn-primary por defecto', () => {
    render(<Button>Test</Button>);
    expect(screen.getByText('Test')).toHaveClass('btn-primary');
  });

  it('UI-008: Clase btn-secondary', () => {
    render(<Button variant="secondary">Test</Button>);
    expect(screen.getByText('Test')).toHaveClass('btn-secondary');
  });

  it('UI-009: Clase btn-ghost', () => {
    render(<Button variant="ghost">Test</Button>);
    expect(screen.getByText('Test')).toHaveClass('btn-ghost');
  });

  it('UI-010: Prop disabled', () => {
    render(<Button disabled>Test</Button>);
    expect(screen.getByText('Test')).toBeDisabled();
  });
});

describe('Input (3 pruebas)', () => {
  it('UI-011: Renderiza label', () => {
    render(<Input label="Email" />);
    expect(screen.getByText('Email')).toBeInTheDocument();
  });

  it('UI-012: Clase input-error cuando error existe', () => {
    const { container } = render(<Input label="Test" error="Error msg" />);
    expect(container.querySelector('.input-error')).toBeInTheDocument();
  });

  it('UI-013: Muestra mensaje de error', () => {
    render(<Input label="Test" error="Campo requerido" />);
    expect(screen.getByText('Campo requerido')).toBeInTheDocument();
  });
});
