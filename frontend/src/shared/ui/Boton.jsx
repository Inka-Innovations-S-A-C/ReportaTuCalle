import { Loader2 } from 'lucide-react'

const ESTILOS = {
  primario:
    'w-full flex justify-center items-center gap-2 py-3 px-4 rounded-lg ' +
    'bg-verde-600 text-white font-semibold text-sm ' +
    'hover:bg-verde-700 active:bg-verde-800 ' +
    'focus:outline-none focus:ring-2 focus:ring-verde-500 focus:ring-offset-2 ' +
    'disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200',
  secundario:
    'w-full flex justify-center items-center gap-2 py-3 px-4 rounded-lg ' +
    'bg-white text-azul-800 font-semibold text-sm border border-azul-200 ' +
    'hover:bg-azul-50 active:bg-azul-100 ' +
    'focus:outline-none focus:ring-2 focus:ring-azul-500 focus:ring-offset-2 ' +
    'disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200',
  peligro:
    'w-full flex justify-center items-center gap-2 py-3 px-4 rounded-lg ' +
    'bg-red-600 text-white font-semibold text-sm ' +
    'hover:bg-red-700 active:bg-red-800 ' +
    'focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2 ' +
    'disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200',
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
