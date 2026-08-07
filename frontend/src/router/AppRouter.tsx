import { BrowserRouter, Routes, Route } from "react-router-dom";
import GaragePage from "../pages/GaragePage";
import VehiclePage from "../pages/VehiclePage";
import LoginPage from "../pages/LoginPage";
import ProtectedRoute from "../components/ProtectedRoute";
import AppLayout from "../pages/layouts/AppLayout";
import RegisterPage from "../pages/RegisterPage";
import FeedPage from "../pages/FeedPage";
import ProfilePage from "../pages/ProfilePage";
import DashboardPage from "../pages/DashboardPage";
import AccountSettingsPage from "../pages/AccountSettingsPage";
import CatalogPage from "../pages/CatalogPage";
import ChronicIssuesPage from "../pages/ChronicIssuesPage";
import ModelChronicPage from "../pages/ModelChronicPage";
import IssueDetailPage from "../pages/IssueDetailPage";
import MenuPage from "../pages/MenuPage";


import { BrandCarouselPage } from "../pages/BrandCarouselPage";

export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        <Route element={<ProtectedRoute />}>
          <Route element={<AppLayout />}>
            <Route path="/" element={<FeedPage />} />
            <Route path="/feed" element={<FeedPage />} />
            <Route path="/garage" element={<GaragePage />} />
            <Route path="/vehicle/:id" element={<VehiclePage />} />
            <Route path="/catalog" element={<CatalogPage />} />
            <Route path="/chronic-issues" element={<ChronicIssuesPage />} />
            <Route path="/chronic-issues/models/:id" element={<ModelChronicPage />} />
            <Route path="/chronic-issues/:id" element={<IssueDetailPage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/account/settings" element={<AccountSettingsPage />} />
            <Route path="/catalog/brands" element={<BrandCarouselPage />} />
            <Route path="/catalog/:brand" element={<CatalogPage />} />
            <Route path="/menu" element={<MenuPage />} />
          </Route>
        </Route>
      </Routes>
    </BrowserRouter>
  );
}