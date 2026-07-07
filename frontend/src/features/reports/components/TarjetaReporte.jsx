import { Users } from 'lucide-react'
import EstadoBadge from '../../../shared/ui/EstadoBadge'
import { useCategorias } from '../../categories/hooks/useCategorias'

function TarjetaReporte({ reporte, isOwner, onEdit, onDelete }) {
  const fecha = new Date(reporte.createdAt).toLocaleDateString('es-PE', {
    day: '2-digit',
    month: 'short',
  })

  const { categorias } = useCategorias()
  const categoria = categorias.find(c => c.id === reporte.categoryId)

  return (
    <article className="min-w-[240px] max-w-[240px] glass-card p-4 flex flex-col gap-3 shrink-0 relative group/card">
      {isOwner && (
        <div className="absolute top-2 right-2 flex gap-1 z-10 opacity-0 group-hover/card:opacity-100 transition-opacity">
          <button onClick={() => onEdit?.(reporte)} className="bg-blue-500/80 hover:bg-blue-600 text-white p-1.5 rounded-md backdrop-blur-md transition-colors" title="Editar">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4Z"/></svg>
          </button>
          <button onClick={() => onDelete?.(reporte.id)} className="bg-red-500/80 hover:bg-red-600 text-white p-1.5 rounded-md backdrop-blur-md transition-colors" title="Eliminar">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/><line x1="10" x2="10" y1="11" y2="17"/><line x1="14" x2="14" y1="11" y2="17"/></svg>
          </button>
        </div>
      )}

      {reporte.imageUrl && (
        <div className="relative overflow-hidden rounded-xl h-32 group">
          <img
            src={reporte.imageUrl}
            alt={reporte.title}
            className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
            loading="lazy"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-surface/80 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
        </div>
      )}

      <div className="flex items-start justify-between gap-2 mt-1">
        <p className="font-semibold text-sm text-gray-100 leading-snug line-clamp-2 flex-1">
          {reporte.title}
        </p>
        <EstadoBadge estado={reporte.status} />
      </div>

      {reporte.description && (
        <p className="text-xs text-gray-400 line-clamp-2 leading-relaxed font-light">
          {reporte.description}
        </p>
      )}

      <div className="flex flex-col gap-2 mt-auto pt-3 border-t border-white/10">
        <div className="flex items-center justify-between">
          {reporte.reportCount > 1 ? (
            <span className="flex items-center gap-1.5 text-xs text-accent-400 font-medium bg-accent-400/10 px-2 py-1 rounded-md">
              <Users size={12} aria-hidden="true" />
              {reporte.reportCount} ciudadanos
            </span>
          ) : (
            <span />
          )}
          <span className="text-[11px] text-gray-500 font-medium tracking-wide">{fecha}</span>
        </div>
        
        {categoria && (
          <span 
            className="self-start text-[10px] font-bold px-2 py-1 rounded-md text-white border"
            style={{ 
              backgroundColor: `${categoria.markerColor}20`,
              borderColor: `${categoria.markerColor}40`,
              color: categoria.markerColor 
            }}
          >
            {categoria.name}
          </span>
        )}
      </div>
    </article>
  )
}

export default TarjetaReporte
