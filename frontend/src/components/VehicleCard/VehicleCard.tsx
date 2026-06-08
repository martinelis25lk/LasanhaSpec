import { useNavigate } from "react-router-dom";
import { useRef, useState } from "react";
import type { VehicleCard as VehicleCardType } from "../../types/vehicle";
import { PerformanceBar } from "../PerformanceBar/PerformanceBar";
import { uploadVehicleImage } from "../../api/vehicleApi";
import "./VehicleCard.css";

interface Props {
  vehicle: VehicleCardType;
  onImageUploaded?: () => void;
}

const VehicleCard = ({ vehicle, onImageUploaded }: Props) => {
  const navigate = useNavigate();
  const fileRef = useRef<HTMLInputElement>(null);
  const [uploading, setUploading] = useState(false);
  // Bug 2 corrigido: estado local para imagem, atualizado imediatamente após upload
  const [localImageUrl, setLocalImageUrl] = useState<string | null>(vehicle.imageUrl);

  const pwrFactory = vehicle.factoryWeight > 0
    ? Math.round((vehicle.factoryHorsePower / (vehicle.factoryWeight / 1000)) * 10) / 10 : 0;
  const pwrCurrent = vehicle.currentWeight > 0
    ? Math.round((vehicle.currentHorsePower / (vehicle.currentWeight / 1000)) * 10) / 10 : 0;
  const pwrDiff = Math.round((pwrCurrent - pwrFactory) * 10) / 10;
  const pwrTrend = pwrDiff > 0 ? "POSITIVE" : pwrDiff < 0 ? "NEGATIVE" : "NEUTRAL";

  async function handleImageUpload(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;

    // Validação de tipo e tamanho
    if (!file.type.startsWith("image/")) {
      alert("Selecione um arquivo de imagem válido.");
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      alert("A imagem deve ter no máximo 5MB.");
      return;
    }

    setUploading(true);
    try {
      const url = await uploadVehicleImage(vehicle.id, file);
      // Bug 2 corrigido: atualiza estado local imediatamente sem aguardar refetch
      setLocalImageUrl(url);
      onImageUploaded?.();
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message;
      alert(msg ?? "Erro ao fazer upload da imagem.");
    } finally {
      setUploading(false);
      if (fileRef.current) fileRef.current.value = "";
    }
  }

  // Bug 7 corrigido: navegação separada do botão de upload
  function handleCardClick() {
    navigate(`/vehicle/${vehicle.id}`);
  }

  function handleUploadClick(e: React.MouseEvent) {
    e.stopPropagation(); // impede propagação para o card
    fileRef.current?.click();
  }

  return (
    <div className="vc-card">
      {/* ── Imagem ──────────────────────────────────────── */}
      <div className="vc-image" onClick={handleCardClick}>
        {localImageUrl ? (
          <img src={localImageUrl} alt={vehicle.name} />
        ) : (
          <div
            className="vc-image-placeholder"
            onClick={(e) => { e.stopPropagation(); fileRef.current?.click(); }}
          >
            <svg width="36" height="36" viewBox="0 0 24 24" fill="none"
              stroke="#444" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
              <rect x="3" y="3" width="18" height="18" rx="2"/>
              <circle cx="8.5" cy="8.5" r="1.5"/>
              <polyline points="21 15 16 10 5 21"/>
            </svg>
            <span>Adicionar foto</span>
          </div>
        )}

        <button
          className={`vc-upload-btn ${uploading ? "vc-upload-btn--loading" : ""}`}
          onClick={handleUploadClick}
          title="Trocar foto"
          disabled={uploading}
        >
          {uploading ? (
            <div className="vc-upload-spinner" />
          ) : (
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
              <polyline points="17 8 12 3 7 8"/>
              <line x1="12" y1="3" x2="12" y2="15"/>
            </svg>
          )}
        </button>

        <input
          ref={fileRef}
          type="file"
          accept="image/*"
          style={{ display: "none" }}
          onChange={handleImageUpload}
        />

        {vehicle.ownerUsername && (
          <div className="vc-owner-badge">
            <span className="vc-owner-label">Owner</span>
            <span className="vc-owner-name">{vehicle.ownerUsername}</span>
          </div>
        )}
      </div>

      {/* ── Conteúdo ────────────────────────────────────── */}
      <div className="vc-content" onClick={handleCardClick}>
        <div className="vc-title-row">
          <div>
            <h2 className="vc-name">{vehicle.name}</h2>
            <p className="vc-engine">{vehicle.engine}</p>
          </div>
          <div className="vc-badges">
            {vehicle.modificationsCount != null && vehicle.modificationsCount > 0 && (
              <span className="vc-mods-badge">{vehicle.modificationsCount} mods</span>
            )}
            {vehicle.driveType && (
              <span className={`vc-drive-badge drive-${vehicle.driveType.toLowerCase()}`}>
                {vehicle.driveType}
              </span>
            )}
          </div>
        </div>

        <div className="vc-bars">
          <PerformanceBar label="Horsepower" factoryValue={vehicle.factoryHorsePower}
            currentValue={vehicle.currentHorsePower} diff={vehicle.horsepowerDiff}
            trend={vehicle.horsepowerTrend} unit="hp" />
          <PerformanceBar label="Torque" factoryValue={vehicle.factoryTorque}
            currentValue={vehicle.currentTorque} diff={vehicle.torqueDiff}
            trend={vehicle.torqueTrend} unit="lb-ft" />
          <PerformanceBar label="Weight" factoryValue={vehicle.factoryWeight}
            currentValue={vehicle.currentWeight} diff={vehicle.weightDiff}
            trend={vehicle.weightTrend} unit="kg" />
          <PerformanceBar label="Power / Weight" factoryValue={pwrFactory}
            currentValue={pwrCurrent} diff={pwrDiff} trend={pwrTrend} unit=" hp/t" />
        </div>

        {(vehicle.acceleration0to100 || vehicle.displacement) && (
          <div className="vc-extra-stats">
            {vehicle.displacement && (
              <div className="vc-stat">
                <span className="vc-stat-val">{vehicle.displacement}L</span>
                <span className="vc-stat-label">Cilindrada</span>
              </div>
            )}
            {vehicle.acceleration0to100 && (
              <div className="vc-stat">
                <span className="vc-stat-val">{vehicle.acceleration0to100}s</span>
                <span className="vc-stat-label">0–100</span>
              </div>
            )}
            {vehicle.transmissionModel && (
              <div className="vc-stat">
                <span className="vc-stat-val">{vehicle.transmissionModel.replace(/_/g, " ")}</span>
                <span className="vc-stat-label">Câmbio</span>
              </div>
            )}
            {vehicle.powerGainPercentage != null && vehicle.powerGainPercentage > 0 && (
              <div className="vc-stat">
                <span className="vc-stat-val" style={{ color: "#00ffae" }}>
                  +{Math.round(vehicle.powerGainPercentage)}%
                </span>
                <span className="vc-stat-label">Ganho HP</span>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default VehicleCard;