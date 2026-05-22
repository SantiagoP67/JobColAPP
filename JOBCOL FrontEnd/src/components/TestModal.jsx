import React, { useState, useEffect } from 'react';
import { ClipboardCheck, Loader2, CheckCircle, XCircle, Send } from 'lucide-react';
import Modal from './Modal';
import Button from './Button';
import { generateQuestion, evaluateOpenAnswer } from '../services/aiTestService';
import './TestModal.css';

const MAX_QUESTIONS = 3;

export default function TestModal({ isOpen, onClose, onComplete, jobCategory }) {
  const [currentQ, setCurrentQ] = useState(0);
  const [questionData, setQuestionData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [selectedOption, setSelectedOption] = useState(null);
  const [openAnswer, setOpenAnswer] = useState('');
  const [submitted, setSubmitted] = useState(false);
  const [previousQA, setPreviousQA] = useState([]);
  const [scores, setScores] = useState([]);
  const [feedback, setFeedback] = useState(null);
  const [evaluating, setEvaluating] = useState(false);

  
  useEffect(() => {
    if (isOpen) {
      resetState();
      loadQuestion(1, []);
    }
  }, [isOpen]);

  const resetState = () => {
    setCurrentQ(0);
    setQuestionData(null);
    setSelectedOption(null);
    setOpenAnswer('');
    setSubmitted(false);
    setPreviousQA([]);
    setScores([]);
    setFeedback(null);
  };

  const loadQuestion = async (qNum, prevQA) => {
    setLoading(true);
    setSelectedOption(null);
    setOpenAnswer('');
    setSubmitted(false);
    setFeedback(null);
    try {
      const q = await generateQuestion(jobCategory || 'General', qNum, prevQA);
      setQuestionData(q);
      setCurrentQ(qNum);
    } catch (err) {
      console.error('Error generating question:', err);
      setQuestionData({
        question: '¿Qué consideras más importante al realizar un trabajo?',
        type: 'closed',
        options: ['Calidad y responsabilidad', 'Velocidad', 'Precio bajo', 'No importa'],
        correctIndex: 0
      });
      setCurrentQ(qNum);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitAnswer = async () => {
    if (submitted) return;
    setSubmitted(true);

    let qScore = 0;
    let wasCorrect = false;
    let answerText = '';
    let fb = null;

    if (questionData.type === 'open') {
      answerText = openAnswer;
      setEvaluating(true);
      try {
        const result = await evaluateOpenAnswer(
          questionData.question, openAnswer,
          questionData.rubric || '', jobCategory || 'General'
        );
        qScore = result.score;
        wasCorrect = qScore >= 60;
        fb = result.feedback;
      } catch {
        qScore = 50;
        wasCorrect = false;
        fb = 'No se pudo evaluar automáticamente.';
      }
      setEvaluating(false);
    } else {
      answerText = questionData.options?.[selectedOption] || '';
      wasCorrect = selectedOption === questionData.correctIndex;
      qScore = wasCorrect ? 100 : 0;
      fb = wasCorrect ? '¡Respuesta correcta!' : `Respuesta incorrecta. La correcta era: "${questionData.options?.[questionData.correctIndex]}"`;
    }

    setFeedback(fb);
    const newScores = [...scores, qScore];
    setScores(newScores);

    const newPrevQA = [...previousQA, {
      question: questionData.question,
      answer: answerText,
      wasCorrect
    }];
    setPreviousQA(newPrevQA);

    
    setTimeout(() => {
      if (currentQ >= MAX_QUESTIONS) {
        finishTest(newScores);
      } else {
        loadQuestion(currentQ + 1, newPrevQA);
      }
    }, 2000);
  };

  const finishTest = (finalScores) => {
    const avg = finalScores.reduce((a, b) => a + b, 0) / finalScores.length;
    onComplete && onComplete({
      score: Math.round(avg),
      details: finalScores,
      totalQuestions: MAX_QUESTIONS
    });
    resetState();
  };

  const handleClose = () => {
    resetState();
    onClose();
  };

  const canSubmit = () => {
    if (!questionData) return false;
    if (questionData.type === 'open') return openAnswer.trim().length >= 2;
    return selectedOption !== null;
  };

 
  const progress = ((currentQ - (submitted ? 0 : 1)) / MAX_QUESTIONS) * 100;

  return (
    <Modal isOpen={isOpen} onClose={handleClose} className="test-modal-wrapper">
      <div className="test-modal-header">
        <div className="test-modal-icon">
          <ClipboardCheck size={28} color="white" />
        </div>
        <h2 className="test-modal-title">Test de Aptitud con IA</h2>
        <p className="test-modal-subtitle">
          Evaluación adaptativa · {jobCategory || 'General'}
        </p>
      </div>

      {/* Progress bar */}
      <div className="test-progress-bar">
        <div className="test-progress-fill" style={{ width: `${progress}%` }} />
      </div>
      <div className="test-progress-label">
        Pregunta {Math.min(currentQ, MAX_QUESTIONS)} de {MAX_QUESTIONS}
      </div>

      {/* Loading state */}
      {loading && (
        <div className="test-loading">
          <Loader2 size={32} className="spin-icon" />
          <p>Generando pregunta con IA...</p>
        </div>
      )}

      {/* Question */}
      {!loading && questionData && (
        <>
          <div className="test-question-card">
            <div className="test-question-number">
              Pregunta {currentQ} · {questionData.type === 'open' ? 'Respuesta abierta' : 'Selección múltiple'}
              {currentQ === 1 && ' · Nivel básico'}
              {currentQ === 2 && ' · Nivel intermedio'}
              {currentQ === 3 && ' · Nivel avanzado'}
            </div>
            <p className="test-question-text">{questionData.question}</p>
          </div>

          {questionData.type === 'closed' && questionData.options && (
            <div className="test-options">
              {questionData.options.map((option, index) => (
                <div
                  key={index}
                  className={`test-option ${selectedOption === index ? 'selected' : ''} ${submitted && index === questionData.correctIndex ? 'correct' : ''} ${submitted && selectedOption === index && index !== questionData.correctIndex ? 'wrong' : ''}`}
                  onClick={() => !submitted && setSelectedOption(index)}
                  style={{ pointerEvents: submitted ? 'none' : 'auto' }}
                >
                  <div className="test-option-radio" />
                  <span className="test-option-text">{option}</span>
                  {submitted && index === questionData.correctIndex && <CheckCircle size={18} color="#16a34a" />}
                  {submitted && selectedOption === index && index !== questionData.correctIndex && <XCircle size={18} color="#dc2626" />}
                </div>
              ))}
            </div>
          )}

          {questionData.type === 'open' && (
            <div className="test-open-answer">
              <textarea
                className="test-textarea"
                placeholder="Escribe tu respuesta aquí (mínimo 2 caracteres)..."
                value={openAnswer}
                onChange={(e) => setOpenAnswer(e.target.value)}
                disabled={submitted}
                rows={4}
              />
              <span className="test-char-count">{openAnswer.length} caracteres</span>
            </div>
          )}

          {feedback && (
            <div className={`test-feedback ${scores[scores.length - 1] >= 60 ? 'success' : 'error'}`}>
              {scores[scores.length - 1] >= 60 ? <CheckCircle size={18} /> : <XCircle size={18} />}
              <span>{feedback}</span>
            </div>
          )}

          {evaluating && (
            <div className="test-evaluating">
              <Loader2 size={18} className="spin-icon" />
              <span>Evaluando respuesta con IA...</span>
            </div>
          )}

          <div className="test-actions">
            <Button variant="ghost" onClick={handleClose}>Cancelar</Button>
            <Button
              variant="primary"
              onClick={handleSubmitAnswer}
              disabled={!canSubmit() || submitted}
            >
              {submitted ? (currentQ >= MAX_QUESTIONS ? 'Finalizando...' : 'Siguiente...') : 'Enviar Respuesta'}
              {!submitted && <Send size={16} />}
            </Button>
          </div>
        </>
      )}
    </Modal>
  );
}
