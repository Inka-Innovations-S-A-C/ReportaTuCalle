function Spinner({ className = '' }) {
  return (
    <div
      role="status"
      aria-label="Cargando"
      className={`h-8 w-8 rounded-full border-4 border-verde-500 border-t-transparent animate-spin ${className}`}
    />
  )
}

export default Spinner
