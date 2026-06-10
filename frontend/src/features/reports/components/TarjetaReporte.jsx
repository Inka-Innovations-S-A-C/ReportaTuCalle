import { Users } from 'lucide-react'
import EstadoBadge from '../../../shared/ui/EstadoBadge'

function TarjetaReporte({ reporte }) {
  const fecha = new Date(reporte.createdAt).toLocaleDateString('es-PE', {
    day: '2-digit',
    month: 'short',
  })

  return (
    <article className="min-w-[210px] max-w-[210px] bg-white border border-gray-200 rounded-2xl p-3.5 shadow-sm flex flex-col gap-2 shrink-0">
      {reporte.imageUrl && (
        <img
          src={reporte.imageUrl}
          alt={reporte.title}
          className="w-full h-24 object-cover rounded-xl"
          loading="lazy"
        />
      )}

      <div className="flex items-start justify-between gap-1.5">
        <p className="font-semibold text-sm text-gray-800 leading-snug line-clamp-2 flex-1">
          {reporte.title}
        </p>
        <EstadoBadge estado={reporte.status} />
      </div>

      <p className="text-xs text-gray-500 line-clamp-2 leading-relaxed">
        {reporte.description}
      </p>

      <div className="flex items-center justify-between mt-auto pt-1 border-t border-gray-100">
        {reporte.reportCount > 1 ? (
          <span className="flex items-center gap-1 text-xs text-verde-600 font-medium">
            <Users size={12} aria-hidden="true" />
            {reporte.reportCount} ciudadanos
          </span>
        ) : (
          <span />
        )}
        <span className="text-xs text-gray-400">{fecha}</span>
      </div>
    </article>
  )
}

export default TarjetaReporte
