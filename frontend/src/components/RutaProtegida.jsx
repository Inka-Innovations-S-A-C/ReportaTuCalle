import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

// Envuelve rutas que requieren sesión activa.
// Si el usuario no está autenticado, lo redirige al login.
function RutaProtegida({ children }) {
  const { estaAutenticado, cargando } = useAuth()

  // Mientras se recupera la sesión de localStorage, no renderizar nada
  if (cargando) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-verde-600" />
      </div>
    )
  }

  if (!estaAutenticado) {
    return <Navigate to="/login" replace />
  }

  return children
}

export default RutaProtegida
