import { useState } from 'react'
import { X, CheckCircle2, Search } from 'lucide-react'

export default function HistorialModal({ isOpen, onClose, resueltos, onRevertir }) {
  const [busqueda, setBusqueda] = useState('')

  if (!isOpen) return null

  const reportesFiltrados = resueltos.filter(r => 
    r.title.toLowerCase().includes(busqueda.toLowerCase()) || 
    r.description.toLowerCase().includes(busqueda.toLowerCase())
  )

  return (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 animate-fade-in">
      <div className="bg-white rounded-2xl w-full max-w-3xl shadow-2xl overflow-hidden flex flex-col max-h-[85vh]">
        
        {/* Header */}
        <div className="flex justify-between items-center p-5 border-b border-gray-100 bg-emerald-50">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 bg-emerald-500 rounded-lg flex items-center justify-center shadow-sm">
              <CheckCircle2 size={20} className="text-white" />
            </div>
            <div>
              <h2 className="text-xl font-bold text-emerald-900">Historial de Resueltos</h2>
              <p className="text-xs text-emerald-600 font-medium">Mostrando hasta los últimos 30 reportes solucionados</p>
            </div>
          </div>
          <button onClick={onClose} className="text-emerald-400 hover:text-emerald-700 transition-colors p-2 hover:bg-emerald-100 rounded-full">
            <X size={24} />
          </button>
        </div>

        {/* Buscador */}
        <div className="p-4 border-b border-gray-100 bg-white">
          <div className="relative">
            <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input 
              type="text" 
              placeholder="Buscar reporte por título o descripción..." 
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              className="w-full pl-10 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all"
            />
          </div>
        </div>

        {/* Lista de Resueltos Compacta */}
        <div className="p-5 overflow-y-auto flex-1 bg-gray-50">
          {reportesFiltrados.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-40 text-gray-400">
              <CheckCircle2 size={48} className="mb-3 opacity-20" />
              <p className="text-sm">{busqueda ? 'No se encontraron resultados para tu búsqueda.' : 'Aún no has resuelto ningún reporte hoy.'}</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {reportesFiltrados.map(r => (
                <div key={r.id} className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm flex flex-col hover:shadow-md transition-shadow group">
                  <div className="flex justify-between items-start gap-2 mb-2">
                    <h3 className="font-semibold text-sm text-gray-800 line-clamp-1">{r.title}</h3>
                    <span className="text-[9px] uppercase tracking-wider font-bold px-2 py-0.5 rounded-md bg-emerald-100 text-emerald-800 shrink-0">
                      RESUELTO
                    </span>
                  </div>
                  <p className="text-xs text-gray-500 line-clamp-2 mb-3 flex-1">{r.description}</p>

                  {r.imageUrl && (
                    <div className="flex gap-2 mb-3">
                      <div className="flex-1 relative">
                        <img src={r.imageUrl} alt="Problema" className="w-full h-20 object-cover rounded-lg border border-gray-100" />
                        <div className="absolute inset-x-0 bottom-0 bg-black/50 text-[9px] text-white text-center py-0.5 rounded-b-lg">Antes</div>
                      </div>
                      {r.resolutionImageUrl && (
                        <div className="flex-1 relative">
                          <img src={r.resolutionImageUrl} alt="Solución" className="w-full h-20 object-cover rounded-lg border-2 border-emerald-500" />
                          <div className="absolute inset-x-0 bottom-0 bg-emerald-500/90 text-[9px] text-white text-center py-0.5 rounded-b-lg font-bold">Después</div>
                        </div>
                      )}
                    </div>
                  )}

                  <div className="mt-auto pt-3 border-t border-gray-100">
                    <button 
                      onClick={() => onRevertir(r.id)}
                      className="w-full text-[11px] font-bold text-gray-500 hover:text-red-600 bg-gray-50 hover:bg-red-50 py-1.5 rounded-lg transition-colors border border-gray-200 hover:border-red-200 flex justify-center items-center gap-1.5"
                    >
                      <X size={12} />
                      Deshacer (Revertir)
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
