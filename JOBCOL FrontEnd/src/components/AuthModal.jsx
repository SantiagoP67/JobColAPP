import React, { useState, useEffect } from 'react';
import { Briefcase, User, Building2, Check, X, Loader2 } from 'lucide-react';
import Modal from './Modal';
import Button from './Button';
import Input from './Input';
import { login, register } from '../services/authService';
import './AuthModal.css';

// Validaciones
const validators = {
  phone: (v) => {
    if (!/^\d+$/.test(v)) return 'Solo se permiten números';
    if (v.length !== 10) return 'Debe tener exactamente 10 dígitos';
    return null;
  },
  cedula: (v) => {
    if (!/^\d+$/.test(v)) return 'Solo se permiten números';
    if (v.length < 6 || v.length > 12) return 'Debe tener entre 6 y 12 dígitos';
    return null;
  },
  email: (v) => {
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)) return 'Correo electrónico inválido';
    return null;
  },
  name: (v) => {
    if (v.trim().length < 2) return 'Mínimo 2 caracteres';
    if (!/^[a-zA-ZáéíóúñÁÉÍÓÚÑüÜ\s]+$/.test(v)) return 'Solo se permiten letras';
    return null;
  },
  username: (v) => {
    if (v.trim().length < 3) return 'Mínimo 3 caracteres';
    if (!/^[a-zA-Z0-9._]+$/.test(v)) return 'Solo letras, números, puntos y guion bajo';
    return null;
  },
  password: (v) => {
    if (v.length < 8) return 'Mínimo 8 caracteres';
    if (!/[A-Z]/.test(v)) return 'Debe tener al menos una mayúscula';
    if (!/[0-9]/.test(v)) return 'Debe tener al menos un número';
    return null;
  }
};

export default function AuthModal({
  isOpen,
  onClose,
  onRegisterSuccess,
  onLoginSuccess,
  onRequireVerifyCode,
  onForgotPassword,
  initialTab = 'login'
}) {

  const [tab, setTab] = useState(initialTab);
  const [role, setRole] = useState('candidato');

  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [phone, setPhone] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [cedula, setCedula] = useState('');

  const [fieldErrors, setFieldErrors] = useState({});
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Sincronizar tab cuando cambia initialTab (al hacer clic en "Registrarse")
  useEffect(() => {
    setTab(initialTab);
  }, [initialTab]);

  const sanitizeUsername = (value) => value.trim().toLowerCase();

  const mapRole = (role) => {
    if (role === 'candidato') return 'TRABAJADOR';
    if (role === 'empleador') return 'EMPLEADOR';
    return role;
  };

  const validateField = (name, value) => {
    const fn = validators[name];
    if (!fn) return null;
    return fn(value);
  };

  const handleFieldChange = (name, value, setter) => {
    setter(value);
    // Limpiar error del campo al escribir
    if (fieldErrors[name]) {
      setFieldErrors(prev => ({ ...prev, [name]: null }));
    }
  };

  // Password strength checks
  const passwordChecks = [
    { label: 'Mínimo 8 caracteres', valid: password.length >= 8 },
    { label: 'Al menos una mayúscula', valid: /[A-Z]/.test(password) },
    { label: 'Al menos un número', valid: /[0-9]/.test(password) },
    { label: 'Las contraseñas coinciden', valid: password.length > 0 && confirmPassword.length > 0 && password === confirmPassword },
  ];

  const allPasswordChecksPass = passwordChecks.every(c => c.valid);

  const validateAllFields = () => {
    if (tab === 'login') return true;

    const errors = {};
    const checks = [
      ['username', username],
      ['name', firstName],
      ['name', lastName],
      ['cedula', cedula],
      ['email', email],
      ['phone', phone],
      ['password', password]
    ];

    const fieldNames = ['username', 'firstName', 'lastName', 'cedula', 'email', 'phone', 'password'];

    checks.forEach(([validatorKey, value], i) => {
      const err = validateField(validatorKey, value);
      if (err) errors[fieldNames[i]] = err;
    });

    if (password !== confirmPassword) {
      errors.confirmPassword = 'Las contraseñas no coinciden';
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    if (!validateAllFields()) return;

    if (tab === 'register' && !allPasswordChecksPass) return;

    setLoading(true);

    try {

      if (tab === 'login') {

        const res = await login({
          username,
          password
        });

        localStorage.setItem("pendingUser", username);
        localStorage.setItem("email", res.email); // importante

        onClose();

        onRequireVerifyCode && onRequireVerifyCode({
          username,
          email: res.email
        });

        return;
      }

      const payload = {
        username: sanitizeUsername(username),
        email,
        password,
        firstName,
        lastName,
        cedula,
        phone,
        role: mapRole(role)
      };

      await register(payload);

      
      localStorage.setItem("pendingUser", username);
      localStorage.setItem("email", email);
      localStorage.setItem("role", payload.role);
      onClose();

      
      onRegisterSuccess && onRegisterSuccess();

    } catch (err) {
      console.error(err);

      const msg =
        err.response?.data?.message ||
        err.response?.data ||
        "Error en autenticación";

      setError(msg);

    } finally {
      setLoading(false);
    }
  };

  const renderFieldError = (name) => {
    if (!fieldErrors[name]) return null;
    return <span style={{ color: '#ef4444', fontSize: '0.75rem', marginTop: '2px', display: 'block' }}>{fieldErrors[name]}</span>;
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose}>

      {/* Loading overlay for registration */}
      {loading && tab === 'register' && (
        <div className="auth-loading-overlay">
          <div className="auth-loading-content">
            <Loader2 size={40} className="auth-spinner" />
            <p className="auth-loading-text">Creando tu cuenta...</p>
            <p className="auth-loading-subtext">Esto puede tomar unos segundos</p>
          </div>
        </div>
      )}

      <div className="auth-header">
        <div className="auth-logo-icon">
          <Briefcase size={24} color="white" />
        </div>
        <h2 className="auth-title">Bienvenido a JobCol</h2>
      </div>

      <div className="auth-tabs">
        <button
          className={tab === 'login' ? 'auth-tab active' : 'auth-tab'}
          onClick={() => setTab('login')}
        >
          Iniciar Sesión
        </button>

        <button
          className={tab === 'register' ? 'auth-tab active' : 'auth-tab'}
          onClick={() => setTab('register')}
        >
          Registrarse
        </button>
      </div>

      <form onSubmit={handleSubmit} className="auth-form">

        {tab === 'login' && (
          <Input
            label="Nombre de usuario"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        )}

        {tab === 'register' && (
          <>
            <div>
              <Input label="Nombre de usuario" value={username} onChange={(e) => handleFieldChange('username', e.target.value, setUsername)} required />
              {renderFieldError('username')}
            </div>
            <div>
              <Input label="Nombre" value={firstName} onChange={(e) => handleFieldChange('firstName', e.target.value, setFirstName)} required />
              {renderFieldError('firstName')}
            </div>
            <div>
              <Input label="Apellido" value={lastName} onChange={(e) => handleFieldChange('lastName', e.target.value, setLastName)} required />
              {renderFieldError('lastName')}
            </div>
            <div>
              <Input label="Cédula" value={cedula} onChange={(e) => handleFieldChange('cedula', e.target.value, setCedula)} required placeholder="Ej: 1234567890" />
              {renderFieldError('cedula')}
            </div>
            <div>
              <Input label="Correo" type="email" value={email} onChange={(e) => handleFieldChange('email', e.target.value, setEmail)} required placeholder="ejemplo@correo.com" />
              {renderFieldError('email')}
            </div>
            <div>
              <Input label="Teléfono" value={phone} onChange={(e) => handleFieldChange('phone', e.target.value, setPhone)} required placeholder="3001234567" />
              {renderFieldError('phone')}
            </div>
          </>
        )}

        {tab === 'register' && (
          <div className="role-selection">
            <label>Me registro como:</label>

            <label className={`role-card ${role === 'candidato' ? 'selected' : ''}`}>
              <input type="radio" checked={role === 'candidato'} onChange={() => setRole('candidato')} />
              <User /> Candidato
            </label>

            <label className={`role-card ${role === 'empleador' ? 'selected' : ''}`}>
              <input type="radio" checked={role === 'empleador'} onChange={() => setRole('empleador')} />
              <Building2 /> Empleador
            </label>
          </div>
        )}

        <div>
          <Input
            label="Contraseña"
            type="password"
            value={password}
            onChange={(e) => handleFieldChange('password', e.target.value, setPassword)}
            required
          />
          {tab === 'register' && renderFieldError('password')}
        </div>

        {tab === 'register' && (
          <>
            <div>
              <Input
                label="Confirmar contraseña"
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />
              {renderFieldError('confirmPassword')}
            </div>

            {/* Password strength indicators - always visible in register */}
            <div className="password-checks">
              <p className="password-checks-title">Requisitos de contraseña:</p>
              {passwordChecks.map((check, i) => (
                <div key={i} className={`password-check-item ${check.valid ? 'valid' : 'invalid'}`}>
                  {check.valid ? <Check size={14} /> : <X size={14} />}
                  <span>{check.label}</span>
                </div>
              ))}
            </div>
          </>
        )}

        {error && <p style={{ color: "red" }}>{error}</p>}

        {tab === 'login' && onForgotPassword && (
          <button
            type="button"
            onClick={() => { onClose(); onForgotPassword(); }}
            style={{ background: 'none', border: 'none', color: '#7c3aed', cursor: 'pointer', fontSize: '0.85rem', fontWeight: 600, fontFamily: 'inherit', padding: 0, textAlign: 'right', display: 'block', marginLeft: 'auto' }}
          >
            ¿Olvidaste tu contraseña?
          </button>
        )}

        <Button type="submit" disabled={loading || (tab === 'register' && !allPasswordChecksPass && password.length > 0)}>
          {loading ? "Cargando..." : tab === 'login' ? "Iniciar Sesión" : "Registrarse"}
        </Button>

      </form>

    </Modal>
  );
}