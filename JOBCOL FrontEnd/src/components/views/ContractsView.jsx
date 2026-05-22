import React, { useState, useEffect } from 'react';

import {
  Calendar,
  DollarSign,
  FileText,
  CheckCircle2,
  Star,
  Loader2
} from 'lucide-react';

import Button from '../Button';

import { useToast } from '../../context/ToastContext';

import ContractDetailsModal from '../ContractDetailsModal';
import ReviewModal from '../ReviewModal';

import {
  getContractsByUser,
  acceptContract,
  rejectContract,
  requestFinishContract,
  confirmFinishContract
} from '../../services/contractService';

import {
  getCurrentUser,
  getUserFromToken
} from '../../services/authService';

import {
  createReview,
  getReviewsByReviewer
} from '../../services/reviewService';

import {
  createNotification
} from '../../services/notificationService';

import './ContractsView.css';

const filters = [
  'Todos',
  'Pendientes',
  'Activos',
  'Pendientes Finalización',
  'Finalizados',
  'Rechazados'
];

export default function ContractsView() {

  const { showToast } = useToast();

  const [contracts, setContracts] = useState([]);

  const [loading, setLoading] = useState(true);

  const [activeFilter, setActiveFilter] =
    useState('Todos');

  const [selectedContract, setSelectedContract] =
    useState(null);

  const [isReviewOpen, setIsReviewOpen] =
    useState(false);

  const [contractToFinish, setContractToFinish] =
    useState(null);

  const [user, setUser] = useState(null);

  const [reviewData, setReviewData] =
    useState({
      rating: 5,
      comment: ''
    });

  // Action loading states
  const [actionLoading, setActionLoading] = useState(null);

  // Reviews history
  const [myReviews, setMyReviews] = useState([]);

  useEffect(() => {

    const fetchContracts = async () => {

      try {

        const userData =
          await getCurrentUser();

        setUser(userData);

        const data =
          await getContractsByUser(
            userData.id
          );

        setContracts(data);

        // Load my reviews for finished contracts
        try {
          const reviews = await getReviewsByReviewer(userData.id);
          setMyReviews(reviews);
        } catch (err) {
          console.warn("No se pudieron cargar reseñas:", err);
        }

      } catch (error) {

        console.error(
          "Error cargando contratos",
          error
        );

        showToast(
          "Error cargando contratos",
          "error"
        );

      } finally {

        setLoading(false);
      }
    };

    fetchContracts();

  }, []);

  const reloadContracts = async () => {

    try {

      const currentUser =
        await getCurrentUser();

      const updated =
        await getContractsByUser(
          currentUser?.id
        );

      setContracts(updated);

      // Reload reviews too
      try {
        const reviews = await getReviewsByReviewer(currentUser.id);
        setMyReviews(reviews);
      } catch (err) {
        // silent
      }

    } catch (err) {

      console.error(err);
    }
  };

  const filteredContracts =
    contracts.filter(contract => {

      if (activeFilter === "Pendientes") {
        return contract.status === "PENDING";
      }

      if (activeFilter === "Activos") {
        return contract.status === "ACTIVE";
      }

      if (
        activeFilter ===
        "Pendientes Finalización"
      ) {
        return (
          contract.status ===
          "PENDING_FINISH"
        );
      }

      if (activeFilter === "Finalizados") {
        return contract.status === "FINISHED";
      }

      if (activeFilter === "Rechazados") {
        return contract.status === "REJECTED";
      }

      return true;
    });

  const stats = [

    {
      label: 'Total',
      count: contracts.length,
      color: 'text-primary'
    },

    {
      label: 'Pendientes',
      count: contracts.filter(
        c => c.status === "PENDING"
      ).length,
      color: 'text-warning'
    },

    {
      label: 'Activos',
      count: contracts.filter(
        c => c.status === "ACTIVE"
      ).length,
      color: 'text-success'
    },

    {
      label: 'Finalizando',
      count: contracts.filter(
        c => c.status === "PENDING_FINISH"
      ).length,
      color: 'text-info'
    },

    {
      label: 'Finalizados',
      count: contracts.filter(
        c => c.status === "FINISHED"
      ).length,
      color: 'text-muted-dark'
    }

  ];

  const formatDate = (date) => {

    if (!date) {
      return "Sin fecha";
    }

    return new Date(date)
      .toLocaleDateString(
        "es-CO",
        {
          day: "2-digit",
          month: "2-digit",
          year: "numeric"
        }
      );
  };

  const handleRequestFinish = async (
    contractId
  ) => {

    try {

      await requestFinishContract(
        contractId,
        user.id
      );

      showToast(
        "Solicitud de finalización enviada",
        "success"
      );

      await reloadContracts();

    } catch (error) {

      console.error(error);

      showToast(
        "Error solicitando finalización",
        "error"
      );
    }
  };

  const handleConfirmFinish = async (
    contractId
  ) => {

    try {

      await confirmFinishContract(
        contractId,
        user.id
      );

      showToast(
        "Contrato finalizado correctamente",
        "success"
      );

      await reloadContracts();

    } catch (error) {

      console.error(error);

      showToast(
        "Error finalizando contrato",
        "error"
      );
    }
  };

  const handleAcceptContract = async (
    contractId
  ) => {

    try {

      setActionLoading(contractId);

      await acceptContract(contractId);

      // Send notification to employer
      const contract = contracts.find(c => c.id === contractId);
      const employerId = contract?.postulation?.offer?.employerId;
      if (employerId) {
        try {
          await createNotification(
            employerId,
            "¡Contrato aceptado!",
            `El trabajador ha aceptado el contrato para "${contract?.postulation?.offer?.title}".`,
            "POSTULACION_ACEPTADA"
          );
        } catch (notifErr) {
          console.warn("Notification error:", notifErr);
        }
      }

      showToast(
        "Contrato aceptado correctamente",
        "success"
      );

      await reloadContracts();

    } catch (error) {

      console.error(error);

      showToast(
        "Error aceptando contrato",
        "error"
      );
    } finally {
      setActionLoading(null);
    }
  };

  const handleRejectContract = async (
    contractId
  ) => {

    try {

      setActionLoading(contractId);

      await rejectContract(contractId);

      showToast(
        "Contrato rechazado",
        "success"
      );

      await reloadContracts();

    } catch (error) {

      console.error(error);

      showToast(
        "Error rechazando contrato",
        "error"
      );
    } finally {
      setActionLoading(null);
    }
  };

  const handleSubmitReview = async (
    reviewValues
  ) => {

    try {

      if (!contractToFinish) {
        return;
      }

      const workerId =
        contractToFinish
          ?.postulation
          ?.workerId;

      const employerId =
        contractToFinish
          ?.postulation
          ?.offer
          ?.employerId;

      let reviewedUserId = null;

      if (
        user.role === "TRABAJADOR"
      ) {

        reviewedUserId =
          employerId;

      } else {

        reviewedUserId =
          workerId;
      }

      if (!reviewedUserId) {

        showToast(
          "No se encontró el usuario a calificar",
          "error"
        );

        return;
      }

      const payload = {

        id: null,

        rating:
          reviewValues.rating,

        comment:
          reviewValues.comment,

        image:
          reviewValues.image,

        authorType:
          user.role,

        reviewDate: null,

        visible: true,

        reviewedUserId,

        reviewerId:
          user.id
      };

      await createReview(
        payload
      );

      if (
        contractToFinish.status ===
        "PENDING_FINISH"
      ) {

        await handleConfirmFinish(
          contractToFinish.id
        );

      } else {

        await handleRequestFinish(
          contractToFinish.id
        );
      }

      // Send notification to the other party
      const otherUserId = user.role === "TRABAJADOR" ? employerId : workerId;
      if (otherUserId) {
        try {
          await createNotification(
            otherUserId,
            "Solicitud de finalización",
            `Se ha solicitado la finalización del contrato "${contractToFinish?.postulation?.offer?.title}". Revisa tus contratos.`,
            "CONTRATO_FINALIZADO"
          );
        } catch (notifErr) {
          console.warn("Notification error:", notifErr);
        }
      }

      showToast(
        "Reseña enviada correctamente",
        "success"
      );

      setIsReviewOpen(false);

      setContractToFinish(null);

      await reloadContracts();

    } catch (error) {

      console.error(error);

      showToast(
        "Error enviando reseña",
        "error"
      );
    }
  };

  // Find review for a finished contract
  const getMyReviewForContract = (contract) => {
    const workerId = contract?.postulation?.workerId;
    const employerId = contract?.postulation?.offer?.employerId;
    const reviewedUserId = user?.role === "TRABAJADOR" ? employerId : workerId;

    return myReviews.find(r =>
      r.reviewedUserId === reviewedUserId &&
      r.reviewerId === user?.id
    );
  };

  if (loading) {

    return (
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '300px' }}>
        <Loader2 size={32} style={{ color: 'var(--primary)', animation: 'spin 1s linear infinite' }} />
      </div>
    );
  }

  return (

    <div className="contracts-view-wrapper">

      <div className="page-header">

        <h1 className="page-title">
          Contratos
        </h1>

        <p className="page-subtitle">

          Gestiona tus contratos
          laborales activos y
          finalizados

        </p>

      </div>

      <div className="stats-grid contracts-grid">

        {stats.map(stat => (

          <div
            key={stat.label}
            className="stat-card"
          >

            <h2
              className={`stat-count ${stat.color}`}
            >
              {stat.count}
            </h2>

            <p className="stat-label">
              {stat.label}
            </p>

          </div>

        ))}

      </div>

      <div className="filter-pills">

        {filters.map(filter => (

          <button
            key={filter}
            className={`pill ${
              activeFilter === filter
                ? 'active'
                : ''
            }`}
            onClick={() =>
              setActiveFilter(filter)
            }
          >
            {filter}
          </button>

        ))}

      </div>

      <div className="contracts-list">

        {filteredContracts.length === 0 ? (

          <p>
            No hay contratos
            disponibles
          </p>

        ) : (

          filteredContracts.map(contract => {

            const offer =
              contract.postulation?.offer;

            const myReview = contract.status === "FINISHED"
              ? getMyReviewForContract(contract)
              : null;

            return (

              <div
                key={contract.id}
                className="contract-card"
              >

                <div className="contract-header">

                  <div>

                    <h3 className="contract-title">

                      {offer?.title ||
                        "Contrato"}

                    </h3>

                    <p className="contract-company">

                      {offer?.category ||
                        "Sin categoría"}

                    </p>

                  </div>

                  <span className={`badge ${
                    contract.status === "ACTIVE"
                      ? "badge-success"
                      : contract.status === "PENDING"
                      ? "badge-warning"
                      : contract.status === "PENDING_FINISH"
                      ? "badge-info"
                      : contract.status === "REJECTED"
                      ? "badge-danger"
                      : "badge-secondary"
                  }`}>

                    {contract.status}

                  </span>

                </div>

                <p className="contract-description">

                  {offer?.description ||
                    "Sin descripción disponible"}

                </p>

                <div className="contract-details-grid">

                  <div className="detail-box">

                    <div className="detail-box-label">

                      <Calendar size={16} />
                      Inicio

                    </div>

                    <div className="detail-box-value">

                      {formatDate(
                        contract.startDate
                      )}

                    </div>

                  </div>

                  <div className="detail-box">

                    <div className="detail-box-label">

                      <Calendar size={16} />
                      Fin

                    </div>

                    <div className="detail-box-value">

                      {formatDate(
                        contract.endDate
                      )}

                    </div>

                  </div>

                  <div className="detail-box">

                    <div className="detail-box-label">

                      <DollarSign size={16} />
                      Monto

                    </div>

                    <div className="detail-box-value font-bold">

                      $
                      {contract.agreedAmount
                        ?.toLocaleString(
                          "es-CO"
                        )}

                    </div>

                  </div>

                </div>

                {/* Review history for finished contracts */}
                {contract.status === "FINISHED" && myReview && (
                  <div className="contract-review-history">
                    <div className="review-history-header">
                      <Star size={16} color="#fbbf24" fill="#fbbf24" />
                      <span>Tu calificación</span>
                    </div>
                    <div className="review-history-stars">
                      {[1, 2, 3, 4, 5].map(s => (
                        <Star
                          key={s}
                          size={16}
                          color="#fbbf24"
                          fill={s <= myReview.rating ? "#fbbf24" : "none"}
                        />
                      ))}
                      <span className="review-history-rating">{myReview.rating}/5</span>
                    </div>
                    {myReview.comment && (
                      <p className="review-history-comment">"{myReview.comment}"</p>
                    )}
                  </div>
                )}

                <div className="contract-actions">

                  <Button
                    variant="secondary"
                    onClick={() =>
                      setSelectedContract(
                        contract
                      )
                    }
                  >

                    <FileText size={16} />
                    Ver Detalles

                  </Button>

                  {user?.role === "TRABAJADOR" &&
                    contract.status === "PENDING" && (

                    <>

                      <Button
                        variant="primary"
                        onClick={() =>
                          handleAcceptContract(
                            contract.id
                          )
                        }
                        disabled={actionLoading === contract.id}
                      >
                        {actionLoading === contract.id ? 'Aceptando...' : 'Aceptar'}
                      </Button>

                      <Button
                        variant="secondary"
                        onClick={() =>
                          handleRejectContract(
                            contract.id
                          )
                        }
                        disabled={actionLoading === contract.id}
                      >
                        Rechazar
                      </Button>

                    </>

                  )}

                  {contract.status === "ACTIVE" && (

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem' }}>
                      <Button
                        variant="secondary"
                        onClick={() => {

                          setContractToFinish(
                            contract
                          );

                          setReviewData({
                            rating: 5,
                            comment: ''
                          });

                          setIsReviewOpen(true);

                        }}
                      >

                        <CheckCircle2 size={16} />
                        Solicitar Finalización

                      </Button>

                      <span style={{ fontSize: '0.7rem', color: '#6b7280', textAlign: 'center' }}>
                        Se pedirá una calificación
                      </span>

                    </div>

                  )}

                  {contract.status === "PENDING_FINISH" && (() => {

                    const userAlreadyRequestedFinish =
                      (user?.role === "TRABAJADOR" &&
                        contract.workerFinished) ||

                      (user?.role !== "TRABAJADOR" &&
                        contract.employerFinished);

                    if (userAlreadyRequestedFinish) {

                      return (

                        <div
                          style={{
                            display: 'flex',
                            flexDirection: 'column',
                            gap: '0.35rem'
                          }}
                        >

                          <Button
                            variant="secondary"
                            disabled
                          >

                            <CheckCircle2 size={16} />
                            Esperando confirmación

                          </Button>

                          <span
                            style={{
                              fontSize: '0.7rem',
                              color: '#6b7280',
                              textAlign: 'center'
                            }}
                          >
                            La otra parte debe confirmar la finalización
                          </span>

                        </div>
                      );
                    }

                    return (

                      <div
                        style={{
                          display: 'flex',
                          flexDirection: 'column',
                          gap: '0.35rem'
                        }}
                      >

                        <Button
                          variant="primary"
                          onClick={() => {

                            setContractToFinish(
                              contract
                            );

                            setReviewData({
                              rating: 5,
                              comment: ''
                            });

                            setIsReviewOpen(true);

                          }}
                        >

                          <CheckCircle2 size={16} />
                          Confirmar y Calificar

                        </Button>

                        <span
                          style={{
                            fontSize: '0.7rem',
                            color: '#7c3aed',
                            textAlign: 'center',
                            fontWeight: 500
                          }}
                        >
                          La otra parte ya solicitó finalizar
                        </span>

                      </div>
                    );

                  })()}

                </div>

              </div>

            );
          })

        )}

      </div>

      <ContractDetailsModal
        isOpen={!!selectedContract}
        onClose={() =>
          setSelectedContract(null)
        }
        contract={selectedContract}
      />

      <ReviewModal
        isOpen={isReviewOpen}
        onClose={() =>
          setIsReviewOpen(false)
        }
        onSubmit={handleSubmitReview}
        targetName={
          user?.role === "TRABAJADOR"
            ? "Empleador"
            : "Trabajador"
        }
        userRole={user?.role}
      />

    </div>
  );
}