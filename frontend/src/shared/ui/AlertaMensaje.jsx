import { AlertCircle, CheckCircle, X } from 'lucide-react'

const CONFIG_TIPO = {
  error: {
    contenedor: 'bg-red-50 border border-red-200 text-red-700',
    Icono: AlertCircle,
  },
  exito: {
    contenedor: 'bg-green-50 border border-green-200 text-green-700',
    Icono: CheckCircle,
  },
}

function AlertaMensaje({ mensaje, tipo = 'error', onCerrar }) {
  if (!mensaje) return null

  const { contenedor, Icono } = CONFIG_TIPO[tipo] ?? CONFIG_TIPO.error

  return (
    <div
      role="alert"
      className={`flex items-start gap-3 rounded-lg px-4 py-3 text-sm ${contenedor}`}
    >
      <Icono size={18} className="mt-0.5 shrink-0" aria-hidden="true" />
      <p className="flex-1 leading-snug">{mensaje}</p>
      {onCerrar && (
        <button
          type="button"
          onClick={onCerrar}
          className="ml-auto shrink-0 hover:opacity-60 transition-opacity"
          aria-label="Cerrar alerta"
        >
          <X size={16} />
        </button>
      )}
    </div>
  )
}

export default AlertaMensaje
