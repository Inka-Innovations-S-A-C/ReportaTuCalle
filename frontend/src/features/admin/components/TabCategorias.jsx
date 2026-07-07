import { useState } from 'react'
import { Plus, Tag, Trash2, Edit2 } from 'lucide-react'
import Modal from '../../../shared/ui/Modal'
import AlertaMensaje from '../../../shared/ui/AlertaMensaje'
import Spinner from '../../../shared/ui/Spinner'
import FormularioCategoria from './FormularioCategoria'
import { useCategorias } from '../../categories/hooks/useCategorias'
import { eliminarCategoria } from '../../categories/services/categoryService'

const ETIQUETAS_ALGORITMO = {
  ROUTING: { label: 'Rutas', cls: 'bg-amber-100 text-amber-700' },
  FLOW: { label: 'Flujo máximo', cls: 'bg-blue-100 text-blue-700' },
  CONNECTIVITY: { label: 'Conectividad', cls: 'bg-purple-100 text-purple-700' },
  NONE: { label: 'Visualización', cls: 'bg-gray-100 text-gray-500' },
}

function TabCategorias() {
  const { categorias, cargando, error } = useCategorias()
  const [listaCategorias, setListaCategorias] = useState(null)
  const [modalAbierto, setModalAbierto] = useState(false)
  const [categoriaAEditar, setCategoriaAEditar] = useState(null)
  const [mensajeExito, setMensajeExito] = useState('')

  const categoriasActuales = listaCategorias ?? categorias

  function manejarExito(nueva, esEdicion) {
    if (esEdicion) {
      setListaCategorias(prev => (prev ?? categorias).map(c => c.id === nueva.id ? nueva : c))
      setMensajeExito(`Categoría "${nueva.name}" actualizada.`)
    } else {
      setListaCategorias(prev => [...(prev ?? categorias), nueva])
      setMensajeExito(`Categoría "${nueva.name}" creada.`)
    }
    setModalAbierto(false)
    setTimeout(() => setMensajeExito(''), 4000)
  }

  function abrirCrear() {
    setCategoriaAEditar(null)
    setModalAbierto(true)
  }

  function abrirEditar(cat) {
    setCategoriaAEditar(cat)
    setModalAbierto(true)
  }

  async function handleDelete(id, name) {
    if (!window.confirm(`¿Eliminar la categoría ${name}?`)) return
    try {
      await eliminarCategoria(id)
      setListaCategorias(categoriasActuales.filter((c) => c.id !== id))
      setMensajeExito(`Categoría "${name}" eliminada.`)
      setTimeout(() => setMensajeExito(''), 4000)
    } catch (err) {
      alert("Error al eliminar la categoría.")
    }
  }

  return (
    <div className="space-y-4 animate-fade-in">
      <div className="flex justify-end">
        <button
          onClick={abrirCrear}
            className="flex items-center gap-2 bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-medium px-4 py-2 rounded-xl shadow transition-colors"

        >
          <Plus size={16} /> Nueva categoría
        </button>
      </div>

      {mensajeExito && <AlertaMensaje mensaje={mensajeExito} tipo="exito" />}

      <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
        {cargando && <div className="py-10 text-center"><Spinner /></div>}
        {error && !cargando && <div className="p-4"><AlertaMensaje mensaje={error} tipo="error" /></div>}
        
        {!cargando && !error && (
          <ul className="divide-y divide-gray-100">
            {categoriasActuales.map((cat) => {
              const alg = ETIQUETAS_ALGORITMO[cat.algorithmType] ?? ETIQUETAS_ALGORITMO.NONE
              return (
                <li key={cat.id} className="flex items-center gap-3 px-5 py-3.5 hover:bg-gray-50/50">
                  <div
                    className="w-4 h-4 rounded-full shrink-0 border border-gray-200"
                    style={{ background: cat.markerColor ?? '#6b7280' }}
                  />
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-800 truncate">{cat.name}</p>
                    {cat.description && <p className="text-xs text-gray-400 truncate">{cat.description}</p>}
                  </div>
                  <span className={`text-[11px] font-semibold px-2 py-0.5 rounded-full shrink-0 ${alg.cls}`}>
                    {alg.label}
                  </span>
                  <button 
                    onClick={() => abrirEditar(cat)}
                    className="ml-2 p-1.5 text-gray-400 hover:text-blue-500 hover:bg-blue-50 rounded-lg transition-colors"
                  >
                    <Edit2 size={16} />
                  </button>
                  <button 
                    onClick={() => handleDelete(cat.id, cat.name)}
                    className="ml-1 p-1.5 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                  >
                    <Trash2 size={16} />
                  </button>
                </li>
              )
            })}
          </ul>
        )}
      </div>

      <Modal abierto={modalAbierto} onCerrar={() => setModalAbierto(false)} titulo={categoriaAEditar ? "Editar categoría" : "Nueva categoría"}>
        <FormularioCategoria 
          initialData={categoriaAEditar}
          onExito={manejarExito} 
          onCancelar={() => setModalAbierto(false)} 
        />
      </Modal>
    </div>
  )
}

export default TabCategorias
