import { useState, useEffect, useCallback, useRef } from "react";
import { getUserVehicles } from "../api/vehicleApi";
import type { VehicleCard } from "../types/vehicle";

// Cache de módulo — limpo explicitamente no logout
let cachedVehicles: VehicleCard[] | null = null;
let cacheTimestamp = 0;
const CACHE_TTL = 30 * 1000;

export function clearVehicleCache() {
  cachedVehicles = null;
  cacheTimestamp = 0;
}

export function useUserVehicles() {
  const now = Date.now();
  const hasValidCache = cachedVehicles !== null && now - cacheTimestamp < CACHE_TTL;

  const [vehicles, setVehicles] = useState<VehicleCard[]>(cachedVehicles ?? []);
  const [loading, setLoading] = useState(!hasValidCache);
  const [error, setError] = useState(false);
  const mounted = useRef(true);

  const fetchVehicles = useCallback(async (forceRefresh = false) => {
    const now2 = Date.now();
    const valid = cachedVehicles !== null && now2 - cacheTimestamp < CACHE_TTL;

    if (!forceRefresh && valid) {
      setVehicles(cachedVehicles!);
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(false);
    try {
      const data = await getUserVehicles();
      cachedVehicles = data;
      cacheTimestamp = Date.now();
      if (mounted.current) {
        setVehicles(data);
      }
    } catch (err) {
      console.error("Erro ao carregar veículos:", err);
      if (mounted.current) {
        setError(true);
      }
    } finally {
      if (mounted.current) {
        setLoading(false);
      }
    }
  }, []);

  useEffect(() => {
    mounted.current = true;
    fetchVehicles();
    return () => {
      mounted.current = false;
    };
  }, [fetchVehicles]);

  function refetch() {
    clearVehicleCache();
    fetchVehicles(true);
  }

  return { vehicles, loading, error, refetch };
}