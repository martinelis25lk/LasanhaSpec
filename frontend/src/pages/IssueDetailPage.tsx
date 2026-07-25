import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  getIssueDetail, voteOnIssue, reportOccurrence, approveIssue, rejectIssue,
  type ChronicIssueDetail,
} from "../api/chronicIssueApi";
import { useCurrentUser } from "../hooks/useCurrentUser";
import { getUserVehicles } from "../api/vehicleApi";
import "./IssueDetailPage.css";

import CommentsSection from "../components/ChronicIssue/CommentsSection";

const complexityColor: Record<string, string> = {
  DIY: "#00ffae", INTERMEDIATE: "#f59e0b", PROFESSIONAL: "#ef4444",
};
const complexityLabel: Record<string, string> = {
  DIY: "DIY", INTERMEDIATE: "Intermediate", PROFESSIONAL: "Professional",
};

export default function IssueDetailPage() {
  const { id } = useParams<{ id: string }>();
  const issueId = Number(id);
  const navigate = useNavigate();
  const user = useCurrentUser();
  const isAdmin = user?.role === "ROLE_ADMIN";

  const [detail, setDetail] = useState<ChronicIssueDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [voting, setVoting] = useState(false);
  const [showOccForm, setShowOccForm] = useState(false);
  const [userVehicles, setUserVehicles] = useState<any[]>([]);
  const [occForm, setOccForm] = useState({ vehicleId: "", millageAtOccurrence: "", repairCost: "", description: "" });
  const [submitting, setSubmitting] = useState(false);
  const [approving, setApproving] = useState(false);

  function load() {
    setLoading(true);
    getIssueDetail(issueId)
      .then(setDetail)
      .catch(() => setError("Erro ao carregar issue."))
      .finally(() => setLoading(false));
  }

  useEffect(() => { load(); }, [issueId]);
  useEffect(() => {
    if (showOccForm) getUserVehicles().then(setUserVehicles).catch(() => {});
  }, [showOccForm]);

  async function handleVote(type: "USEFUL" | "NOT_USEFUL") {
    setVoting(true);
    try { await voteOnIssue(issueId, type); load(); }
    catch (e: any) { alert(e?.response?.data?.message ?? "Erro ao votar."); }
    finally { setVoting(false); }
  }

  async function handleOccurrence() {
    if (!occForm.vehicleId) return alert("Selecione um veículo.");
    setSubmitting(true);
    try {
      await reportOccurrence(issueId, {
        vehicleId: Number(occForm.vehicleId),
        millageAtOccurrence: Number(occForm.millageAtOccurrence),
        repairCost: Number(occForm.repairCost),
        description: occForm.description,
      });
      setShowOccForm(false);
      load();
    } catch (e: any) {
      alert(e?.response?.data?.message ?? "Erro ao reportar ocorrência.");
    } finally { setSubmitting(false); }
  }

  if (loading) return <div className="idp-loading"><div className="idp-spinner" /><p>Carregando...</p></div>;
  if (error || !detail) return <p className="idp-error">{error || "Issue não encontrado."}</p>;

  const card = detail.chronicIssueCardDTO;
  const total = (card.usefulVotes ?? 0) + (card.notUsefulVotes ?? 0);
  const pct = total > 0 ? Math.round((card.usefulVotes / total) * 100) : 0;

  return (
    <div className="idp-page">
      <button className="idp-back" onClick={() => navigate(-1)}>← Back to Chronic Issues</button>

      <div className="idp-header-card">
        <div className="idp-title-row">
          <span className={`idp-sev sev-${card.severity?.toLowerCase()}`}>{card.severity}</span>
          <h1>{card.title}</h1>
        </div>
        <div className="idp-stats-grid">
          <div className="idp-stat"><span className="idp-stat-label">Mileage Range</span>
            <span className="idp-stat-val">{card.millageMin?.toLocaleString()} – {card.millageMax?.toLocaleString()} mi</span></div>
          <div className="idp-stat"><span className="idp-stat-label">Repair Cost</span>
            <span className="idp-stat-val">${card.costMin?.toLocaleString()} – ${card.costMax?.toLocaleString()}</span></div>
          <div className="idp-stat"><span className="idp-stat-label">Confirmed Cases</span>
            <span className="idp-stat-val" style={{ color: "#00ffae" }}>{card.occurrences ?? 0}</span></div>
          <div className="idp-stat"><span className="idp-stat-label">Usefulness</span>
            <span className="idp-stat-val">{pct}% helpful</span></div>
        </div>
      </div>

      <div className="idp-section">
        <h2>⚠ Issue Overview</h2>
        <p>{card.description}</p>
      </div>

      <div className="idp-section">
        <h2>Technical Details</h2>
        <div className="idp-tech-grid">
          <div>
            <span className="idp-tech-label">Affected Engines</span>
            <div className="idp-tech-tags">
              {detail.affectedEngines?.split(",").map((e) => (
                <span key={e} className="idp-tag">{e.trim()}</span>
              ))}
            </div>
          </div>
          <div>
            <span className="idp-tech-label">Affected Model Years</span>
            <span className="idp-tech-val">{detail.affectedYears}</span>
          </div>
          <div>
            <span className="idp-tech-label">Repair Complexity</span>
            <span className="idp-tech-complexity" style={{ color: complexityColor[detail.repairComplexity] ?? "#f0f0f0" }}>
              {complexityLabel[detail.repairComplexity] ?? detail.repairComplexity}
            </span>
          </div>
        </div>
      </div>

      {detail.symptoms?.length > 0 && (
        <div className="idp-section">
          <h2>🔧 Symptoms</h2>
          <ul className="idp-list">
            {detail.symptoms.map((s, i) => <li key={i}>{s}</li>)}
          </ul>
        </div>
      )}

      {detail.preventiveMaintenance?.length > 0 && (
        <div className="idp-section">
          <h2>🛡 Preventive Maintenance</h2>
          <ul className="idp-list">
            {detail.preventiveMaintenance.map((s, i) => <li key={i}>{s}</li>)}
          </ul>
        </div>
      )}

      {detail.occurrenceReports?.length > 0 && (
        <div className="idp-section">
          <h2>👥 Community Reports</h2>
          <div className="idp-reports">
            {detail.occurrenceReports.map((r, i) => (
              <div key={i} className="idp-report">
                <div className="idp-report-meta">
                  <span>{r.username ?? "Usuário"}</span>
                  <span>{r.millageAtOccurrence?.toLocaleString()} mi · ${r.repairCost?.toLocaleString()}</span>
                </div>
                <p>{r.description}</p>
              </div>
            ))}
          </div>
        </div>
      )}

      <CommentsSection issueId={issueId} />

      <div className="idp-footer">
        <div className="idp-vote-row">
          <button className="idp-vote-useful" onClick={() => handleVote("USEFUL")} disabled={voting}>
            👍 Useful ({card.usefulVotes ?? 0})
          </button>
          <button className="idp-vote-not" onClick={() => handleVote("NOT_USEFUL")} disabled={voting}>
            👎 Not Useful ({card.notUsefulVotes ?? 0})
          </button>
          <button className="idp-experienced-btn" onClick={() => setShowOccForm(true)}>
            I experienced this issue
          </button>
        </div>
        {isAdmin && (
          <div className="idp-admin-row">
            <button className="idp-approve" onClick={async () => { setApproving(true); try { await approveIssue(issueId); load(); } catch { alert("Erro."); } finally { setApproving(false); } }} disabled={approving}>✓ Approve</button>
            <button className="idp-reject" onClick={async () => { if (!confirm("Rejeitar?")) return; setApproving(true); try { await rejectIssue(issueId); load(); } catch { alert("Erro."); } finally { setApproving(false); } }} disabled={approving}>✗ Reject</button>
          </div>
        )}
      </div>

      {showOccForm && (
        <div className="idp-modal-overlay" onClick={() => setShowOccForm(false)}>
          <div className="idp-modal" onClick={(e) => e.stopPropagation()}>
            <h3>Report Occurrence</h3>
            <p className="idp-modal-sub">Registre que você experienciou este problema no seu veículo.</p>
            <label>Seu veículo</label>
            <select value={occForm.vehicleId} onChange={(e) => setOccForm({ ...occForm, vehicleId: e.target.value })}>
              <option value="">Selecione...</option>
              {userVehicles.map((v: any) => (
                <option key={v.id} value={v.id}>{v.name ?? `Veículo #${v.id}`}</option>
              ))}
            </select>
            <label>Quilometragem na ocorrência</label>
            <input type="number" placeholder="ex: 75000" value={occForm.millageAtOccurrence}
              onChange={(e) => setOccForm({ ...occForm, millageAtOccurrence: e.target.value })} />
            <label>Custo do reparo (R$)</label>
            <input type="number" placeholder="ex: 3500" value={occForm.repairCost}
              onChange={(e) => setOccForm({ ...occForm, repairCost: e.target.value })} />
            <label>Descrição</label>
            <textarea placeholder="Descreva o que aconteceu..." value={occForm.description}
              onChange={(e) => setOccForm({ ...occForm, description: e.target.value })} />
            <div className="idp-modal-actions">
              <button className="idp-modal-cancel" onClick={() => setShowOccForm(false)}>Cancelar</button>
              <button className="idp-modal-submit" onClick={handleOccurrence} disabled={submitting}>
                {submitting ? "Enviando..." : "Confirmar"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}