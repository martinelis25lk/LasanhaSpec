export interface VehicleCard {
  id: number;
  name: string;
  engine: string;
  imageUrl: string;
  ownerUsername?: string;
  modCount?: number;

  factoryHorsePower: number;
  currentHorsePower: number;
  horsepowerDiff: number;
  horsepowerTrend: "POSITIVE" | "NEGATIVE" | "NEUTRAL";

  factoryTorque: number;
  currentTorque: number;
  torqueDiff: number;
  torqueTrend: "POSITIVE" | "NEGATIVE" | "NEUTRAL";

  factoryWeight: number;
  currentWeight: number;
  weightDiff: number;
  weightTrend: "POSITIVE" | "NEGATIVE" | "NEUTRAL";
}

export interface VehicleDetail {
  id: number;
  name: string;
  engine: string;
  factoryHorsePower: number;
  currentHorsePower: number;
  horsepowerDiff: number;
  horsepowerTrend: "POSITIVE" | "NEGATIVE" | "NEUTRAL";
  factoryTorque: number;
  currentTorque: number;
  torqueDiff: number;
  torqueTrend: "POSITIVE" | "NEGATIVE" | "NEUTRAL";
  factoryWeight: number;
  currentWeight: number;
  weightDiff: number;
  weightTrend: "POSITIVE" | "NEGATIVE" | "NEUTRAL";
}

export interface Modification {
  id: number;
  name: string;
  brand: string;
  category: string;
}

export interface VehicleDetail extends VehicleCard {
  year?: number;
}