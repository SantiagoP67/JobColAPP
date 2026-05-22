import React, { useState } from 'react';

import {
  Star,
  ImagePlus,
  CheckCircle2,
  Loader2,
  X,
  Award
} from 'lucide-react';

import Modal from './Modal';
import Button from './Button';

import './ReviewModal.css';

export default function ReviewModal({
  isOpen,
  onClose,
  onSubmit,
  targetName,
  userRole
}) {

  const [rating, setRating] =
    useState(5);

  const [hoverRating, setHoverRating] =
    useState(0);

  const [comment, setComment] =
    useState('');

  const [image, setImage] =
    useState(null);

  const [preview, setPreview] =
    useState(null);

  const [submitting, setSubmitting] =
    useState(false);

  const [imageError, setImageError] =
    useState(false);

  const isWorker = userRole === "TRABAJADOR";

  const handleImageChange = (e) => {

    const file = e.target.files?.[0];

    if (!file) {
      return;
    }

    setImage(file);
    setImageError(false);

    setPreview(
      URL.createObjectURL(file)
    );
  };

  const removeImage = () => {
    setImage(null);
    setPreview(null);
  };

  const handleSubmit = async (e) => {

    e.preventDefault();

    // Worker must upload image
    if (isWorker && !image) {
      setImageError(true);
      return;
    }

    setSubmitting(true);

    try {
      await onSubmit({
        rating,
        comment,
        image
      });

      setRating(5);
      setComment('');
      setImage(null);
      setPreview(null);
      setImageError(false);
    } catch (err) {
      console.error(err);
    } finally {
      setSubmitting(false);
    }
  };

  return (

    <Modal
      isOpen={isOpen}
      onClose={onClose}
      className="review-modal"
    >

      {/* Loading overlay */}
      {submitting && (
        <div className="review-loading-overlay">
          <Loader2 size={36} className="review-spinner" />
          <p className="review-loading-text">Enviando calificación...</p>
        </div>
      )}

      <div className="review-modal-header-new">
        <div className="review-header-icon">
          <Award size={28} />
        </div>
        <h2 className="review-modal-title-new">
          Finalizar Servicio
        </h2>
        <p className="review-modal-subtitle-new">
          Califica tu experiencia con{" "}
          {targetName || "el usuario"}
        </p>
      </div>

      <form
        onSubmit={handleSubmit}
        className="review-form-new"
      >

        {/* Rating Section */}
        <div className="review-section">
          <label className="review-section-label">
            ¿Cómo evaluarías el trabajo?
          </label>

          <div className="review-stars-container">
            <div className="stars-wrapper-new">
              {[1, 2, 3, 4, 5].map((star) => (
                <button
                  key={star}
                  type="button"
                  className={`star-btn-new ${
                    (hoverRating || rating) >= star
                      ? 'active'
                      : ''
                  }`}
                  onMouseEnter={() =>
                    setHoverRating(star)
                  }
                  onMouseLeave={() =>
                    setHoverRating(0)
                  }
                  onClick={() =>
                    setRating(star)
                  }
                >
                  <Star
                    size={36}
                    fill={
                      (hoverRating || rating) >= star
                        ? 'currentColor'
                        : 'none'
                    }
                  />
                </button>
              ))}
            </div>

            <span className="rating-text-new">
              {rating === 1 && '😞 Muy malo'}
              {rating === 2 && '😕 Malo'}
              {rating === 3 && '😐 Regular'}
              {rating === 4 && '😊 Bueno'}
              {rating === 5 && '🤩 Excelente'}
            </span>
          </div>
        </div>

        {/* Comment Section */}
        <div className="review-section">
          <label
            className="review-section-label"
            htmlFor="review-comment"
          >
            Cuéntanos más (Opcional)
          </label>

          <textarea
            id="review-comment"
            className="review-textarea-new"
            placeholder="Escribe tu opinión sobre el servicio..."
            value={comment}
            onChange={(e) =>
              setComment(e.target.value)
            }
            rows={4}
          />
        </div>

        {/* Image Section - Only for workers (required) or employers (hidden) */}
        {isWorker && (
          <div className="review-section">
            <label className="review-section-label">
              Evidencia fotográfica <span className="review-required">*Obligatorio</span>
            </label>
            <p className="review-section-hint">
              Sube una foto del trabajo realizado como evidencia
            </p>

            {!preview ? (
              <label className={`review-image-upload-new ${imageError ? 'error' : ''}`}>
                <ImagePlus size={24} />
                <span className="upload-text">Seleccionar imagen</span>
                <span className="upload-hint">JPG, PNG o WEBP</span>
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleImageChange}
                  hidden
                />
              </label>
            ) : (
              <div className="review-preview-container">
                <img
                  src={preview}
                  alt="preview"
                  className="review-preview-image-new"
                />
                <button
                  type="button"
                  className="review-remove-image"
                  onClick={removeImage}
                >
                  <X size={16} />
                </button>
              </div>
            )}

            {imageError && (
              <p className="review-image-error">
                Debes subir una foto del trabajo realizado
              </p>
            )}
          </div>
        )}

        {/* Actions */}
        <div className="review-actions-new">
          <Button
            variant="ghost"
            type="button"
            onClick={onClose}
            disabled={submitting}
          >
            Cancelar
          </Button>

          <Button
            variant="primary"
            type="submit"
            disabled={submitting}
          >
            {submitting ? 'Enviando...' : 'Enviar Calificación'}
          </Button>
        </div>

      </form>

    </Modal>
  );
}