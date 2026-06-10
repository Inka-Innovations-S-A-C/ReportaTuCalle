/**
 * Campo de input reutilizable con etiqueta y mensaje de error.
 */
function CampoInput({
  etiqueta,
  error,
  icono: Icono,
  className = '',
  ...props
}) {
  return (
    <div className={`flex flex-col gap-1 ${className}`}>
      {etiqueta && (
        <label className="text-sm font-medium text-gray-700">
          {etiqueta}
        </label>
      )}

      <div className="relative">
        {Icono && (
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Icono size={18} className="text-gray-400" />
          </div>
        )}
        <input
          className={`input-campo ${Icono ? 'pl-10' : ''} ${error ? 'border-red-500 focus:ring-red-500' : ''}`}
          {...props}
        />
      </div>

      {error && (
        <p className="text-xs text-red-600 mt-0.5">{error}</p>
      )}
    </div>
  )
}

export default CampoInput
