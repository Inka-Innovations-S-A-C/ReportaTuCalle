import { MapPin } from 'lucide-react'
import { useCategorias } from '../../categories/hooks/useCategorias'
import { useCrearReporte } from '../hooks/useCrearReporte'
import SelectorCategoria from './SelectorCategoria'
import SubidaImagen from '../../media/components/SubidaImagen'
import CampoInput from '../../../shared/ui/CampoInput'
import CampoTextarea from '../../../shared/ui/CampoTextarea'
import Boton from '../../../shared/ui/Boton'
import AlertaMensaje from '../../../shared/ui/AlertaMensaje'

function FormularioReporte({ posicion, onExito, onCancelar }) {
  const { categorias, cargando: cargandoCats } = useCategorias()
  const {
    formulario,
    errores,
    mensajeError,
    cargando,
    manejarCambio,
    setCampo,
    manejarEnvio,
    limpiarError,
  } = useCrearReporte({ posicion, onExito })

  return (
    <form onSubmit={manejarEnvio} className="space-y-5" noValidate>
      {/* Selector de categoría */}
      <SelectorCategoria
        categorias={categorias}
        cargando={cargandoCats}
        valor={formulario.categoryId}
        onChange={(id) => setCampo('categoryId', id)}
        error={errores.categoryId}
      />

      {/* Título */}
      <CampoInput
        etiqueta="Título *"
        name="title"
        type="text"
        placeholder="Ej: Bache profundo en Jr. Lima 340"
        value={formulario.title}
        onChange={manejarCambio}
        error={errores.title}
        maxLength={150}
      />

      {/* Descripción */}
      <CampoTextarea
        etiqueta="Descripción *"
        name="description"
        placeholder="Describe el problema con detalle: tamaño, peligrosidad, tiempo que lleva así..."
        value={formulario.description}
        onChange={manejarCambio}
        error={errores.description}
        filas={3}
      />

      {/* Ubicación (solo lectura, tomada del GPS) */}
      <div className="flex items-start gap-2.5 bg-gray-50 rounded-xl p-3">
        <MapPin size={16} className="text-verde-500 mt-0.5 shrink-0" aria-hidden="true" />
        <div className="flex-1 min-w-0">
          <p className="text-xs font-medium text-gray-700">Ubicación capturada</p>
          {posicion ? (
            <p className="text-xs text-gray-500 font-mono mt-0.5">
              {posicion.latitude.toFixed(5)}, {posicion.longitude.toFixed(5)}
            </p>
          ) : (
            <p className="text-xs text-amber-600 mt-0.5">Obteniendo GPS...</p>
          )}
          {errores.ubicacion && (
            <p className="text-xs text-red-600 mt-0.5">{errores.ubicacion}</p>
          )}
        </div>
      </div>

      {/* Foto */}
      <SubidaImagen onImagenSubida={(url) => setCampo('imageUrl', url)} />

      {/* Error global */}
      {mensajeError && (
        <AlertaMensaje mensaje={mensajeError} tipo="error" onCerrar={limpiarError} />
      )}

      {/* Botones */}
      <div className="flex gap-3 pt-1">
        <Boton type="button" variante="secundario" onClick={onCancelar} className="flex-1">
          Cancelar
        </Boton>
        <Boton type="submit" variante="primario" cargando={cargando} className="flex-1">
          {cargando ? 'Enviando...' : 'Enviar reporte'}
        </Boton>
      </div>
    </form>
  )
}

export default FormularioReporte
