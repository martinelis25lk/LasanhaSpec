import { useCurrentUser } from "../hooks/useCurrentUser";
import "./MenuPage.css";

export default function MenuPage() {
  const user = useCurrentUser();

  return (
    <div className="menu-page-wrapper">
      <div className="menu-page">
        <div className="menu-overlay" />
        <div className="menu-content">
          <span className="menu-eyebrow">LasanhaSpec</span>
          <h1>Bem-vindo de volta{user?.username ? `, ${user.username}` : ""}</h1>
          <p>Por onde você quer começar?</p>
        </div>
      </div>
    </div>
  );
}