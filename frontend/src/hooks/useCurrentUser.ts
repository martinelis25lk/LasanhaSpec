import { useState, useEffect } from "react";

export interface CurrentUser {
  email: string;
  username: string;
  role: "ROLE_USER" | "ROLE_ADMIN" | string;
  profileImageUrl: string | null;
}

function decodeJwtPayload(token: string): Record<string, unknown> | null {
  try {
    const base64 = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
    return JSON.parse(atob(base64));
  } catch {
    return null;
  }
}

function parseUser(token: string | null): CurrentUser | null {
  if (!token) return null;
  const payload = decodeJwtPayload(token);
  if (!payload) return null;

  const email = (payload.sub as string) ?? "";
  const username = (payload.username as string) ?? email.split("@")[0];
  const role = (payload.role as string) ?? "ROLE_USER";
  const profileImageUrl = (payload.profileImageUrl as string) ?? null;

  return { email, username, role, profileImageUrl };
}

export function useCurrentUser(): CurrentUser | null {
  const [user, setUser] = useState<CurrentUser | null>(() =>
    parseUser(localStorage.getItem("token"))
  );

  useEffect(() => {
    // Bug 6 corrigido: escuta mudanças no storage (login/logout em outras abas)
    function onStorage(e: StorageEvent) {
      if (e.key === "token") {
        setUser(parseUser(e.newValue));
      }
    }
    window.addEventListener("storage", onStorage);
    return () => window.removeEventListener("storage", onStorage);
  }, []);

  return user;
}