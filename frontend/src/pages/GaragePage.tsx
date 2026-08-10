import { useState } from "react";
import { useUserVehicles } from "../hooks/useUserVehicles";
import { VehicleShowcase } from "../components/VehicleShowcase";
import AddVehicleModal from "../components/AddVehicleModal/AddVehicleModal";
import { deleteUserVehicle } from "../api/vehicleApi";
import "./GaragePage.css";

const MAX_VEHICLES = 5;

const GaragePage = () => {
  const { vehicles, loading, refetch } = useUserVehicles();
  const [showModal, setShowModal] = useState(false);
  const [deletingId, setDeletingId] = useState<number | null>(null);

  async function handleDelete(id: number) {
    if (!confirm("Remover este veículo da garagem?")) return;
    setDeletingId(id);
    try {
      await deleteUserVehicle(id);
      refetch();
    } catch {
      alert("Erro ao remover veículo.");
    } finally {
      setDeletingId(null);
    }
  }

  const atLimit = vehicles.length >= MAX_VEHICLES;

  return (
    <div className="garage-page">

      {/* ── Header ──────────────────────────────────────── */}
      <div className="garage-header">
        <div className="garage-header-text">
          <h1>My Garage</h1>
          <p>Manage your vehicle builds</p>
        </div>
        <div className="garage-header-right">
          <span className="garage-count">{vehicles.length}/{MAX_VEHICLES} veículos</span>
          <button
            className={`add-vehicle-btn ${atLimit ? "add-vehicle-btn--disabled" : ""}`}
            onClick={() => !atLimit && setShowModal(true)}
            disabled={atLimit}
            title={atLimit ? "Limite de 5 veículos atingido" : "Adicionar veículo"}
          >
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            Add Vehicle
          </button>
        </div>
      </div>

      {/* ── Lista ───────────────────────────────────────── */}
      {loading ? (
        <div className="garage-loading">
          <div className="garage-spinner" />
          <p>Carregando sua garagem...</p>
        </div>
      ) : vehicles.length === 0 ? (
        <div className="garage-empty" onClick={() => setShowModal(true)}>
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none"
            stroke="#333" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round">
            <path d="M5 17H3a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11a2 2 0 0 1 2 2v3"/>
            <rect x="9" y="11" width="14" height="10" rx="2"/>
            <circle cx="12" cy="20" r="1"/><circle cx="20" cy="20" r="1"/>
          </svg>
          <p>Sua garagem está vazia.</p>
          <span>Clique para adicionar seu primeiro veículo →</span>
        </div>
      ) : (
        <div className="garage-list">
        {vehicles.map((vehicle) => (
         <div key={vehicle.id} className="garage-item">
         <VehicleShowcase vehicle={vehicle} />
         <button
            className="garage-delete-btn"
            onClick={() => handleDelete(vehicle.id)}
            disabled={deletingId === vehicle.id}
            title="Remover veículo"
          >
        {/* ...ícone que já existe, sem mudança... */}
      </button>
    </div>
  ))}
</div>
      )}

      {/* ── Modal ───────────────────────────────────────── */}
      {showModal && (
        <AddVehicleModal
          onClose={() => setShowModal(false)}
          onSuccess={refetch}
        />
      )}
    </div>
  );
};

export default GaragePage;