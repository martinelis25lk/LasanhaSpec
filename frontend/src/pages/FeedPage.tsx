import { useEffect, useState } from "react";
import VehicleCard from "../components/VehicleCard/VehicleCard";
import { EmptyState, ErrorState } from "../components/StateViews/StateViews";
import { getFeedVehicles } from "../api/vehicleApi";
import type { VehicleCard as VehicleCardType } from "../types/vehicle";
import "./GaragePage.css";

export default function FeedPage() {
  const [vehicles, setVehicles] = useState<VehicleCardType[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  async function loadFeed() {
    setLoading(true);
    setError(false);
    try {
      const data = await getFeedVehicles();
      setVehicles(data);
    } catch (err) {
      console.error("Erro ao carregar feed:", err);
      setError(true);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadFeed();
  }, []);

  return (
    <div className="garage-page">
      <div className="garage-header">
        <div className="garage-header-text">
          <h1>Feed</h1>
          <p>Garagens da comunidade</p>
        </div>
      </div>

      {loading ? (
        <div className="garage-loading">
          <div className="garage-spinner" />
          <p>Carregando feed...</p>
        </div>
      ) : error ? (
        <ErrorState message="Não foi possível carregar o feed." onRetry={loadFeed} />
      ) : vehicles.length === 0 ? (
        <EmptyState
          icon="🚗"
          title="Nenhuma garagem por aqui ainda"
          description="Quando a comunidade adicionar veículos, eles aparecem aqui. Que tal ser o primeiro? Adicione um veículo na sua garagem."
        />
      ) : (
        <div className="garage-list">
          {vehicles.map((vehicle) => (
            <VehicleCard key={vehicle.id} vehicle={vehicle} />
          ))}
        </div>
      )}
    </div>
  );
}