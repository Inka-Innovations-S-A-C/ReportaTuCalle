import { Routes, Route, Navigate } from 'react-router-dom'
import { LoginPage, RegisterPage } from '../features/auth'
import DashboardPage from '../pages/DashboardPage'
import PerfilPage from '../features/users/pages/PerfilPage'
import AdminPage from '../features/admin/pages/AdminPage'
import RutaProtegida from '../shared/guards/RutaProtegida'

function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />

      {/* Rutas públicas */}
      <Route path="/login"    element={<LoginPage />} />
      <Route path="/registro" element={<RegisterPage />} />

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
        path="/admin"
        element={
          <RutaProtegida rolesPermitidos={['ADMIN']}>
            <AdminPage />
          </RutaProtegida>
        }
      />

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}

export default AppRouter
