import { useState } from 'react'
import Navbar from '../../../shared/ui/Navbar'
import TabCategorias from '../components/TabCategorias'
import TabUsuarios from '../components/TabUsuarios'
import TabReportes from '../components/TabReportes'
import { LayoutDashboard, Users, MapPin } from 'lucide-react'

function AdminPage() {
  const [tabActiva, setTabActiva] = useState('reportes')

  const TABS = [
    { id: 'reportes', label: 'Reportes', icon: MapPin },
    { id: 'usuarios', label: 'Usuarios', icon: Users },
    { id: 'categorias', label: 'Categorías', icon: LayoutDashboard },
  ]

  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Navbar />

      <main className="flex-1 max-w-4xl mx-auto w-full px-4 py-8 space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Panel de Administración</h1>
          <p className="text-sm text-gray-500 mt-1">Supervisa reportes, asigna tareas y gestiona usuarios.</p>
        </div>

        {/* Navegación de Pestañas */}
        <div className="flex space-x-1 bg-gray-200/50 p-1 rounded-xl">
          {TABS.map(tab => (
            <button
              key={tab.id}
              onClick={() => setTabActiva(tab.id)}
              className={`flex-1 flex items-center justify-center gap-2 py-2.5 text-sm font-semibold rounded-lg transition-all ${
                tabActiva === tab.id
                  ? 'bg-gray-800 text-white shadow-sm'
                  : 'text-gray-500 hover:text-gray-700 hover:bg-gray-200'
              }`}
            >
              <tab.icon size={16} />
              {tab.label}
            </button>
          ))}
        </div>

        {/* Contenido de la Pestaña Activa */}
        <div className="pt-2">
          {tabActiva === 'reportes' && <TabReportes />}
          {tabActiva === 'usuarios' && <TabUsuarios />}
          {tabActiva === 'categorias' && <TabCategorias />}
        </div>
      </main>
    </div>
  )
}

export default AdminPage
