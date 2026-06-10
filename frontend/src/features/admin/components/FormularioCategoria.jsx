import CampoInput from '../../../shared/ui/CampoInput'
import CampoTextarea from '../../../shared/ui/CampoTextarea'
import Boton from '../../../shared/ui/Boton'
import AlertaMensaje from '../../../shared/ui/AlertaMensaje'
import { useCrearCategoria } from '../hooks/useCrearCategoria'

const ALGORITMOS = [
  { value: 'NONE', label: 'Ninguno (solo visualización)' },
  { value: 'ROUTING', label: 'Routing — TSP/VRP (ej. baches, basura)' },
  { value: 'FLOW', label: 'Flow — flujo máximo (ej. fugas de agua)' },
  { value: 'CONNECTIVITY', label: 'Connectivity — árbol mínimo (ej. semáforos)' },
]

const COLORES_RAPIDOS = [
  '#ef4444', '#f97316', '#eab308', '#22c55e',
  '#3b82f6', '#8b5cf6', '#ec4899', '#6b7280',
]

function FormularioCategoria({ onExito, onCancelar }) {
  const {
    formulario,
    errores,
    mensajeError,
    cargando,
    manejarCambio,
    setCampo,
    manejarEnvio,
    limpiarError,
  } = useCrearCategoria({ onExito })

  return (
    <form onSubmit={manejarEnvio} className="space-y-4" noValidate>
      <CampoInput
        etiqueta="Nombre *"
        name="name"
        placeholder="Ej: Baches en calzada"
        value={formulario.name}
        onChange={manejarCambio}
        error={errores.name}
        maxLength={80}
      />

      <CampoTextarea
        etiqueta="Descripción *"
        name="description"
        placeholder="Describe qué tipo de problema cubre esta categoría..."
        value={formulario.description}
        onChange={manejarCambio}
        error={errores.description}
        filas={2}
      />

      {/* Color del marcador */}
      <div className="flex flex-col gap-1.5">
        <label className="text-sm font-medium text-gray-700">Color del marcador *</label>
        <div className="flex items-center gap-2 flex-wrap">
          {COLORES_RAPIDOS.map((color) => (
            <button
              key={color}
              type="button"
              onClick={() => setCampo('markerColor', color)}
              className="w-7 h-7 rounded-full border-2 transition-transform hover:scale-110"
              style={{
                background: color,
                borderColor: formulario.markerColor === color ? '#111827' : 'transparent',
                boxShadow: formulario.markerColor === color ? '0 0 0 2px white inset' : undefined,
              }}
              aria-label={`Color ${color}`}
            />
          ))}
          <input
            type="color"
            value={formulario.markerColor}
            onChange={(e) => setCampo('markerColor', e.target.value)}
            className="w-7 h-7 rounded cursor-pointer border border-gray-200"
            title="Color personalizado"
          />
        </div>
        <div className="flex items-center gap-2 mt-0.5">
          <div className="w-4 h-4 rounded-full border border-gray-200" style={{ background: formulario.markerColor }} />
          <span className="text-xs text-gray-500 font-mono">{formulario.markerColor}</span>
        </div>
        {errores.markerColor && <p className="text-xs text-red-600">{errores.markerColor}</p>}
      </div>

      {/* Tipo de algoritmo */}
      <div className="flex flex-col gap-1">
        <label className="text-sm font-medium text-gray-700">Tipo de algoritmo</label>
        <select
          name="algorithmType"
          value={formulario.algorithmType}
          onChange={manejarCambio}
          className="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-verde-500 focus:border-transparent"
        >
          {ALGORITMOS.map((a) => (
            <option key={a.value} value={a.value}>{a.label}</option>
          ))}
        </select>
      </div>

      {mensajeError && <AlertaMensaje mensaje={mensajeError} tipo="error" onCerrar={limpiarError} />}

      <div className="flex gap-3 pt-1">
        {onCancelar && (
          <Boton type="button" variante="secundario" onClick={onCancelar} className="flex-1">
            Cancelar
          </Boton>
        )}
        <Boton type="submit" variante="primario" cargando={cargando} className="flex-1">
          {cargando ? 'Creando...' : 'Crear categoría'}
        </Boton>
      </div>
    </form>
  )
}

export default FormularioCategoria
