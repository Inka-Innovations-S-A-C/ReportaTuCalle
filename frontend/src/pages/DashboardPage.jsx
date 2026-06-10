import { useState } from 'react'
import { Plus, RefreshCw, AlertTriangle, Route, X, ChevronDown } from 'lucide-react'
import Navbar from '../shared/ui/Navbar'
import Spinner from '../shared/ui/Spinner'
import AlertaMensaje from '../shared/ui/AlertaMensaje'
import { MapaReportes, useGeolocalizacion } from '../features/map'
import { TarjetaReporte, ModalCrearReporte, useReportesCercanos } from '../features/reports'
import { useRutaOptimizada } from '../features/reports/hooks/useRutaOptimizada'
import { useCategorias } from '../features/categories/hooks/useCategorias'
import { useAuth } from '../context/AuthContext'

function DashboardPage() {
  const { usuario } = useAuth()
  const { posicion, error: errorGPS, cargando: cargandoGPS } = useGeolocalizacion()
  const { reportes, cargando: cargandoReportes, error: errorReportes, recargar } = useReportesCercanos(posicion)
  const { ruta, cargando: cargandoRuta, error: errorRuta, calcularRuta, limpiarRuta } = useRutaOptimizada()
  const { categorias } = useCategorias()
  const [modalAbierto, setModalAbierto] = useState(false)
  const [panelRutaAbierto, setPanelRutaAbierto] = useState(false)
  const [configRuta, setConfigRuta] = useState({ categoryId: '', radiusInMeters: 5000 })

  const esSupervisorOAdmin = usuario?.role === 'SUPERVISOR' || usuario?.role === 'ADMIN'

  async function manejarCalcularRuta(e) {
    e.preventDefault()
    if (!configRuta.categoryId || !posicion) return
    await calcularRuta({
      categoryId: configRuta.categoryId,
      startLatitude: posicion.latitude,
      startLongitude: posicion.longitude,
      radiusInMeters: Number(configRuta.radiusInMeters),
    })
  }

  function cerrarPanelRuta() {
    setPanelRutaAbierto(false)
    limpiarRuta()
  }

  return (
    <div className="flex flex-col h-screen bg-gray-100 overflow-hidden">
      <Navbar />

      {/* Aviso de GPS denegado */}
      {errorGPS && (
        <div className="flex items-center gap-2 bg-amber-50 border-b border-amber-200 px-4 py-2 text-xs text-amber-700 shrink-0">
          <AlertTriangle size={13} aria-hidden="true" />
          {errorGPS}
        </div>
      )}

      {/* Panel de ruta óptima — solo SUPERVISOR y ADMIN */}
      {esSupervisorOAdmin && panelRutaAbierto && (
        <div className="bg-white border-b border-gray-200 px-4 py-3 shrink-0 shadow-sm z-[1200]">
          <form onSubmit={manejarCalcularRuta} className="flex flex-wrap items-end gap-2">
            <div className="flex flex-col gap-1 flex-1 min-w-[140px]">
              <label className="text-xs font-medium text-gray-600">Categoría</label>
              <select
                value={configRuta.categoryId}
                onChange={(e) => setConfigRuta((p) => ({ ...p, categoryId: e.target.value }))}
                className="rounded-lg border border-gray-300 px-2.5 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500"
                required
              >
                <option value="">Selecciona...</option>
                {categorias.map((c) => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </select>
            </div>
            <div className="flex flex-col gap-1">
              <label className="text-xs font-medium text-gray-600">Radio (m)</label>
              <input
                type="number"
                min={500}
                max={20000}
                step={500}
                value={configRuta.radiusInMeters}
                onChange={(e) => setConfigRuta((p) => ({ ...p, radiusInMeters: e.target.value }))}
                className="w-24 rounded-lg border border-gray-300 px-2.5 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500"
              />
            </div>
            <button
              type="submit"
              disabled={cargandoRuta || !posicion || !configRuta.categoryId}
              className="flex items-center gap-1.5 bg-purple-600 hover:bg-purple-700 disabled:opacity-50 text-white text-xs font-semibold px-3 py-2 rounded-lg transition-colors"
            >
              {cargandoRuta ? <Spinner className="h-3.5 w-3.5" /> : <Route size={14} />}
              {cargandoRuta ? 'Calculando...' : 'Calcular'}
            </button>
            {ruta && (
              <div className="flex items-center gap-2 ml-1">
                <span className="text-xs text-purple-700 font-semibold bg-purple-50 px-2.5 py-1 rounded-full">
                  {ruta.orderedStops.length} paradas · {ruta.totalDistanceKm?.toFixed(1)} km
                </span>
                <button
                  type="button"
                  onClick={limpiarRuta}
                  className="text-gray-400 hover:text-gray-600 transition-colors"
                  aria-label="Limpiar ruta"
                >
                  <X size={14} />
                </button>
              </div>
            )}
            <button
              type="button"
              onClick={cerrarPanelRuta}
              className="ml-auto text-gray-400 hover:text-gray-600 transition-colors p-1"
              aria-label="Cerrar panel de ruta"
            >
              <X size={16} />
            </button>
          </form>
          {errorRuta && <p className="text-xs text-red-500 mt-2">{errorRuta}</p>}
        </div>
      )}

      {/* Mapa — ocupa todo el espacio disponible */}
      <div className="flex-1 relative min-h-0">
        <MapaReportes
          posicion={posicion}
          reportes={reportes}
          cargandoGPS={cargandoGPS}
          ruta={ruta}
        />

        {/* Botón recargar reportes */}
        <button
          onClick={recargar}
          disabled={cargandoReportes || !posicion}
          className="absolute top-3 right-3 z-[1000] bg-white shadow-md rounded-full p-2.5 text-gray-600 hover:text-verde-600 hover:shadow-lg transition-all disabled:opacity-40"
          aria-label="Recargar reportes"
        >
          <RefreshCw size={16} className={cargandoReportes ? 'animate-spin' : ''} />
        </button>

        {/* Botón ruta óptima — supervisor/admin, solo cuando el panel está cerrado */}
        {esSupervisorOAdmin && !panelRutaAbierto && (
          <button
            onClick={() => setPanelRutaAbierto(true)}
            className="absolute top-3 left-3 z-[1000] flex items-center gap-1.5 bg-purple-600 hover:bg-purple-700 text-white text-xs font-semibold px-3 py-2 rounded-xl shadow-md transition-colors"
            aria-label="Calcular ruta óptima"
          >
            <Route size={14} />
            <span className="hidden sm:inline">Ruta óptima</span>
            <ChevronDown size={13} />
          </button>
        )}

        {/* FAB — crear reporte */}
        <button
          onClick={() => setModalAbierto(true)}
          className="absolute bottom-5 right-4 z-[1000] w-14 h-14 bg-verde-500 hover:bg-verde-600 active:bg-verde-700 rounded-full shadow-xl flex items-center justify-center text-white transition-colors"
          aria-label="Crear nuevo reporte"
        >
          <Plus size={28} strokeWidth={2.5} />
        </button>
      </div>

      {/* Panel inferior — lista de reportes cercanos */}
      <div className="bg-white border-t border-gray-200 shrink-0">
        <div className="px-4 pt-3 pb-1 flex items-center justify-between">
          <p className="text-xs font-semibold text-gray-500 uppercase tracking-wide">
            Reportes cercanos
          </p>
          {cargandoReportes && <Spinner className="h-4 w-4" />}
          {!cargandoReportes && (
            <span className="text-xs text-gray-400">{reportes.length} encontrados</span>
          )}
        </div>

        {errorReportes && (
          <p className="text-xs text-red-500 px-4 pb-2">{errorReportes}</p>
        )}

        {!cargandoReportes && reportes.length === 0 && !errorReportes && posicion && (
          <p className="text-xs text-gray-400 px-4 pb-3">
            No hay reportes en un radio de 5 km. ¡Sé el primero en reportar!
          </p>
        )}

        {/* Scroll horizontal de tarjetas */}
        {reportes.length > 0 && (
          <div className="flex gap-3 overflow-x-auto px-4 pb-4 pt-1 snap-x snap-mandatory">
            {reportes.map((reporte) => (
              <div key={reporte.id} className="snap-start">
                <TarjetaReporte reporte={reporte} />
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Modal de creación */}
      <ModalCrearReporte
        abierto={modalAbierto}
        onCerrar={() => setModalAbierto(false)}
        posicion={posicion}
        onExito={recargar}
      />
    </div>
  )
}

export default DashboardPage
