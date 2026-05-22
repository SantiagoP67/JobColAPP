import React, { useEffect, useState } from "react";
import { getAllOffers } from "../../services/offerService";
import { createPostulation } from "../../services/postulationService";

export default function OffersView() {

  const [offers, setOffers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadOffers();
  }, []);

  const loadOffers = async () => {
    try {
      const data = await getAllOffers();
      setOffers(data);
    } catch (err) {
      console.error("Error cargando ofertas", err);
    } finally {
      setLoading(false);
    }
  };

  const handleApply = async (offerId) => {
    try {
      await createPostulation(offerId);

      alert("✅ Postulación enviada correctamente");

    } catch (error) {
      console.error("❌ Error al postularse", error);

      if (error.response?.status === 400) {
        alert("Ya te postulaste a esta oferta");
      } else {
        alert("Error al postularse");
      }
    }
  };

  if (loading) return <p>Cargando ofertas...</p>;

  return (
    <div>
      <h2>Ofertas disponibles</h2>

      {offers.length === 0 ? (
        <p>No hay ofertas disponibles</p>
      ) : (
        offers.map((offer) => (
          <div key={offer.id} className="offer-card">

            <h3>{offer.title}</h3>
            <p>{offer.description}</p>
            <p><strong>Ubicación:</strong> {offer.location}</p>
            <p><strong>Salario:</strong> {offer.salaryRange}</p>
            <p><strong>Categoría:</strong> {offer.category}</p>

            
            <button
                type="button"
                onClick={() => {
                    console.log("CLICK FUNCIONA");
                    handleApply(offer.id);
                }}
                >
                Postularse
            </button>

          </div>
        ))
      )}
    </div>
  );
}