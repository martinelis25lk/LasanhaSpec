import { NavLink, Outlet } from "react-router-dom";
import { Link } from "react-router-dom";
import UserMenu from "../../components/UserMenu/UserMenu";
import "./AppLayout.css";

export default function AppLayout() {
  return (
    <div className="app-layout">
      <header className="navbar">
        <Link to="/menu" className="navbar-logo">LasanhaSpec</Link>

        <nav className="navbar-links">
          
          <NavLink to="/feed">Feed</NavLink>
          <NavLink to="/garage">Garage</NavLink>
          <NavLink to="/catalog/brands">Catalog</NavLink>
          <NavLink to="/chronic-issues">Chronic Issues</NavLink>
          <NavLink to="/explore" className="nav-wip" onClick={(e) => e.preventDefault()}>
            Explore <span className="nav-wip-badge">em breve</span>
          </NavLink>
        </nav>

        <div className="navbar-right">
          <input className="topbar-search" placeholder="Search vehicles, builds, issues..." />
          <button className="topbar-notif" aria-label="Notificações">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
              <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
            </svg>
            <span className="topbar-notif-dot" />
          </button>
          <UserMenu />
        </div>
      </header>

      <main className="content">
        <Outlet />
      </main>
    </div>
  );
}