import api from "./api";

export const getUserVehicles = async () => {
  const response = await api.get("/user-vehicles/me");
  return response.data;
};

export interface VehicleImage {
  id: number;
  imageUrl: string;
  primaryImage: boolean;
}

export const getVehicleImages = async (vehicleId: number): Promise<VehicleImage[]> => {
  const response = await api.get(`/user-vehicles/${vehicleId}/images`);
  return response.data;
};

export const getFeedVehicles = async () => {
  const response = await api.get("/user-vehicles/feed");
  return response.data;
};

export const getVehicleById = async (id: number) => {
  const response = await api.get(`/user-vehicles/${id}`);
  return response.data;
};

export const createUserVehicle = async (data: {
  vehicleCatalogModelId: number;
  nickName: string;
  currentHorsePower: number;
  currentTorque: number;
  currentWeight: number;
}) => {
  const response = await api.post("/user-vehicles", data);
  return response.data;
};

export const deleteUserVehicle = async (id: number) => {
  await api.delete(`/user-vehicles/${id}`);
};

// Bug 4 corrigido: sem Content-Type manual — Axios define o boundary automaticamente
export const uploadVehicleImage = async (vehicleId: number, file: File): Promise<string> => {
  const formData = new FormData();
  formData.append("file", file);
  const response = await api.post(`/user-vehicles/${vehicleId}/images`, formData);
  return response.data;
};

export const deleteVehicleImage = async (vehicleId: number, imageId: number) => {
  await api.delete(`/user-vehicles/${vehicleId}/images/${imageId}`);
};

export const setPrimaryImage = async (vehicleId: number, imageId: number) => {
  await api.put(`/user-vehicles/${vehicleId}/images/${imageId}/primary`);
};

export const getCatalogVehicles = async () => {
  const response = await api.get("/catalog/vehicles");
  return response.data;
};

export const createCatalogVehicle = async (data: object) => {
  const response = await api.post("/catalog/vehicles", data);
  return response.data;
};

export const updateCatalogVehicle = async (id: number, data: object) => {
  const response = await api.put(`/catalog/vehicles/${id}`, data);
  return response.data;
};

export const deleteCatalogVehicle = async (id: number) => {
  await api.delete(`/catalog/vehicles/${id}`);
};