import { Loader2 } from 'lucide-react'

const ESTILOS = {
  primario: 'btn-primario',
  secundario: 'btn-secundario',
  peligro:
    'w-full flex justify-center items-center gap-2 py-3 px-5 rounded-xl ' +
    'bg-danger/20 text-danger font-semibold text-sm border border-danger/30 ' +
    'hover:bg-danger/30 active:bg-danger/40 shadow-[0_0_15px_rgba(239,68,68,0.15)] ' +
    'focus:outline-none focus:ring-2 focus:ring-danger focus:ring-offset-2 focus:ring-offset-background ' +
    'disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-300',
}

function Boton({ children, variante = 'primario', cargando = false, icono: Icono, className = '', ...props }) {
  return (
    <button
      className={`${ESTILOS[variante] ?? ESTILOS.primario} ${className}`}
      disabled={cargando || props.disabled}
      {...props}
    >
      {cargando ? (
        <Loader2 size={18} className="animate-spin" aria-hidden="true" />
      ) : (
        Icono && <Icono size={18} aria-hidden="true" />
      )}
      {children}
    </button>
  )
}

export default Boton
