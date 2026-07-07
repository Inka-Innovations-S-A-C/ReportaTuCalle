import { AlertCircle, CheckCircle, X } from 'lucide-react'

const CONFIG_TIPO = {
  error: {
    contenedor: 'bg-danger/10 border border-danger/30 text-danger shadow-[0_0_15px_rgba(239,68,68,0.15)]',
    Icono: AlertCircle,
  },
  exito: {
    contenedor: 'bg-accent-500/10 border border-accent-500/30 text-accent-400 shadow-[0_0_15px_rgba(16,185,129,0.15)]',
    Icono: CheckCircle,
  },
}

function AlertaMensaje({ mensaje, tipo = 'error', onCerrar, flotante = false }) {
  if (!mensaje) return null

  const { contenedor, Icono } = CONFIG_TIPO[tipo] ?? CONFIG_TIPO.error

  return (
    <div
      role="alert"
      className={`flex items-start gap-3 rounded-xl px-4 py-3 text-sm transition-all duration-300 ${contenedor} ${
        flotante ? 'fixed bottom-6 right-6 z-[9999] shadow-2xl animate-fade-in translate-y-0 min-w-[300px]' : ''
      }`}
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
