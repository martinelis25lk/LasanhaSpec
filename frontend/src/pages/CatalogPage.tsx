import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  getCatalogVehicles,
  createCatalogVehicle,
  updateCatalogVehicle,
  deleteCatalogVehicle,
} from "../api/vehicleApi";
import { useCurrentUser } from "../hooks/useCurrentUser";
import type { CatalogVehicle, CatalogVehicleForm } from "../types/catalog";
import CatalogFormModal from "../components/CatalogFormModal/CatalogFormModal";
import "./CatalogPage.css";


export default function CatalogPage() {
  const user = useCurrentUser();
  const isAdmin = user?.role === "ROLE_ADMIN";

  const [vehicles, setVehicles] = useState<CatalogVehicle[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editing, setEditing] = useState<CatalogVehicle | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [error, setError] = useState("");
  const {brand} = useParams();


  

  async function load() {
    setLoading(true);
    try {
      setVehicles(await getCatalogVehicles());
    } catch {
      setError("Erro ao carregar catálogo.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, []);

  const filtered = vehicles.filter((v) => {
    const matchesSearch = '${v.brand} ${v.model} ${v.year} ${v.engineCode}'
      .toLowerCase().includes(search.toLowerCase());
    const matchesBrand = brand ? v.brand.toLowerCase() === brand.toLowerCase() : true;
    return matchesSearch && matchesBrand;
  
  })

  async function handleSubmit(form: CatalogVehicleForm) {
    if (editing) {
      await updateCatalogVehicle(editing.id, form);
    } else {
      await createCatalogVehicle(form);
    }
    setShowForm(false);
    setEditing(null);
    load();
  }

  async function handleDelete(id: number) {
    if (!confirm("Remover este modelo do catálogo? Esta ação não pode ser desfeita.")) return;
    setDeletingId(id);
    try {
      await deleteCatalogVehicle(id);
      load();
    } catch {
      alert("Erro ao remover.");
    } finally {
      setDeletingId(null);
    }
  }

  function openCreate() {
    setEditing(null);
    setShowForm(true);
  }

  function openEdit(v: CatalogVehicle) {
    setEditing(v);
    setShowForm(true);
  }

  return (
    <div className="catalog-page">

      {/* ── Header ──────────────────────────────────────── */}
      <div className="catalog-header">
        <div>
          <h1>Vehicle Catalog</h1>
          <p>Base de modelos de fábrica · {vehicles.length} modelos</p>
        </div>
        {isAdmin && (
          <button className="catalog-add-btn" onClick={openCreate}>
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            Add Model
          </button>
        )}
      </div>

      {/* ── Admin banner ─────────────────────────────────── */}
      {isAdmin && (
        <div className="catalog-admin-banner">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
          </svg>
          Você está logado como <strong>Admin</strong> — pode criar, editar e remover modelos do catálogo.
        </div>
      )}

      {/* ── Search ───────────────────────────────────────── */}
      <div className="catalog-search-row">
        <div className="catalog-search-wrap">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
          <input
            className="catalog-search"
            placeholder="Buscar marca, modelo, motor ou ano..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <span className="catalog-count">{filtered.length} resultado{filtered.length !== 1 ? "s" : ""}</span>
      </div>

      {/* ── Tabela ───────────────────────────────────────── */}
      {error && <p className="catalog-error">{error}</p>}

      {loading ? (
        <div className="catalog-loading">
          <div className="catalog-spinner" />
          <p>Carregando catálogo...</p>
        </div>
      ) : (
        <div className="catalog-table-wrap">
          <table className="catalog-table">
            <thead>
              <tr>
                <th>Marca / Modelo</th>
                <th>Ano</th>
                <th>Motor</th>
                <th>Aspiração</th>
                <th>HP</th>
                <th>Torque</th>
                <th>Peso</th>
                <th>Tração</th>
                <th>Câmbio</th>
                {isAdmin && <th className="th-actions">Ações</th>}
              </tr>
            </thead>
            <tbody>
              {filtered.length === 0 ? (
                <tr>
                  <td colSpan={isAdmin ? 10 : 9} className="catalog-empty">
                    Nenhum modelo encontrado.
                  </td>
                </tr>
              ) : (
                filtered.map((v) => (
                  <tr key={v.id}>
                    <td>
                      <div className="catalog-brand-model">
                        <span className="catalog-brand">{v.brand}</span>
                        <span className="catalog-model-name">{v.model}</span>
                      </div>
                    </td>
                    <td className="catalog-year">{v.year}</td>
                    <td className="catalog-engine">{v.engineCode}</td>
                    <td>
                      <span className={`catalog-asp-badge asp-${v.aspirationType?.toLowerCase()}`}>
                        {v.aspirationType?.replace("_", " ")}
                      </span>
                    </td>
                    <td className="catalog-value">{v.factoryHorsepower}<span className="catalog-unit">hp</span></td>
                    <td className="catalog-value">{v.factoryTorque}<span className="catalog-unit">Nm</span></td>
                    <td className="catalog-value">{v.factoryWeight}<span className="catalog-unit">kg</span></td>
                    <td>
                      <span className={`catalog-drive-badge drive-${v.driveType?.toLowerCase()}`}>
                        {v.driveType ?? "—"}
                      </span>
                    </td>
                    <td className="catalog-transmission">
                      {v.transmissionType?.replace("_", " ") ?? "—"}
                      {v.gearCount ? ` · ${v.gearCount}v` : ""}
                    </td>
                    {isAdmin && (
                      <td>
                        <div className="catalog-actions">
                          <button
                            className="catalog-btn-edit"
                            onClick={() => openEdit(v)}
                            title="Editar"
                          >
                            <svg width="13" height="13" viewBox="0 0 24 24" fill="none"
                              stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                            </svg>
                          </button>
                          <button
                            className="catalog-btn-delete"
                            onClick={() => handleDelete(v.id)}
                            disabled={deletingId === v.id}
                            title="Remover"
                          >
                            {deletingId === v.id ? "..." : (
                              <svg width="13" height="13" viewBox="0 0 24 24" fill="none"
                                stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <polyline points="3 6 5 6 21 6"/>
                                <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/>
                                <path d="M10 11v6M14 11v6"/>
                              </svg>
                            )}
                          </button>
                        </div>
                      </td>
                    )}
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* ── Modal de formulário ───────────────────────────── */}
      {showForm && (
        <CatalogFormModal
          editing={editing}
          onClose={() => { setShowForm(false); setEditing(null); }}
          onSubmit={handleSubmit}
        />
      )}
    </div>
  );
}