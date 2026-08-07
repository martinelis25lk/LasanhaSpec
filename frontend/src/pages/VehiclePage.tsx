import { useParams, useNavigate } from "react-router-dom";
import { useVehicleById } from "../hooks/useVehicleById";
import { PerformanceBar } from "../components/PerformanceBar/PerformanceBar";
import "./VehiclePage.css";

const VehiclePage = () => {
  const { id } = useParams<{ id: string }>();
  const vehicleId = Number(id);
  const navigate = useNavigate();
  const { data, loading, error } = useVehicleById(vehicleId);

  if (loading) return <p style={{ padding: 24, color: "#888" }}>Loading...</p>;
  if (error || !data) return <p style={{ padding: 24, color: "#ef4444" }}>Veículo não encontrado.</p>;

  return (
    <div className="vehicle-page">
      <button className="vehicle-back" onClick={() => navigate("/garage")}>
        ← Back to Garage
      </button>

      <div className="vehicle-hero">
        {data.imageUrl && <img src={data.imageUrl} alt={data.name} />}
        <h1>{data.name}</h1>
        <p>{data.engine}</p>
      </div>

      <div className="vehicle-card">
        <h2>Performance Stats</h2>
        <PerformanceBar label="Horsepower" factoryValue={data.factoryHorsePower} currentValue={data.currentHorsePower} diff={data.horsepowerDiff} trend={data.horsepowerTrend} />
        <PerformanceBar label="Torque" factoryValue={data.factoryTorque} currentValue={data.currentTorque} diff={data.torqueDiff} trend={data.torqueTrend} />
        <PerformanceBar label="Weight" factoryValue={data.factoryWeight} currentValue={data.currentWeight} diff={data.weightDiff} trend={data.weightTrend} />
      </div>

      <div className="vehicle-card small">
         <h2>Info</h2>
         <p><strong>Ano:</strong> {data.year ?? "—"}</p>
         <p><strong>Motor:</strong> {data.engine}</p>
         <p><strong>Mods:</strong> {data.modificationsCount ?? 0}</p>
      </div>

      {data.mods?.length > 0 && (
        <div className="vehicle-card">
          <h2>Modificações</h2>
          <div className="mods-grid">
            {data.mods?.map((mod) => (
              <div key={mod.name} className="mod-card">{mod.name}</div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default VehiclePage;