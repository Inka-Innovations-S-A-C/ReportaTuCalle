function CampoTextarea({ etiqueta, error, className = '', filas = 3, ...props }) {
  return (
    <div className={`flex flex-col gap-1 ${className}`}>
      {etiqueta && (
        <label className="text-sm font-medium text-gray-700">{etiqueta}</label>
      )}
      <textarea
        rows={filas}
        className={[
          'w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm text-gray-900',
          'placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-verde-500 focus:border-transparent',
          'disabled:opacity-50 resize-none transition',
          error ? 'border-red-500 focus:ring-red-500' : '',
        ].join(' ')}
        aria-invalid={Boolean(error)}
        aria-describedby={error ? `${props.name}-error` : undefined}
        {...props}
      />
      {error && (
        <p id={`${props.name}-error`} className="text-xs text-red-600" role="alert">
          {error}
        </p>
      )}
    </div>
  )
}

export default CampoTextarea
