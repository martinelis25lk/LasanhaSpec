import { Link } from "react-router-dom";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../api/authApi";
import "./LoginPage.css";

export default function LoginPage() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  async function handleSubmit(event: React.FormEvent) {
  event.preventDefault();

  try {
    localStorage.removeItem("token");

    const token = await login({ email, password });

    localStorage.setItem("token", token);

    navigate("/menu");
  } catch (error) {
    console.error("Erro no login:", error);
    setError("Usuário ou senha inválidos.");
  }
}

  return (
    <main className="login-page">
      <section className="login-card">
        <h1 className="login-title">LasanhaSpec</h1>
        <p className="login-subtitle">
          Entre para acessar sua garagem digital.
        </p>

        <form className="login-form" onSubmit={handleSubmit}>
          <input
            className="login-input"
            placeholder="Usuário ou email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />

          <input
            className="login-input"
            type="password"
            placeholder="Senha"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <button className="login-button" type="submit">
            Entrar
          </button>

          <p className="login-register-link">
             Não tem conta? 
              <Link
              to="/register" onClick={() => localStorage.removeItem("token")}>
              Cadastre-se
              </Link>
          </p>

          {error && <p className="login-error">{error}</p>}
        </form>
      </section>
    </main>
  );
}