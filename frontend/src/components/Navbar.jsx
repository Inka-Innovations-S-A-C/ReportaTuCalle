import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { MapPin, Menu, X, LogOut, Bell, User } from 'lucide-react'
import { useAuth } from '../context/AuthContext'

function Navbar() {
  const { usuario, cerrarSesion } = useAuth()
  const navegar = useNavigate()
  const [menuAbierto, setMenuAbierto] = useState(false)

  const manejarCerrarSesion = () => {
    cerrarSesion()
    navegar('/login')
  }

  return (
    <nav className="bg-azul-900 text-white shadow-lg">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">

          {/* Logo y nombre */}
          <Link to="/dashboard" className="flex items-center gap-2 font-bold text-lg hover:opacity-90 transition-opacity">
            <MapPin className="text-verde-400" size={24} />
            <span className="hidden sm:block">ReportaTuCalle</span>
          </Link>

          {/* Acciones escritorio */}
          <div className="hidden md:flex items-center gap-4">
            <span className="text-sm text-gray-300">
              Hola, <span className="text-verde-400 font-semibold">{usuario?.username || usuario?.nombre || 'Ciudadano'}</span>
            </span>

            <button
              className="relative p-2 rounded-full hover:bg-azul-800 transition-colors"
              aria-label="Notificaciones"
            >
              <Bell size={20} />
            </button>

            <button
              onClick={manejarCerrarSesion}
              className="flex items-center gap-2 px-3 py-2 rounded-lg text-sm
                         hover:bg-red-600 transition-colors duration-200"
            >
              <LogOut size={16} />
              Cerrar sesión
            </button>
          </div>

          {/* Botón menú móvil */}
          <button
            className="md:hidden p-2 rounded-lg hover:bg-azul-800 transition-colors"
            onClick={() => setMenuAbierto(!menuAbierto)}
            aria-label="Abrir menú"
          >
            {menuAbierto ? <X size={22} /> : <Menu size={22} />}
          </button>
        </div>
      </div>

      {/* Menú desplegable móvil */}
      {menuAbierto && (
        <div className="md:hidden bg-azul-800 border-t border-azul-700 px-4 py-3 space-y-3">
          <div className="flex items-center gap-2 text-sm text-gray-300 pb-2 border-b border-azul-700">
            <User size={16} />
            <span>{usuario?.username || usuario?.nombre || 'Ciudadano'}</span>
          </div>

          <button
            onClick={manejarCerrarSesion}
            className="w-full flex items-center gap-2 px-3 py-2 rounded-lg text-sm
                       text-red-400 hover:bg-red-600 hover:text-white transition-colors"
          >
            <LogOut size={16} />
            Cerrar sesión
          </button>
        </div>
      )}
    </nav>
  )
}

export default Navbar
