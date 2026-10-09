import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getAllModelsWithIssues, type VehicleChronicSummary } from "../api/chronicIssueApi";
import { getCatalogVehicles } from "../api/vehicleApi";
import { useCurrentUser } from "../hooks/useCurrentUser";
import { EmptyState, ErrorState } from "../components/StateViews/StateViews";
import type { CatalogVehicle } from "../types/catalog";
import "./ChronicIssuesPage.css";

export default function ChronicIssuesPage() {
  const navigate = useNavigate();
  const user = useCurrentUser();
  const isAdmin = user?.role === "ROLE_ADMIN";

  const [models, setModels] = useState<VehicleChronicSummary[]>([]);
  const [catalog, setCatalog] = useState<CatalogVehicle[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");

  async function load() {
    setLoading(true);
    setError("");
    try {
      setModels(await getAllModelsWithIssues());
    } catch {
      setError("Não foi possível carregar os modelos.");
      setLoading(false);
      return;
    }

    // Admin também precisa ver modelos do catálogo SEM issues aprovados,
    // senão não tem como cadastrar o primeiro issue de um modelo.
    if (isAdmin) {
      try {
        setCatalog(await getCatalogVehicles());
      } catch {
        setCatalog([]);
      }
    }
    setLoading(false);
  }

  useEffect(() => { load(); }, [isAdmin]);

  const filtered = models.filter((m) =>
    m.name.toLowerCase().includes(search.toLowerCase())
  );

  const modelIdsWithIssues = new Set(models.map((m) => m.id));
  const modelsWithoutIssues = catalog.filter(
    (c) =>
      !modelIdsWithIssues.has(c.id) &&
      `${c.brand} ${c.model} ${c.year}`.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="ci-page">
      <div className="ci-header">
        <h1>⚠ Chronic Issues</h1>
        <p>Problemas crônicos documentados e validados pela comunidade</p>
      </div>

      <div className="ci-search-row">
        <div className="ci-search-wrap">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
          <input
            className="ci-search"
            placeholder="Buscar modelo..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <span className="ci-count">{filtered.length} modelo{filtered.length !== 1 ? "s" : ""}</span>
      </div>

      {loading ? (
        <div className="ci-loading"><div className="ci-spinner" /><p>Carregando...</p></div>
      ) : error ? (
        <ErrorState message={error} onRetry={load} />
      ) : (
        <>
          {models.length === 0 ? (
            <EmptyState
              icon="🔧"
              title="Nenhum problema crônico documentado ainda"
              description={
                isAdmin
                  ? "Escolha um modelo abaixo para cadastrar o primeiro issue."
                  : "Os issues precisam ser aprovados por um admin para aparecer aqui. Volte em breve!"
              }
            />
          ) : filtered.length === 0 ? (
            <p className="ci-empty">Nenhum modelo encontrado para essa busca.</p>
          ) : (
            <div className="ci-grid">
              {filtered.map((model) => (
                <div key={model.id} className="ci-card"
                  onClick={() => navigate(`/chronic-issues/models/${model.id}`)}>
                  <div className="ci-card-image">
                    {model.imageUrl
                      ? <img src={model.imageUrl} alt={model.name} />
                      : <div className="ci-card-img-placeholder" />}
                  </div>
                  <div className="ci-card-body">
                    <h2 className="ci-card-name">{model.name}</h2>
                    <div className="ci-card-stats">
                      <div className="ci-stat">
                        <span className="ci-stat-val" style={{ color: "#f59e0b" }}>
                          {model.reliabilityScore?.toFixed(1)}/10
                        </span>
                        <span className="ci-stat-label">Reliability</span>
                      </div>
                      <div className="ci-stat">
                        <span className="ci-stat-val">${model.avgAnnualCost?.toFixed(0)}</span>
                        <span className="ci-stat-label">Avg. Annual Cost</span>
                      </div>
                      <div className="ci-stat">
                        <span className="ci-stat-val" style={{ color: "#ef4444" }}>
                          {model.criticalFailureRate?.toFixed(0)}%
                        </span>
                        <span className="ci-stat-label">Critical Failures</span>
                      </div>
                    </div>
                    {model.topIssueNames?.length > 0 && (
                      <div className="ci-card-tags">
                        {model.topIssueNames.map((name) => (
                          <span key={name} className="ci-tag">{name}</span>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* ── Admin: modelos do catálogo sem issues aprovados ── */}
          {isAdmin && (
            <section style={{ marginTop: 32 }}>
              <h2 style={{ fontSize: 16, color: "#fff", margin: "0 0 4px" }}>
                Modelos sem issues aprovados
              </h2>
              <p style={{ fontSize: 13, color: "#555", margin: "0 0 14px" }}>
                Abra um modelo e use “+ Report Issue” para cadastrar um problema crônico.
              </p>
              {catalog.length === 0 ? (
                <EmptyState
                  icon="📚"
                  title="O catálogo está vazio"
                  description="Cadastre modelos no catálogo antes de documentar problemas crônicos."
                  action={{ label: "Ir para o catálogo", onClick: () => navigate("/catalog") }}
                />
              ) : modelsWithoutIssues.length === 0 ? (
                <p className="ci-empty">Todos os modelos do catálogo já têm issues.</p>
              ) : (
                <div className="ci-tags" style={{ display: "flex", flexWrap: "wrap", gap: 8 }}>
                  {modelsWithoutIssues.map((c) => (
                    <button
                      key={c.id}
                      className="ci-tag"
                      style={{ cursor: "pointer", border: "1px solid #2a2a35", background: "transparent", padding: "8px 12px" }}
                      onClick={() => navigate(`/chronic-issues/models/${c.id}`)}
                    >
                      {c.brand} {c.model} {c.year}
                    </button>
                  ))}
                </div>
              )}
            </section>
          )}
        </>
      )}
    </div>
  );
}