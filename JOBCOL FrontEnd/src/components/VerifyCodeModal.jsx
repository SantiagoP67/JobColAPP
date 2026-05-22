import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Modal from './Modal';
import Button from './Button';
import Input from './Input';
import api from '../services/api';

export default function VerifyCodeModal({ isOpen, onClose, email, onSuccess }) {

    const [code, setCode] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate(); 

    const handleVerify = async () => {
        setLoading(true);
        setError('');

        try {
            const res = await api.post('/auth/verify-code', {
                email,
                code
            });

            if (res.data.valid) {

                // 🧹 limpiar datos temporales
                localStorage.removeItem("pendingUser");
                localStorage.removeItem("email");

                onClose();

                // callback opcional
                onSuccess && onSuccess();

                // 🚀 REDIRECCIÓN AL DASHBOARD
                navigate("/dashboard");

            } else {
                setError('Código inválido');
            }

        } catch (err) {
            console.error(err);
            setError('Error al verificar código');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal isOpen={isOpen} onClose={onClose}>
            <div style={{ textAlign: 'center' }}>
                <h2>Verificación de código</h2>
                <p>Ingresa el código enviado a tu correo</p>

                <Input
                    label="Código"
                    value={code}
                    onChange={(e) => setCode(e.target.value)}
                />

                {error && <p style={{ color: 'red' }}>{error}</p>}

                <Button onClick={handleVerify} disabled={loading}>
                    {loading ? "Verificando..." : "Verificar"}
                </Button>
            </div>
        </Modal>
    );
}