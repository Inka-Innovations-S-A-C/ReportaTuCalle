import Spinner from '../../../shared/ui/Spinner'

function SelectorCategoria({ categorias, cargando, valor, onChange, error }) {
  if (cargando) {
    return (
      <div className="flex justify-center py-4">
        <Spinner className="h-6 w-6" />
      </div>
    )
  }

  return (
    <div className="flex flex-col gap-1.5">
      <span className="text-sm font-medium text-gray-700">Tipo de incidente *</span>

      <div className="grid grid-cols-2 gap-2">
        {categorias.map((cat) => {
          const seleccionado = valor === cat.id
          return (
            <button
              key={cat.id}
              type="button"
              onClick={() => onChange(cat.id)}
              className={[
                'flex items-center gap-2 p-3 rounded-xl border-2 text-left text-sm font-medium transition-all',
                seleccionado
                  ? 'border-verde-500 bg-verde-50 text-verde-800'
                  : 'border-gray-200 text-gray-600 hover:border-gray-300 hover:bg-gray-50',
              ].join(' ')}
            >
              <span
                className="w-3 h-3 rounded-full shrink-0 ring-1 ring-black/10"
                style={{ backgroundColor: cat.markerColor ?? '#6b7280' }}
                aria-hidden="true"
              />
              <span className="leading-tight line-clamp-2">{cat.name}</span>
            </button>
          )
        })}
      </div>

      {error && <p className="text-xs text-red-600">{error}</p>}
    </div>
  )
}

export default SelectorCategoria
