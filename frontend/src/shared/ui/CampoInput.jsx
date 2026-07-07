function CampoInput({ etiqueta, error, icono: Icono, className = '', ...props }) {
  return (
    <div className={`flex flex-col gap-1 ${className}`}>
      {etiqueta && (
        <label className="text-sm font-semibold text-gray-300 mb-1">{etiqueta}</label>
      )}

      <div className="relative">
        {Icono && (
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Icono size={18} className="text-gray-400" aria-hidden="true" />
          </div>
        )}
        <input
          className={[
            'input-campo',
            Icono ? 'pl-11' : '',
            error ? 'border-danger focus:ring-danger focus:border-danger' : '',
          ].join(' ')}
          aria-invalid={Boolean(error)}
          aria-describedby={error ? `${props.name}-error` : undefined}
          {...props}
        />
      </div>

      {error && (
        <p id={`${props.name}-error`} className="text-xs text-danger mt-1 font-medium pl-1" role="alert">
          {error}
        </p>
      )}
    </div>
  )
}

export default CampoInput
