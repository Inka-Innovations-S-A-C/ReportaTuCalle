import { useState } from 'react'
import { Plus, Tag } from 'lucide-react'
import Navbar from '../../../shared/ui/Navbar'
import Modal from '../../../shared/ui/Modal'
import AlertaMensaje from '../../../shared/ui/AlertaMensaje'
import Spinner from '../../../shared/ui/Spinner'
import FormularioCategoria from '../components/FormularioCategoria'
import { useCategorias } from '../../categories/hooks/useCategorias'

const ETIQUETAS_ALGORITMO = {
  ROUTING: { label: 'Routing', cls: 'bg-amber-100 text-amber-700' },
  FLOW: { label: 'Flow', cls: 'bg-azul-100 text-azul-700' },
  CONNECTIVITY: { label: 'Connectivity', cls: 'bg-purple-100 text-purple-700' },
  NONE: { label: 'Solo visualización', cls: 'bg-gray-100 text-gray-500' },
}

function AdminPage() {
  const { categorias, cargando, error } = useCategorias()
  const [listaCategorias, setListaCategorias] = useState(null)
  const [modalAbierto, setModalAbierto] = useState(false)
  const [mensajeExito, setMensajeExito] = useState('')

  const categoriasActuales = listaCategorias ?? categorias

  function manejarNuevaCategoria(nueva) {
    setListaCategorias((prev) => [...(prev ?? categorias), nueva])
    setModalAbierto(false)
    setMensajeExito(`Categoría "${nueva.name}" creada correctamente.`)
    setTimeout(() => setMensajeExito(''), 4000)
  }

  return (
    <div className="flex flex-col min-h-screen bg-gray-100">
      <Navbar />

      <main className="flex-1 max-w-2xl mx-auto w-full px-4 py-6 space-y-4">
        {/* Cabecera */}
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-lg font-bold text-gray-900">Panel de Administración</h1>
            <p className="text-xs text-gray-500 mt-0.5">Gestiona las categorías de reportes</p>
          </div>
          <button
            onClick={() => setModalAbierto(true)}
            className="flex items-center gap-1.5 bg-verde-500 hover:bg-verde-600 text-white text-sm font-semibold px-4 py-2 rounded-xl shadow-sm transition-colors"
          >
            <Plus size={16} />
            Nueva categoría
          </button>
        </div>

        {mensajeExito && <AlertaMensaje mensaje={mensajeExito} tipo="exito" />}

        {/* Lista de categorías */}
        <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
          <div className="px-5 py-3 border-b border-gray-100 flex items-center gap-2">
            <Tag size={15} className="text-gray-400" />
            <span className="text-sm font-semibold text-gray-700">Categorías</span>
            {!cargando && (
              <span className="ml-auto text-xs text-gray-400">{categoriasActuales.length} total</span>
            )}
          </div>

          {cargando && (
            <div className="flex justify-center py-10">
              <Spinner />
            </div>
          )}

          {error && !cargando && (
            <div className="p-4">
              <AlertaMensaje mensaje={error} tipo="error" />
            </div>
          )}

          {!cargando && !error && categoriasActuales.length === 0 && (
            <p className="text-sm text-gray-400 text-center py-10">
              No hay categorías. Crea la primera.
            </p>
          )}

          {!cargando && categoriasActuales.length > 0 && (
            <ul className="divide-y divide-gray-100">
              {categoriasActuales.map((cat) => {
                const alg = ETIQUETAS_ALGORITMO[cat.algorithmType] ?? ETIQUETAS_ALGORITMO.NONE
                return (
                  <li key={cat.id} className="flex items-center gap-3 px-5 py-3.5">
                    <div
                      className="w-4 h-4 rounded-full shrink-0 border border-gray-200"
                      style={{ background: cat.markerColor ?? '#6b7280' }}
                    />
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-gray-800 truncate">{cat.name}</p>
                      {cat.description && (
                        <p className="text-xs text-gray-400 truncate">{cat.description}</p>
                      )}
                    </div>
                    <span className={`text-[11px] font-semibold px-2 py-0.5 rounded-full shrink-0 ${alg.cls}`}>
                      {alg.label}
                    </span>
                  </li>
                )
              })}
            </ul>
          )}
        </div>
      </main>

      <Modal abierto={modalAbierto} onCerrar={() => setModalAbierto(false)} titulo="Nueva categoría">
        <FormularioCategoria
          onExito={manejarNuevaCategoria}
          onCancelar={() => setModalAbierto(false)}
        />
      </Modal>
    </div>
  )
}

export default AdminPage
