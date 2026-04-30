import { Loader2 } from 'lucide-react'

/**
 * Botón reutilizable con variantes y estado de carga.
 * @param {'primario' | 'secundario' | 'peligro'} variante
 */
function Boton({
  children,
  variante = 'primario',
  cargando = false,
  icono: Icono,
  className = '',
  ...props
}) {
  const estilos = {
    primario: 'btn-primario',
    secundario: 'btn-secundario',
    peligro: `w-full flex justify-center items-center gap-2 py-3 px-4 rounded-lg
              bg-red-600 text-white font-semibold text-sm
              hover:bg-red-700 active:bg-red-800
              focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2
              disabled:opacity-50 disabled:cursor-not-allowed
              transition-colors duration-200`,
  }

  return (
    <button
      className={`${estilos[variante]} ${className}`}
      disabled={cargando || props.disabled}
      {...props}
    >
      {cargando ? (
        <Loader2 size={18} className="animate-spin" />
      ) : (
        Icono && <Icono size={18} />
      )}
      {children}
    </button>
  )
}

export default Boton
