import { Navigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

function RutaProtegida({ children, rolesPermitidos }) {
  const { estaAutenticado, cargando, usuario } = useAuth()

  if (cargando) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div
          className="h-10 w-10 rounded-full border-4 border-verde-500 border-t-transparent animate-spin"
          role="status"
          aria-label="Cargando sesión"
        />
      </div>
    )
  }

  if (!estaAutenticado) return <Navigate to="/login" replace />

  if (rolesPermitidos && !rolesPermitidos.includes(usuario?.role)) {
    return <Navigate to="/dashboard" replace />
  }

  return children
}

export default RutaProtegida
