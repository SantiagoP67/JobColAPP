import React, { useState } from 'react';
import { KeyRound, Mail, ShieldCheck, Check, X, Loader2, ArrowLeft } from 'lucide-react';
import Modal from './Modal';
import Button from './Button';
import Input from './Input';
import { forgotPassword, resetPassword } from '../services/authService';
import './AuthModal.css';

export default function ForgotPasswordModal({ isOpen, onClose, onSuccess }) {
  const [step, setStep] = useState(1); // 1=email, 2=code, 3=new password
  const [email, setEmail] = useState('');
  const [code, setCode] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const passwordChecks = [
    { label: 'Mínimo 8 caracteres', valid: newPassword.length >= 8 },
    { label: 'Al menos una mayúscula', valid: /[A-Z]/.test(newPassword) },
    { label: 'Al menos un número', valid: /[0-9]/.test(newPassword) },
    { label: 'Las contraseñas coinciden', valid: newPassword.length > 0 && confirmPassword.length > 0 && newPassword === confirmPassword },
  ];

  const allChecksPass = passwordChecks.every(c => c.valid);

  const handleSendCode = async () => {
    if (!email.trim()) { setError('Ingresa tu correo electrónico'); return; }
    setLoading(true);
    setError('');
    try {
      await forgotPassword(email);
      setStep(2);
    } catch (err) {
      const msg = err.response?.data?.message || err.response?.data || 'Error enviando el código';
      setError(typeof msg === 'string' ? msg : 'Error enviando el código');
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyAndReset = async () => {
    if (!code.trim()) { setError('Ingresa el código'); return; }
    if (!allChecksPass) { setError('Verifica los requisitos de la contraseña'); return; }
    setLoading(true);
    setError('');
    try {
      await resetPassword(email, code, newPassword);
      setSuccess(true);
    } catch (err) {
      const msg = err.response?.data?.message || err.response?.data || 'Error al restablecer la contraseña';
      setError(typeof msg === 'string' ? msg : 'Error al restablecer la contraseña');
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setStep(1);
    setEmail('');
    setCode('');
    setNewPassword('');
    setConfirmPassword('');
    setError('');
    setSuccess(false);
    onClose();
  };

  const handleSuccessClose = () => {
    handleClose();
    onSuccess && onSuccess();
  };

  if (success) {
    return (
      <Modal isOpen={isOpen} onClose={handleSuccessClose}>
        <div style={{ textAlign: 'center', padding: '1rem 0' }}>
          <div style={{ width: 72, height: 72, borderRadius: '50%', background: 'linear-gradient(135deg, #dcfce7, #bbf7d0)', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1.25rem' }}>
            <ShieldCheck size={36} color="#16a34a" />
          </div>
          <h2 style={{ fontSize: '1.4rem', fontWeight: 700, marginBottom: '0.5rem' }}>¡Contraseña restablecida!</h2>
          <p style={{ color: '#6b7280', fontSize: '0.95rem', marginBottom: '1.5rem', lineHeight: 1.6 }}>
            Tu contraseña ha sido cambiada exitosamente. Ya puedes iniciar sesión con tu nueva contraseña.
          </p>
          <Button onClick={handleSuccessClose}>Iniciar Sesión</Button>
        </div>
      </Modal>
    );
  }

  return (
    <Modal isOpen={isOpen} onClose={handleClose}>
      <div className="auth-header">
        <div className="auth-logo-icon" style={{ background: 'linear-gradient(135deg, #f59e0b, #d97706)' }}>
          <KeyRound size={24} color="white" />
        </div>
        <h2 className="auth-title">Recuperar Contraseña</h2>
        <p style={{ color: '#6b7280', fontSize: '0.9rem', marginTop: '0.25rem' }}>
          {step === 1 && 'Ingresa tu correo para recibir un código de verificación'}
          {step === 2 && `Enviamos un código a ${email}`}
        </p>
      </div>

      {step === 1 && (
        <div className="auth-form" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <Input
            label="Correo electrónico"
            type="email"
            value={email}
            onChange={(e) => { setEmail(e.target.value); setError(''); }}
            placeholder="ejemplo@correo.com"
          />
          {error && <p style={{ color: '#ef4444', fontSize: '0.85rem', margin: 0 }}>{error}</p>}
          <Button onClick={handleSendCode} disabled={loading}>
            {loading ? 'Enviando...' : 'Enviar código'}
          </Button>
          <button
            onClick={handleClose}
            style={{ background: 'none', border: 'none', color: '#7c3aed', cursor: 'pointer', fontSize: '0.85rem', fontWeight: 600, fontFamily: 'inherit' }}
          >
            <ArrowLeft size={14} style={{ verticalAlign: 'middle', marginRight: 4 }} />
            Volver al inicio de sesión
          </button>
        </div>
      )}

      {step === 2 && (
        <div className="auth-form" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <Input
            label="Código de verificación"
            value={code}
            onChange={(e) => { setCode(e.target.value); setError(''); }}
            placeholder="Ingresa el código"
          />
          <Input
            label="Nueva contraseña"
            type="password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
          />
          <Input
            label="Confirmar contraseña"
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
          />

          <div className="password-checks">
            <p className="password-checks-title">Requisitos de contraseña:</p>
            {passwordChecks.map((check, i) => (
              <div key={i} className={`password-check-item ${check.valid ? 'valid' : 'invalid'}`}>
                {check.valid ? <Check size={14} /> : <X size={14} />}
                <span>{check.label}</span>
              </div>
            ))}
          </div>

          {error && <p style={{ color: '#ef4444', fontSize: '0.85rem', margin: 0 }}>{error}</p>}

          <Button onClick={handleVerifyAndReset} disabled={loading || !allChecksPass}>
            {loading ? 'Restableciendo...' : 'Restablecer contraseña'}
          </Button>

          <button
            onClick={() => { setStep(1); setError(''); }}
            style={{ background: 'none', border: 'none', color: '#7c3aed', cursor: 'pointer', fontSize: '0.85rem', fontWeight: 600, fontFamily: 'inherit' }}
          >
            <ArrowLeft size={14} style={{ verticalAlign: 'middle', marginRight: 4 }} />
            Cambiar correo
          </button>
        </div>
      )}
    </Modal>
  );
}
