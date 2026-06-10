import { Link } from 'react-router-dom'
import { MapPin, LogOut, User, Settings } from 'lucide-react'
import { useAuth } from '../../context/AuthContext'

function Navbar() {
  const { usuario, cerrarSesion } = useAuth()

  return (
    <nav className="bg-azul-900 text-white px-4 h-14 flex items-center justify-between shrink-0 shadow-md z-[1500]">
      {/* Logo */}
      <Link to="/dashboard" className="flex items-center gap-2 hover:opacity-80 transition-opacity">
        <div className="bg-verde-500 rounded-lg p-1">
          <MapPin size={16} className="text-white" aria-hidden="true" />
        </div>
        <span className="font-bold text-sm tracking-tight">ReportaTuCalle</span>
      </Link>

      {/* Usuario + acciones */}
      <div className="flex items-center gap-1">
        {/* Link al perfil */}
        <Link
          to="/perfil"
          className="flex items-center gap-1.5 text-xs text-gray-300 hover:text-white transition-colors px-2 py-1.5 rounded-lg hover:bg-white/10"
          aria-label="Mi perfil"
        >
          <User size={15} aria-hidden="true" />
          <span className="hidden sm:inline max-w-[120px] truncate">{usuario?.email}</span>
        </Link>

        {/* Link al panel admin — solo ADMIN */}
        {usuario?.role === 'ADMIN' && (
          <Link
            to="/admin"
            className="flex items-center gap-1.5 text-xs text-gray-300 hover:text-white transition-colors px-2 py-1.5 rounded-lg hover:bg-white/10"
            aria-label="Panel de administración"
          >
            <Settings size={15} aria-hidden="true" />
            <span className="hidden sm:inline">Admin</span>
          </Link>
        )}

        <button
          onClick={cerrarSesion}
          className="flex items-center gap-1.5 text-xs text-gray-300 hover:text-white transition-colors px-2 py-1.5 rounded-lg hover:bg-white/10"
          aria-label="Cerrar sesión"
        >
          <LogOut size={15} aria-hidden="true" />
          <span className="hidden sm:inline">Salir</span>
        </button>
      </div>
    </nav>
  )
}

export default Navbar
