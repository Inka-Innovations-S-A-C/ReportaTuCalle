import { Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import RutaProtegida from './components/RutaProtegida'

function App() {
  return (
    <Routes>
      {/* Ruta raíz → redirige al login */}
      <Route path="/" element={<Navigate to="/login" replace />} />

      {/* Rutas públicas */}
      <Route path="/login" element={<LoginPage />} />
      <Route path="/registro" element={<RegisterPage />} />

      {/* Rutas protegidas: solo accesibles con sesión activa */}
      <Route
        path="/dashboard"
        element={
          <RutaProtegida>
            <DashboardPage />
          </RutaProtegida>
        }
      />

      {/* Cualquier ruta no encontrada redirige al login */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}

export default App
