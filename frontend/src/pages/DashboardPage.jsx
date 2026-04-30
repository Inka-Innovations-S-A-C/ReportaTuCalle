import { useState } from 'react'
import {
  MapPin, PlusCircle, Clock, CheckCircle2,
  AlertTriangle, ChevronRight, Camera, Navigation,
  FileText, Wrench, Leaf, Zap, Droplets,
} from 'lucide-react'
import Navbar from '../components/Navbar'
import { useAuth } from '../context/AuthContext'

// Categorías de reportes disponibles (segunda entrega: serán funcionales)
const CATEGORIAS = [
  { id: 'via', etiqueta: 'Vía pública', icono: Wrench, color: 'bg-orange-100 text-orange-600' },
  { id: 'verde', etiqueta: 'Áreas verdes', icono: Leaf, color: 'bg-green-100 text-green-600' },
  { id: 'alumbrado', etiqueta: 'Alumbrado', icono: Zap, color: 'bg-yellow-100 text-yellow-600' },
  { id: 'desague', etiqueta: 'Desagüe', icono: Droplets, color: 'bg-blue-100 text-blue-600' },
]

// Reportes de ejemplo para mostrar la interfaz (serán reales en la segunda entrega)
const REPORTES_EJEMPLO = [
  {
    id: 1,
    titulo: 'Bache en Av. Principal',
    categoria: 'Vía pública',
    estado: 'pendiente',
    fecha: '28/04/2026',
    descripcion: 'Bache grande que daña los vehículos.',
  },
  {
    id: 2,
    titulo: 'Árbol caído en parque',
    categoria: 'Áreas verdes',
    estado: 'en_proceso',
    fecha: '27/04/2026',
    descripcion: 'Árbol caído bloquea el paso peatonal.',
  },
  {
    id: 3,
    titulo: 'Poste sin luz en Jr. Los Pinos',
    categoria: 'Alumbrado',
    estado: 'resuelto',
    fecha: '25/04/2026',
    descripcion: 'El poste lleva 3 días sin funcionar.',
  },
]

const ETIQUETA_ESTADO = {
  pendiente:   { texto: 'Pendiente',   clase: 'bg-yellow-100 text-yellow-700', icono: Clock },
  en_proceso:  { texto: 'En proceso',  clase: 'bg-blue-100 text-blue-700',     icono: AlertTriangle },
  resuelto:    { texto: 'Resuelto',    clase: 'bg-green-100 text-green-700',   icono: CheckCircle2 },
}

function TarjetaReporte({ reporte }) {
  const estado = ETIQUETA_ESTADO[reporte.estado]
  const IconoEstado = estado.icono

  return (
    <div className="tarjeta hover:shadow-md transition-shadow cursor-pointer group">
      <div className="flex items-start justify-between gap-3">
        <div className="flex-1 min-w-0">
          <h3 className="font-semibold text-gray-800 truncate group-hover:text-verde-700 transition-colors">
            {reporte.titulo}
          </h3>
          <p className="text-xs text-gray-500 mt-0.5">{reporte.categoria} · {reporte.fecha}</p>
          <p className="text-sm text-gray-600 mt-2 line-clamp-2">{reporte.descripcion}</p>
        </div>
        <ChevronRight size={18} className="text-gray-400 shrink-0 mt-1 group-hover:text-verde-600 transition-colors" />
      </div>

      <div className="mt-3 pt-3 border-t border-gray-100 flex items-center gap-1.5">
        <IconoEstado size={14} className={`${estado.clase.split(' ')[1]}`} />
        <span className={`text-xs font-medium px-2 py-0.5 rounded-full ${estado.clase}`}>
          {estado.texto}
        </span>
      </div>
    </div>
  )
}

function DashboardPage() {
  const { usuario } = useAuth()
  const [tabActiva, setTabActiva] = useState('mis_reportes')

  const nombreMostrar = usuario?.username || usuario?.nombre || 'Ciudadano'

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <main className="max-w-2xl mx-auto px-4 py-6 space-y-6">

        {/* Bienvenida */}
        <section className="bg-gradient-to-r from-azul-900 to-azul-800 rounded-2xl p-6 text-white shadow-md">
          <p className="text-verde-300 text-sm font-medium">Bienvenido de vuelta 👋</p>
          <h2 className="text-2xl font-bold mt-1">{nombreMostrar}</h2>
          <p className="text-gray-300 text-sm mt-1">
            Juntos mejoramos nuestro distrito, un reporte a la vez.
          </p>
        </section>

        {/* Botón principal: nuevo reporte */}
        <section>
          <button
            className="w-full flex items-center justify-center gap-3 py-4 px-6 rounded-2xl
                       bg-verde-600 text-white font-semibold text-base shadow-md
                       hover:bg-verde-700 active:bg-verde-800
                       focus:outline-none focus:ring-2 focus:ring-verde-500 focus:ring-offset-2
                       transition-colors duration-200"
            onClick={() => alert('Funcionalidad disponible en la segunda entrega 🚀')}
          >
            <PlusCircle size={22} />
            Crear nuevo reporte
          </button>
        </section>

        {/* Categorías de reporte (vista previa segunda entrega) */}
        <section>
          <h3 className="text-sm font-semibold text-gray-500 uppercase tracking-wide mb-3">
            Categorías de reporte
          </h3>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            {CATEGORIAS.map(({ id, etiqueta, icono: Icono, color }) => (
              <button
                key={id}
                className="tarjeta flex flex-col items-center gap-2 py-4 hover:shadow-md transition-shadow
                           hover:border-verde-300 focus:outline-none focus:ring-2 focus:ring-verde-500"
                onClick={() => alert('Funcionalidad disponible en la segunda entrega 🚀')}
              >
                <div className={`p-2.5 rounded-xl ${color}`}>
                  <Icono size={22} />
                </div>
                <span className="text-xs font-medium text-gray-700 text-center leading-tight">
                  {etiqueta}
                </span>
              </button>
            ))}
          </div>
        </section>

        {/* Acciones rápidas (segunda entrega) */}
        <section className="grid grid-cols-2 gap-3">
          <button
            className="tarjeta flex items-center gap-3 hover:shadow-md transition-shadow"
            onClick={() => alert('Cámara disponible en la segunda entrega 🚀')}
          >
            <div className="p-2.5 rounded-xl bg-purple-100 text-purple-600">
              <Camera size={20} />
            </div>
            <div className="text-left">
              <p className="text-sm font-semibold text-gray-800">Fotografía</p>
              <p className="text-xs text-gray-500">Adjuntar foto</p>
            </div>
          </button>

          <button
            className="tarjeta flex items-center gap-3 hover:shadow-md transition-shadow"
            onClick={() => alert('Geolocalización disponible en la segunda entrega 🚀')}
          >
            <div className="p-2.5 rounded-xl bg-red-100 text-red-600">
              <Navigation size={20} />
            </div>
            <div className="text-left">
              <p className="text-sm font-semibold text-gray-800">Ubicación</p>
              <p className="text-xs text-gray-500">GPS automático</p>
            </div>
          </button>
        </section>

        {/* Tabs: Mis reportes / Todos */}
        <section>
          <div className="flex border-b border-gray-200 mb-4">
            {[
              { id: 'mis_reportes', etiqueta: 'Mis reportes', icono: FileText },
              { id: 'todos', etiqueta: 'Todos', icono: MapPin },
            ].map(({ id, etiqueta, icono: Icono }) => (
              <button
                key={id}
                className={`flex items-center gap-1.5 px-4 py-2.5 text-sm font-medium border-b-2 transition-colors
                  ${tabActiva === id
                    ? 'border-verde-600 text-verde-700'
                    : 'border-transparent text-gray-500 hover:text-gray-700'
                  }`}
                onClick={() => setTabActiva(id)}
              >
                <Icono size={15} />
                {etiqueta}
              </button>
            ))}
          </div>

          {/* Lista de reportes de ejemplo */}
          <div className="space-y-3">
            {REPORTES_EJEMPLO.map((reporte) => (
              <TarjetaReporte key={reporte.id} reporte={reporte} />
            ))}
          </div>

          <p className="text-center text-xs text-gray-400 mt-4">
            Los reportes reales estarán disponibles en la segunda entrega.
          </p>
        </section>

      </main>
    </div>
  )
}

export default DashboardPage
