import { AlertCircle, CheckCircle, X } from 'lucide-react'

/**
 * Alerta reutilizable para mostrar mensajes de éxito o error.
 * @param {'error' | 'exito'} tipo
 */
function AlertaMensaje({ mensaje, tipo = 'error', onCerrar }) {
  if (!mensaje) return null

  const estilos = {
    error: 'bg-red-50 border-red-200 text-red-700',
    exito: 'bg-green-50 border-green-200 text-green-700',
  }

  const Icono = tipo === 'exito' ? CheckCircle : AlertCircle

  return (
    <div className={`flex items-start gap-3 p-4 rounded-lg border ${estilos[tipo]}`} role="alert">
      <Icono size={18} className="shrink-0 mt-0.5" />
      <p className="text-sm flex-1">{mensaje}</p>
      {onCerrar && (
        <button onClick={onCerrar} className="shrink-0 hover:opacity-70 transition-opacity">
          <X size={16} />
        </button>
      )}
    </div>
  )
}

export default AlertaMensaje
