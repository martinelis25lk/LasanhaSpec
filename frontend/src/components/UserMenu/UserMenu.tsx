import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useCurrentUser } from "../../hooks/useCurrentUser";
import { clearVehicleCache } from "../../hooks/useUserVehicles";
import "./UserMenu.css";

const IconUser = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <circle cx="12" cy="8" r="4" /><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" />
  </svg>
);
const IconDashboard = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/>
    <rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/>
  </svg>
);
const IconSettings = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <circle cx="12" cy="12" r="3"/>
    <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/>
  </svg>
);
const IconLogout = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
    <polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/>
  </svg>
);
const IconChevron = ({ open }: { open: boolean }) => (
  <svg
    width="12" height="12" viewBox="0 0 24 24" fill="none"
    stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"
    style={{ transform: open ? "rotate(180deg)" : "rotate(0deg)", transition: "transform 0.2s ease" }}
  >
    <polyline points="6 9 12 15 18 9"/>
  </svg>
);

function Avatar({ src, username }: { src: string | null; username: string }) {
  const initials = username.slice(0, 2).toUpperCase();
  if (src) return <img className="um-avatar" src={src} alt={username} />;
  return <div className="um-avatar um-avatar--initials">{initials}</div>;
}

function RoleBadge({ role }: { role: string }) {
  const isAdmin = role === "ROLE_ADMIN";
  return (
    <span className={`um-badge ${isAdmin ? "um-badge--admin" : "um-badge--user"}`}>
      {isAdmin ? "Admin" : "Pro Member"}
    </span>
  );
}

export default function UserMenu() {
  const [open, setOpen] = useState(false);
  const wrapperRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();
  const user = useCurrentUser();

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  function handleLogout() {
  clearVehicleCache(); 
  localStorage.removeItem("token");
  navigate("/login");
}

  function go(path: string) {
    setOpen(false);
    navigate(path);
  }

  if (!user) return (
  <div style={{ width: 120, height: 36 }} />
);

  return (
    <div className="um-wrapper" ref={wrapperRef}>
      <button
        className={`um-trigger ${open ? "um-trigger--open" : ""}`}
        onClick={() => setOpen((v) => !v)}
        aria-expanded={open}
        aria-haspopup="menu"
      >
        <div className="um-trigger-info">
          <span className="um-trigger-name">{user.username}</span>
          <RoleBadge role={user.role} />
        </div>
        <Avatar src={user.profileImageUrl} username={user.username} />
        <IconChevron open={open} />
      </button>

      {open && (
        <div className="um-dropdown" role="menu">
          <div className="um-dropdown-header">
            <Avatar src={user.profileImageUrl} username={user.username} />
            <div>
              <p className="um-dh-name">{user.username}</p>
              <p className="um-dh-email">{user.email}</p>
            </div>
          </div>

          <div className="um-divider" />

          <button className="um-item" role="menuitem" onClick={() => go("/profile")}>
            <span className="um-item-icon"><IconUser /></span>
            My Profile
          </button>
          <button className="um-item" role="menuitem" onClick={() => go("/dashboard")}>
            <span className="um-item-icon"><IconDashboard /></span>
            Dashboard
          </button>
          <button className="um-item" role="menuitem" onClick={() => go("/account/settings")}>
            <span className="um-item-icon"><IconSettings /></span>
            Account Settings
          </button>

          <div className="um-divider" />

          <button className="um-item um-item--danger" role="menuitem" onClick={handleLogout}>
            <span className="um-item-icon"><IconLogout /></span>
            Logout
          </button>
        </div>
      )}
    </div>
  );
}