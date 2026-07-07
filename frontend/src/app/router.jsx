import { Routes, Route, Navigate } from 'react-router-dom'
import { LoginPage, RegisterPage } from '../features/auth'
import ContactUsPage from '../pages/ContactUsPage'
import DashboardPage from '../pages/DashboardPage'
import LeaderboardPage from '../features/gamification/pages/LeaderboardPage'
import PerfilPage from '../features/users/pages/PerfilPage'
import AdminPage from '../features/admin/pages/AdminPage'
import SupervisorPage from '../features/supervisor/pages/SupervisorPage'
import RutaProtegida from '../shared/guards/RutaProtegida'

function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />

      {/* Rutas públicas */}
      <Route path="/login"    element={<LoginPage />} />
      <Route path="/registro" element={<RegisterPage />} />
      <Route path="/contact"  element={<ContactUsPage />} />

      {/* Rutas protegidas */}
      <Route
        path="/dashboard"
        element={
          <RutaProtegida>
            <DashboardPage />
          </RutaProtegida>
        }
      />
      <Route
        path="/perfil"
        element={
          <RutaProtegida>
            <PerfilPage />
          </RutaProtegida>
        }
      />
      <Route
        path="/leaderboard"
        element={
          <RutaProtegida>
            <LeaderboardPage />
          </RutaProtegida>
        }
      />
      <Route
        path="/admin"
        element={
          <RutaProtegida rolesPermitidos={['ADMIN']}>
            <AdminPage />
          </RutaProtegida>
        }
      />
      <Route
        path="/supervisor"
        element={
          <RutaProtegida rolesPermitidos={['SUPERVISOR', 'ROLE_SUPERVISOR', 'ADMIN', 'ROLE_ADMIN']}>
            <SupervisorPage />
          </RutaProtegida>
        }
      />

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}

export default AppRouter
