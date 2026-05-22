import React, { useEffect } from 'react';
import './Modal.css';

export default function Modal({ isOpen, onClose, children, className = '' }) {
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }
    return () => {
      document.body.style.overflow = 'unset';
    };
  }, [isOpen]);

  if (!isOpen) return null;

  return (
    <div className="modal-overlay animate-overlay" onClick={onClose}>
      <div 
        className={`modal-content animate-fade-in ${className}`} 
        onClick={e => e.stopPropagation()}
      >
        {children}
      </div>
    </div>
  );
}
