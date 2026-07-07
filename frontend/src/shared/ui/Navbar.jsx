import { Link, useLocation } from 'react-router-dom'
import { MapPin, LogOut, User, Settings } from 'lucide-react'
import { useAuth } from '../../context/AuthContext'

function Navbar() {
  const { usuario, cerrarSesion } = useAuth()
  const location = useLocation()
  const esDashboard = location.pathname === '/dashboard' || location.pathname === '/'

  return (
    <nav className={`glass-panel text-white px-5 h-16 flex items-center justify-between shrink-0 z-[1500] sticky top-0 transition-all ${
      esDashboard 
        ? 'mx-2 mt-2 rounded-b-2xl' 
        : 'w-full m-0 rounded-none'
    }`}>
      {/* Logo */}
      <Link to="/dashboard" className="flex items-center gap-2.5 hover:opacity-80 transition-opacity">
        <div className="bg-gradient-to-br from-primary-500 to-accent-500 rounded-lg p-1.5 shadow-glow">
          <MapPin size={18} className="text-white" aria-hidden="true" />
        </div>
        <span className="font-bold text-base tracking-tight text-white">ReportaTuCalle</span>
      </Link>

      {/* Usuario + acciones */}
      <div className="flex items-center gap-1">
        <Link to="/leaderboard" className="hidden sm:block text-sm font-medium text-gray-300 hover:text-white transition-colors mr-4">
          Ranking
        </Link>
        <Link to="/contact" className="hidden sm:block text-sm font-medium text-gray-300 hover:text-white transition-colors mr-4">
          Contacto
        </Link>

        {(usuario?.role === 'SUPERVISOR' || usuario?.role === 'ROLE_SUPERVISOR') && (
          <Link to="/supervisor" className="text-sm font-medium text-gray-300 hover:text-white transition-colors mr-4">
            Supervisor
          </Link>
        )}

        {/* Link al perfil */}
        <Link
          to="/perfil"
          className="flex items-center gap-2 bg-surface/80 hover:bg-surface border border-primary-500/20 px-3 py-1.5 rounded-xl transition-all"
        >
          <div className="w-7 h-7 bg-gradient-to-br from-primary-500 to-accent-500 rounded-full flex items-center justify-center text-white font-bold text-xs shadow-glow">
            {usuario?.firstName?.charAt(0)}
          </div>
          <span className="hidden md:inline text-sm font-semibold text-white">
            {usuario?.firstName}
          </span>
          {usuario?.role === 'CITIZEN' && usuario?.civicScore !== undefined && (
            <div className="hidden md:flex items-center gap-1 bg-accent-500/20 border border-accent-500/30 px-2 py-0.5 rounded-full">
              <span className="text-xs font-bold text-accent-400">{usuario.civicScore} pts</span>
            </div>
          )}
        </Link>

        {/* Link al panel admin — solo ADMIN */}
        {usuario?.role === 'ADMIN' && (
          <Link
            to="/admin"
            className="flex items-center gap-2 text-sm text-gray-300 hover:text-white transition-all px-3 py-2 rounded-xl hover:bg-white/10"
            aria-label="Panel de administración"
          >
            <Settings size={16} aria-hidden="true" />
            <span className="hidden sm:inline font-medium">Admin</span>
          </Link>
        )}

        <button
          onClick={cerrarSesion}
          className="flex items-center gap-2 text-sm text-gray-300 hover:text-danger hover:bg-danger/10 transition-all px-3 py-2 rounded-xl ml-2"
          aria-label="Cerrar sesión"
        >
          <LogOut size={16} aria-hidden="true" />
          <span className="hidden sm:inline font-medium">Salir</span>
        </button>
      </div>
    </nav>
  )
}

export default Navbar
