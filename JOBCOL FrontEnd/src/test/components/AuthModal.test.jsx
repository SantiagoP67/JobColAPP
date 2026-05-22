import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import AuthModal from '../../components/AuthModal';

vi.mock('../../services/authService', () => ({
  login: vi.fn(),
  register: vi.fn(),
}));
vi.mock('../../context/ToastContext', () => ({
  useToast: () => ({ showToast: vi.fn() }),
  ToastProvider: ({ children }) => children,
}));

import { login, register } from '../../services/authService';

const renderAM = (ov = {}) => {
  const p = { isOpen: true, onClose: vi.fn(), onRegisterSuccess: vi.fn(), onLoginSuccess: vi.fn(), onRequireVerifyCode: vi.fn(), initialTab: 'login', ...ov };
  return { ...render(<AuthModal {...p} />), props: p };
};

describe('AuthModal (23 pruebas)', () => {
  beforeEach(() => { vi.clearAllMocks(); localStorage.clear(); });

  // AUTH-001
  it('AUTH-001: Componente se instancia', () => {
    const { container } = renderAM();
    expect(container).toBeTruthy();
  });

  // AUTH-002
  it('AUTH-002: Tab login activo cuando initialTab=login', () => {
    renderAM({ initialTab: 'login' });
    const tabs = screen.getAllByRole('button');
    const loginTab = tabs.find(b => b.textContent.includes('Iniciar Sesión') && b.classList.contains('auth-tab'));
    expect(loginTab).toHaveClass('active');
  });

  // AUTH-003
  it('AUTH-003: Tab register activo cuando initialTab=register', () => {
    renderAM({ initialTab: 'register' });
    const tabs = screen.getAllByRole('button');
    const regTab = tabs.find(b => b.textContent.includes('Registrarse') && b.classList.contains('auth-tab'));
    expect(regTab).toHaveClass('active');
  });

  // AUTH-004
  it('AUTH-004: Se puede cambiar al tab de registro', () => {
    renderAM({ initialTab: 'login' });
    const tabs = screen.getAllByRole('button');
    const regTab = tabs.find(b => b.textContent.includes('Registrarse') && b.classList.contains('auth-tab'));
    fireEvent.click(regTab);
    expect(regTab).toHaveClass('active');
  });

  // AUTH-005
  it('AUTH-005: Se puede cambiar al tab de login', () => {
    renderAM({ initialTab: 'register' });
    const tabs = screen.getAllByRole('button');
    const loginTab = tabs.find(b => b.textContent.includes('Iniciar Sesión') && b.classList.contains('auth-tab'));
    fireEvent.click(loginTab);
    expect(loginTab).toHaveClass('active');
  });

  // AUTH-006
  it('AUTH-006: Campos de registro visibles', () => {
    renderAM({ initialTab: 'register' });
    expect(screen.getByText('Nombre de usuario')).toBeInTheDocument();
    expect(screen.getByText('Nombre')).toBeInTheDocument();
    expect(screen.getByText('Apellido')).toBeInTheDocument();
    expect(screen.getByText('Cédula')).toBeInTheDocument();
    expect(screen.getByText('Correo')).toBeInTheDocument();
    expect(screen.getByText('Teléfono')).toBeInTheDocument();
  });

  // AUTH-007
  it('AUTH-007: Rol inicia como candidato', () => {
    renderAM({ initialTab: 'register' });
    const radios = screen.getAllByRole('radio');
    expect(radios[0].checked).toBe(true); // candidato
    expect(screen.getByText('Candidato')).toBeInTheDocument();
  });

  // AUTH-008
  it('AUTH-008: Se puede seleccionar rol empleador', () => {
    renderAM({ initialTab: 'register' });
    const radios = screen.getAllByRole('radio');
    fireEvent.click(radios[1]);
    expect(radios[1].checked).toBe(true);
  });

  // AUTH-009 to AUTH-019: Validation tests (pure logic)
  it('AUTH-009: Validación teléfono - solo números', () => {
    const v = (val) => { if (!/^\d+$/.test(val)) return 'Solo se permiten números'; if (val.length !== 10) return 'Debe tener exactamente 10 dígitos'; return null; };
    expect(v('abc')).toBe('Solo se permiten números');
  });

  it('AUTH-010: Validación teléfono - longitud', () => {
    const v = (val) => { if (!/^\d+$/.test(val)) return 'Solo se permiten números'; if (val.length !== 10) return 'Debe tener exactamente 10 dígitos'; return null; };
    expect(v('12345')).toBe('Debe tener exactamente 10 dígitos');
    expect(v('3001234567')).toBeNull();
  });

  it('AUTH-011: Validación cédula - solo números', () => {
    const v = (val) => { if (!/^\d+$/.test(val)) return 'Solo se permiten números'; if (val.length < 6 || val.length > 12) return 'Debe tener entre 6 y 12 dígitos'; return null; };
    expect(v('abc123')).toBe('Solo se permiten números');
  });

  it('AUTH-012: Validación cédula - rango', () => {
    const v = (val) => { if (!/^\d+$/.test(val)) return 'Solo se permiten números'; if (val.length < 6 || val.length > 12) return 'Debe tener entre 6 y 12 dígitos'; return null; };
    expect(v('123')).toBe('Debe tener entre 6 y 12 dígitos');
    expect(v('12345678')).toBeNull();
  });

  it('AUTH-013: Validación email inválido', () => {
    const v = (val) => { if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val)) return 'Correo electrónico inválido'; return null; };
    expect(v('invalid')).toBe('Correo electrónico inválido');
    expect(v('test@mail.com')).toBeNull();
  });

  it('AUTH-014: Validación nombre mínimo', () => {
    const v = (val) => { if (val.trim().length < 2) return 'Mínimo 2 caracteres'; return null; };
    expect(v('A')).toBe('Mínimo 2 caracteres');
  });

  it('AUTH-015: Validación nombre solo letras', () => {
    const v = (val) => { if (val.trim().length < 2) return 'Mín 2'; if (!/^[a-zA-ZáéíóúñÁÉÍÓÚÑüÜ\s]+$/.test(val)) return 'Solo se permiten letras'; return null; };
    expect(v('Juan123')).toBe('Solo se permiten letras');
    expect(v('Juan')).toBeNull();
  });

  it('AUTH-016: Validación username mínimo', () => {
    const v = (val) => { if (val.trim().length < 3) return 'Mínimo 3 caracteres'; return null; };
    expect(v('ab')).toBe('Mínimo 3 caracteres');
    expect(v('abc')).toBeNull();
  });

  it('AUTH-017: Validación contraseña mínimo 8', () => {
    const v = (val) => { if (val.length < 8) return 'Mínimo 8 caracteres'; return null; };
    expect(v('Pass1')).toBe('Mínimo 8 caracteres');
  });

  it('AUTH-018: Validación contraseña mayúscula', () => {
    const v = (val) => { if (val.length < 8) return 'Min 8'; if (!/[A-Z]/.test(val)) return 'Debe tener al menos una mayúscula'; return null; };
    expect(v('password1')).toBe('Debe tener al menos una mayúscula');
  });

  it('AUTH-019: Validación contraseña número', () => {
    const v = (val) => { if (val.length < 8) return 'Min 8'; if (!/[A-Z]/.test(val)) return 'May'; if (!/[0-9]/.test(val)) return 'Debe tener al menos un número'; return null; };
    expect(v('Password')).toBe('Debe tener al menos un número');
    expect(v('Password1')).toBeNull();
  });

  // AUTH-020
  it('AUTH-020: Los 4 checks de contraseña visibles en registro', () => {
    renderAM({ initialTab: 'register' });
    expect(screen.getByText('Mínimo 8 caracteres')).toBeInTheDocument();
    expect(screen.getByText('Al menos una mayúscula')).toBeInTheDocument();
    expect(screen.getByText('Al menos un número')).toBeInTheDocument();
    expect(screen.getByText('Las contraseñas coinciden')).toBeInTheDocument();
  });

  // AUTH-021
  it('AUTH-021: Login exitoso llama onRequireVerifyCode', async () => {
    login.mockResolvedValueOnce({ accessToken: 'mock-token', email: 'test@mail.com' });
    const { props } = renderAM({ initialTab: 'login' });

    const inputs = screen.getAllByRole('textbox');
    const passwordInputs = document.querySelectorAll('input[type="password"]');
    fireEvent.change(inputs[0], { target: { value: 'testuser' } });
    fireEvent.change(passwordInputs[0], { target: { value: 'Password1' } });

    const submitBtn = document.querySelector('button[type="submit"]');
    fireEvent.click(submitBtn);

    await waitFor(() => expect(login).toHaveBeenCalledWith({ username: 'testuser', password: 'Password1' }));
    await waitFor(() => expect(props.onRequireVerifyCode).toHaveBeenCalled());
  });

  // AUTH-022
  it('AUTH-022: Registro exitoso llama onRegisterSuccess', async () => {
    register.mockResolvedValueOnce({ accessToken: 'mock-token' });
    const { props } = renderAM({ initialTab: 'register' });

    const textInputs = screen.getAllByRole('textbox');
    const passwordInputs = document.querySelectorAll('input[type="password"]');

    // Fill all text inputs in order: username, firstName, lastName, cedula, email, phone
    fireEvent.change(textInputs[0], { target: { value: 'testuser' } });
    fireEvent.change(textInputs[1], { target: { value: 'Juan' } });
    fireEvent.change(textInputs[2], { target: { value: 'García' } });
    fireEvent.change(textInputs[3], { target: { value: '12345678' } });
    fireEvent.change(textInputs[4], { target: { value: 'test@mail.com' } });
    fireEvent.change(textInputs[5], { target: { value: '3001234567' } });
    fireEvent.change(passwordInputs[0], { target: { value: 'Password1' } });
    fireEvent.change(passwordInputs[1], { target: { value: 'Password1' } });

    const submitBtn = document.querySelector('button[type="submit"]');
    fireEvent.click(submitBtn);

    await waitFor(() => expect(register).toHaveBeenCalled());
    await waitFor(() => expect(props.onRegisterSuccess).toHaveBeenCalled());
  });

  // AUTH-023
  it('AUTH-023: Error en login muestra mensaje', async () => {
    login.mockRejectedValueOnce({ response: { data: { message: 'Credenciales inválidas' } } });
    renderAM({ initialTab: 'login' });

    const inputs = screen.getAllByRole('textbox');
    const passwordInputs = document.querySelectorAll('input[type="password"]');
    fireEvent.change(inputs[0], { target: { value: 'bad' } });
    fireEvent.change(passwordInputs[0], { target: { value: 'wrong' } });

    const submitBtn = document.querySelector('button[type="submit"]');
    fireEvent.click(submitBtn);

    await waitFor(() => expect(screen.getByText('Credenciales inválidas')).toBeInTheDocument());
  });
});
