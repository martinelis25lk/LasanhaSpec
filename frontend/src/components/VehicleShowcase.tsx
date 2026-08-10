import { useState, useRef, useEffect} from "react";
import { Link } from "react-router-dom";
import type { VehicleCard } from "../types/vehicle";
import { PerformanceBar } from "./PerformanceBar/PerformanceBar";
import { uploadVehicleImage, getVehicleImages  } from "../api/vehicleApi";
import "./VehicleShowcase.css";






interface Props {
  vehicle: VehicleCard;
  onImageUploaded?: () => void;
}



function CoveredCarPlaceholder() {
  return (
    <svg viewBox="0 0 240 140" className="covered-car-placeholder">
      <ellipse cx="120" cy="128" rx="100" ry="8" fill="rgba(0,0,0,0.35)" />
      <path
        d="M20 100 Q20 40 120 32 Q220 40 220 100 Q220 118 200 118 L40 118 Q20 118 20 100 Z"
        fill="#1a1a1f" stroke="#333" strokeWidth="2"
      />
      <path d="M20 100 Q60 88 120 88 Q180 88 220 100" fill="none" stroke="#2a2a30" strokeWidth="1.5" />
      <text x="120" y="72" textAnchor="middle" fontSize="11" fill="#555" letterSpacing="1">
        SEM FOTO AINDA
      </text>
    </svg>
  );
}




            export function VehicleShowcase({ vehicle, onImageUploaded }: Props) {
        const fileRef = useRef<HTMLInputElement>(null);
        const [uploading, setUploading] = useState(false);
        const [photos, setPhotos] = useState<string[]>([]);
        const [photoIndex, setPhotoIndex] = useState(0);

        async function loadImages() {
            try {
            const images = await getVehicleImages(vehicle.id);
            const ordered = [...images].sort((a, b) => (b.primaryImage ? 1 : 0) - (a.primaryImage ? 1 : 0));
            setPhotos(ordered.map((img) => img.imageUrl));
            setPhotoIndex(0);
            } catch {
            // se falhar, cai pro fallback da imageUrl única que já veio no card
            setPhotos(vehicle.imageUrl ? [vehicle.imageUrl] : []);
            }
        }

        useEffect(() => {
            loadImages();
            // eslint-disable-next-line react-hooks/exhaustive-deps
        }, [vehicle.id]);

        function prevPhoto() {
            setPhotoIndex((i) => (i === 0 ? photos.length - 1 : i - 1));
        }
        function nextPhoto() {
            setPhotoIndex((i) => (i === photos.length - 1 ? 0 : i + 1));
        }

        function handleUploadClick() {
            fileRef.current?.click();
        }

        async function handleImageUpload(e: React.ChangeEvent<HTMLInputElement>) {
            const file = e.target.files?.[0];
            if (!file) return;

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
            await uploadVehicleImage(vehicle.id, file);
            await loadImages();
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





  const pwrFactory = vehicle.factoryWeight > 0
    ? Math.round((vehicle.factoryHorsePower / (vehicle.factoryWeight / 1000)) * 10) / 10 : 0;
  const pwrCurrent = vehicle.currentWeight > 0
    ? Math.round((vehicle.currentHorsePower / (vehicle.currentWeight / 1000)) * 10) / 10 : 0;

  return (
    <div className="showcase">
      <div className="showcase-header">
        <div>
          <span className="showcase-brand">{vehicle.engine}</span>
          <h2 className="showcase-name">{vehicle.name}</h2>
        </div>
        {vehicle.powerGainPercentage != null && vehicle.powerGainPercentage > 0 && (
          <span className="showcase-gain">⭐ Ganho: +{Math.round(vehicle.powerGainPercentage)}%</span>
        )}
      </div>

      <div className="showcase-frame">
        <span className="showcase-bolt tl" />
        <span className="showcase-bolt tr" />
        <span className="showcase-bolt bl" />
        <span className="showcase-bolt br" />

        <button
          className={`showcase-upload-btn ${uploading ? "showcase-upload-btn--loading" : ""}`}
          onClick={handleUploadClick}
          title="Adicionar foto"
          disabled={uploading}
        >
          {uploading ? (
            <div className="showcase-upload-spinner" />
          ) : (
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
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

        <div className="showcase-photo-area">
          {photos.length > 1 && (
            <button className="showcase-arrow left" onClick={prevPhoto} aria-label="Foto anterior">‹</button>
          )}

          {photos.length > 0 ? (
            <img src={photos[photoIndex]} alt={vehicle.name} className="showcase-photo" />
          ) : (
            <div onClick={handleUploadClick} style={{ cursor: "pointer" }}>
              <CoveredCarPlaceholder />
            </div>
          )}

          {photos.length > 1 && (
            <button className="showcase-arrow right" onClick={nextPhoto} aria-label="Próxima foto">›</button>
          )}
        </div>

        {photos.length > 1 && (
          <span className="showcase-photo-count">{photoIndex + 1} / {photos.length}</span>
        )}
      </div>

      <div className="showcase-stats">
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
          currentValue={pwrCurrent} diff={Math.round((pwrCurrent - pwrFactory) * 10) / 10}
          trend={pwrCurrent > pwrFactory ? "POSITIVE" : pwrCurrent < pwrFactory ? "NEGATIVE" : "NEUTRAL"}
          unit=" hp/t" />
      </div>

      <div className="showcase-tabs">
        <button className="showcase-tab" disabled title="Em breve">Histórico do carro</button>
        <button className="showcase-tab" disabled title="Em breve">Dyno</button>
        <Link to={`/vehicle/${vehicle.id}`} className="showcase-tab showcase-tab--active">
          Ver detalhes →
        </Link>
      </div>
    </div>
  );
}