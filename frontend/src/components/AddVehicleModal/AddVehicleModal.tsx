import { useEffect, useState } from "react";
import { getCatalogVehicles, createUserVehicle } from "../../api/vehicleApi";
import { PerformanceBar } from "../PerformanceBar/PerformanceBar";
import type { CatalogVehicle } from "../../types/catalog";
import "./AddVehicleModal.css";

interface Props {
  onClose: () => void;
  onSuccess: () => void;
}

type Step = "select" | "customize";

export default function AddVehicleModal({ onClose, onSuccess }: Props) {
  const [catalog, setCatalog] = useState<CatalogVehicle[]>([]);
  const [loadingCatalog, setLoadingCatalog] = useState(true);
  const [search, setSearch] = useState("");
  const [step, setStep] = useState<Step>("select");
  const [selected, setSelected] = useState<CatalogVehicle | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [catalogError, setCatalogError] = useState(false);


  // Form — herdado do catálogo, editável
  const [nickName, setNickName] = useState("");
  const [hp, setHp] = useState(0);
  const [torque, setTorque] = useState(0);
  const [weight, setWeight] = useState(0);

  useEffect(() => {
    getCatalogVehicles()
      .then(setCatalog)
      .catch(() => setError("Erro ao carregar catálogo."))
      .finally(() => setLoadingCatalog(false));
  }, []);

  const filtered = catalog.filter((c) =>
    `${c.brand} ${c.model} ${c.year} ${c.engineCode}`
      .toLowerCase()
      .includes(search.toLowerCase())
  );

    function loadCatalog() {
    setLoadingCatalog(true);
    setCatalogError(false);
    getCatalogVehicles()
      .then(setCatalog)
      .catch(() => setCatalogError(true))
      .finally(() => setLoadingCatalog(false));
  }

  useEffect(() => { loadCatalog(); }, []);

  function selectCatalog(c: CatalogVehicle) {
    setSelected(c);
    setNickName(`${c.brand} ${c.model}`);
    setHp(c.factoryHorsepower ?? 0);
    setTorque(c.factoryTorque ?? 0);
    setWeight(c.factoryWeight ?? 0);
    setError("");
    setStep("customize");
  }

  // Diffs calculados em tempo real
  const hpDiff = hp - (selected?.factoryHorsepower ?? 0);
  const torqueDiff = torque - (selected?.factoryTorque ?? 0);
  const weightDiff = weight - (selected?.factoryWeight ?? 0);

  const hpTrend = hpDiff > 0 ? "POSITIVE" : hpDiff < 0 ? "NEGATIVE" : "NEUTRAL";
  const torqueTrend = torqueDiff > 0 ? "POSITIVE" : torqueDiff < 0 ? "NEGATIVE" : "NEUTRAL";
  const weightTrend = weightDiff < 0 ? "POSITIVE" : weightDiff > 0 ? "NEGATIVE" : "NEUTRAL";

  async function handleSubmit() {
    if (!selected) return;
    if (!nickName.trim()) { setError("Dê um apelido ao veículo."); return; }
    if (hp <= 0 || torque <= 0 || weight <= 0) {
      setError("HP, torque e peso devem ser maiores que zero.");
      return;
    }
    setSubmitting(true);
    setError("");
    try {
      await createUserVehicle({
        vehicleCatalogModelId: selected.id,
        nickName: nickName.trim(),
        currentHorsePower: hp,
        currentTorque: torque,
        currentWeight: weight,
      });
      onSuccess();
      onClose();
    } catch {
      setError("Erro ao adicionar veículo. Tente novamente.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="avm-overlay" onClick={onClose}>
      <div className="avm-box" onClick={(e) => e.stopPropagation()}>

        {/* ── Header ──────────────────────────────────── */}
        <div className="avm-header">
          <div className="avm-header-left">
            {step === "customize" && (
              <button className="avm-back" onClick={() => setStep("select")}>
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                  stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                  <polyline points="15 18 9 12 15 6"/>
                </svg>
              </button>
            )}
            <div>
              <h2>{step === "select" ? "Add Vehicle" : "Customize Build"}</h2>
              <p>
                {step === "select"
                  ? "Selecione o modelo base do catálogo"
                  : `${selected?.brand} ${selected?.model} ${selected?.year}`}
              </p>
            </div>
          </div>
          <button className="avm-close" onClick={onClose}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>

        {/* ── Steps indicator ─────────────────────────── */}
        <div className="avm-steps">
          <div className={`avm-step ${step === "select" ? "avm-step--active" : "avm-step--done"}`}>
            <span className="avm-step-num">1</span>
            <span>Modelo base</span>
          </div>
          <div className="avm-step-line" />
          <div className={`avm-step ${step === "customize" ? "avm-step--active" : ""}`}>
            <span className="avm-step-num">2</span>
            <span>Sua build</span>
          </div>
        </div>

        {/* ── Step 1: Selecionar do catálogo ──────────── */}
        {step === "select" && (
          <>
            <div className="avm-search-wrap">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
              </svg>
              <input
                className="avm-search"
                placeholder="Buscar marca, modelo, ano..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                autoFocus
              />
            </div>

            {loadingCatalog ? (
              <div className="avm-loading">
                <div className="avm-spinner" />
                <p>Carregando catálogo...</p>
              </div>
            ) : catalogError ? (
              <div className="avm-catalog-list">
                <p className="avm-empty">
                  Não foi possível carregar o catálogo.{" "}
                  <button type="button" onClick={loadCatalog}
                    style={{ all: "unset", cursor: "pointer", color: "#5f7a4a", textDecoration: "underline" }}>
                    Tentar de novo
                  </button>
                </p>
              </div>
            ) : (
              <div className="avm-catalog-list">
                {catalog.length === 0 ? (
                  <p className="avm-empty">
                    O catálogo ainda está vazio. Peça a um admin para cadastrar os primeiros modelos.
                  </p>
                ) : filtered.length === 0 ? (
                  <p className="avm-empty">Nenhum modelo encontrado para essa busca.</p>
                ) : (
                  filtered.map((c) => (
                    <button key={c.id} className="avm-catalog-item" onClick={() => selectCatalog(c)}>
                      <div className="avm-ci-left">
                        <div className="avm-ci-name">{c.brand} {c.model}</div>
                        <div className="avm-ci-meta">{c.engineCode} • {c.year}</div>
                      </div>
                      <div className="avm-ci-specs">
                        <span>{c.factoryHorsepower}hp</span>
                        <span>{c.factoryTorque}lb-ft</span>
                        <span>{c.factoryWeight}kg</span>
                        {c.driveType && (
                          <span className={`avm-drive drive-${c.driveType.toLowerCase()}`}>
                            {c.driveType}
                          </span>
                        )}
                      </div>
                    </button>
                  ))
                )}
              </div>
            )}
          </>
        )}

        {/* ── Step 2: Customizar ──────────────────────── */}
        {step === "customize" && selected && (
          <div className="avm-customize">
            {/* Apelido */}
            <div className="avm-field">
              <label>Apelido do veículo</label>
              <input
                value={nickName}
                onChange={(e) => setNickName(e.target.value)}
                placeholder="Ex: Meu Skyline"
                maxLength={30}
              />
            </div>

            {/* Specs editáveis */}
            <div className="avm-specs-section">
              <p className="avm-specs-title">
                Especificações atuais
                <span>Edite os valores do seu veículo</span>
              </p>

              <div className="avm-specs-grid">
                <div className="avm-spec-field">
                  <label>
                    Potência (hp)
                    {hpDiff !== 0 && (
                      <span className={`avm-spec-diff ${hpDiff > 0 ? "positive" : "negative"}`}>
                        {hpDiff > 0 ? "+" : ""}{hpDiff}hp
                      </span>
                    )}
                  </label>
                  <div className="avm-spec-input-wrap">
                    <span className="avm-spec-factory">{selected.factoryHorsepower}hp original</span>
                    <input
                      type="number"
                      value={hp}
                      onChange={(e) => setHp(Number(e.target.value))}
                      min={1}
                    />
                  </div>
                </div>

                <div className="avm-spec-field">
                  <label>
                    Torque (lb-ft)
                    {torqueDiff !== 0 && (
                      <span className={`avm-spec-diff ${torqueDiff > 0 ? "positive" : "negative"}`}>
                        {torqueDiff > 0 ? "+" : ""}{torqueDiff}lb-ft
                      </span>
                    )}
                  </label>
                  <div className="avm-spec-input-wrap">
                    <span className="avm-spec-factory">{selected.factoryTorque}lb-ft original</span>
                    <input
                      type="number"
                      value={torque}
                      onChange={(e) => setTorque(Number(e.target.value))}
                      min={1}
                    />
                  </div>
                </div>

                <div className="avm-spec-field">
                  <label>
                    Peso (kg)
                    {weightDiff !== 0 && (
                      <span className={`avm-spec-diff ${weightDiff < 0 ? "positive" : "negative"}`}>
                        {weightDiff > 0 ? "+" : ""}{weightDiff}kg
                      </span>
                    )}
                  </label>
                  <div className="avm-spec-input-wrap">
                    <span className="avm-spec-factory">{selected.factoryWeight}kg original</span>
                    <input
                      type="number"
                      value={weight}
                      onChange={(e) => setWeight(Number(e.target.value))}
                      min={1}
                    />
                  </div>
                </div>
              </div>
            </div>

            {/* Preview das barras NFS em tempo real */}
            {(hpDiff !== 0 || torqueDiff !== 0 || weightDiff !== 0) && (
              <div className="avm-preview">
                <p className="avm-preview-title">Preview da build</p>
                <div className="avm-preview-bars">
                  <PerformanceBar
                    label="Horsepower"
                    factoryValue={selected.factoryHorsepower}
                    currentValue={hp}
                    diff={hpDiff}
                    trend={hpTrend}
                    unit="hp"
                  />
                  <PerformanceBar
                    label="Torque"
                    factoryValue={selected.factoryTorque}
                    currentValue={torque}
                    diff={torqueDiff}
                    trend={torqueTrend}
                    unit="lb-ft"
                  />
                  <PerformanceBar
                    label="Weight"
                    factoryValue={selected.factoryWeight}
                    currentValue={weight}
                    diff={weightDiff}
                    trend={weightTrend}
                    unit="kg"
                  />
                </div>
              </div>
            )}
          </div>
        )}

        {/* ── Footer ──────────────────────────────────── */}
        {error && <p className="avm-error">{error}</p>}

        {step === "customize" && (
          <div className="avm-footer">
            <button className="avm-btn-cancel" onClick={onClose}>Cancelar</button>
            <button className="avm-btn-submit" onClick={handleSubmit} disabled={submitting}>
              {submitting ? "Adicionando..." : "Adicionar à Garagem"}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}