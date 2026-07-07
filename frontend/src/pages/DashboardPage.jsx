import { useState } from 'react'
import { Plus, RefreshCw, AlertTriangle, Route, X, ChevronDown, Maximize2, Minimize2 } from 'lucide-react'
import Navbar from '../shared/ui/Navbar'
import Spinner from '../shared/ui/Spinner'
import AlertaMensaje from '../shared/ui/AlertaMensaje'
import { MapaReportes, useGeolocalizacion } from '../features/map'
import { TarjetaReporte, ModalCrearReporte, useReportesCercanos } from '../features/reports'
import ModalEditarReporte from '../features/reports/components/ModalEditarReporte'
import { useAuth } from '../context/AuthContext'
import apiClient from '../shared/lib/apiClient'

function DashboardPage() {
  const { usuario } = useAuth()
  const { posicion, error: errorGPS, cargando: cargandoGPS } = useGeolocalizacion()
  const { reportes, cargando: cargandoReportes, error: errorReportes, recargar, notificacion } = useReportesCercanos(posicion)
  const [modalAbierto, setModalAbierto] = useState(false)
  const [reporteAEditar, setReporteAEditar] = useState(null)
  const [panelState, setPanelState] = useState('cerrado') // 'cerrado' | 'medio' | 'expandido'
  const [tabReportes, setTabReportes] = useState('cercanos') // 'cercanos' | 'mios'
  const [mensajeAlerta, setMensajeAlerta] = useState(null)

  const esSupervisorOAdmin = usuario?.role === 'SUPERVISOR' || usuario?.role === 'ADMIN'

  const handleEliminarReporte = async (reportId) => {
    if (window.confirm("¿Estás seguro de que deseas eliminar este reporte?")) {
      try {
        await apiClient.delete(`/reports/${reportId}`)
        recargar()
      } catch (err) {
        alert("Error al eliminar el reporte.")
      }
    }
  }

  async function manejarAutoAsignar(reportId) {
    try {
      await apiClient.put(`/reports/${reportId}/self-assign`)
      recargar()
    } catch (err) {
      alert('Error al auto-asignar reporte')
    }
  }

  const reportesAMostrar = tabReportes === 'mios' 
    ? reportes.filter(r => r.citizenId === usuario?.profileId) 
    : reportes

  return (
    <div className="flex flex-col h-screen overflow-hidden relative">
      <Navbar />

      {/* Aviso de GPS denegado */}
      {errorGPS && (
        <div className="flex items-center gap-2 bg-amber-50 border-b border-amber-200 px-4 py-2 text-xs text-amber-700 shrink-0">
          <AlertTriangle size={13} aria-hidden="true" />
          {errorGPS}
        </div>
      )}

      {/* Notificación de reporte resuelto */}
      {notificacion && (
        <div className="absolute top-20 left-1/2 -translate-x-1/2 z-[2000] animate-bounce">
          <div className="bg-emerald-500 text-white px-6 py-3 rounded-2xl shadow-xl flex items-center gap-3">
            <span className="text-xl">🎉</span>
            <span className="font-semibold text-sm">{notificacion}</span>
          </div>
        </div>
      )}

      {/* Mapa */}
      <div className="flex-1 relative min-h-0">
        <MapaReportes
          posicion={posicion}
          reportes={reportes} // En el mapa siempre mostramos todos los cercanos
          cargandoGPS={cargandoGPS}
          usuario={usuario}
          onAutoAsignar={manejarAutoAsignar}
        />

        <button
          onClick={recargar}
          disabled={cargandoReportes || !posicion}
          className="absolute top-4 right-4 z-[1000] glass-panel rounded-full p-3 text-gray-800 hover:text-verde-600 transition-all disabled:opacity-40 shadow-md bg-white"
          aria-label="Recargar reportes"
        >
          <RefreshCw size={18} className={cargandoReportes ? 'animate-spin' : ''} />
        </button>

        {/* FAB */}
        <button
          onClick={() => setModalAbierto(true)}
          className="absolute bottom-6 right-6 z-[1000] w-16 h-16 bg-gradient-to-r from-accent-600 to-accent-500 hover:from-accent-500 hover:to-accent-400 rounded-full shadow-glow-accent flex items-center justify-center text-white transition-all hover:scale-105 active:scale-95 border border-white/10"
          aria-label="Crear nuevo reporte"
        >
          <Plus size={32} strokeWidth={2.5} className="drop-shadow-sm" />
        </button>
      </div>

      {/* Panel inferior — lista de reportes */}
      <div className={`glass-panel shrink-0 rounded-t-3xl mx-2 mb-2 transition-all duration-500 ${panelState !== 'cerrado' ? 'pb-2' : 'pb-0'} ${panelState === 'expandido' ? 'h-[60vh] flex flex-col' : ''}`}>
        
        {/* Encabezado del Panel con Pestañas */}
        <div className="w-full px-4 pt-4 flex flex-col gap-3 shrink-0">
          <div className="flex items-center justify-between gap-3">
            <button 
              onClick={() => {
                setPanelState(prev => prev === 'cerrado' ? 'medio' : 'cerrado')
              }} 
              className="flex items-center justify-center gap-2 focus:outline-none flex-1 bg-gradient-to-r from-accent-500 to-accent-400 hover:from-accent-400 hover:to-accent-300 py-2 rounded-xl text-white shadow-lg transition-all active:scale-95"
            >
              <ChevronDown size={18} className={`transition-transform duration-500 ${panelState === 'cerrado' ? 'rotate-180' : 'rotate-0'}`} />
              <span className="text-sm font-extrabold uppercase tracking-widest">
                {panelState === 'cerrado' ? 'Ver Reportes' : 'Cerrar Panel'}
              </span>
            </button>
            
            {panelState !== 'cerrado' && (
              <button
                onClick={() => setPanelState(prev => prev === 'medio' ? 'expandido' : 'medio')}
                className="p-2.5 rounded-xl bg-gray-100 text-gray-600 hover:bg-gray-200 transition-colors shrink-0"
                title={panelState === 'medio' ? 'Expandir a pantalla completa' : 'Minimizar a fila'}
              >
                {panelState === 'medio' ? <Maximize2 size={18} /> : <Minimize2 size={18} />}
              </button>
            )}

            <div className="flex items-center gap-2 shrink-0">
              {cargandoReportes && <Spinner className="h-4 w-4" />}
              <span className="text-xs text-gray-600 bg-white shadow-sm font-bold px-3 py-1 rounded-full">{reportesAMostrar.length} encontrados</span>
            </div>
          </div>
          
          {panelState !== 'cerrado' && (
            <div className="flex bg-gray-100/80 p-1 rounded-xl">
              <button
                onClick={() => setTabReportes('cercanos')}
                className={`flex-1 py-1.5 text-xs font-semibold rounded-lg transition-all ${tabReportes === 'cercanos' ? 'bg-white shadow-sm text-gray-800' : 'text-gray-500 hover:text-gray-700'}`}
              >
                Cercanos a mí
              </button>
              <button
                onClick={() => setTabReportes('mios')}
                className={`flex-1 py-1.5 text-xs font-semibold rounded-lg transition-all ${tabReportes === 'mios' ? 'bg-white shadow-sm text-accent-600' : 'text-gray-500 hover:text-gray-700'}`}
              >
                Mis reportes
              </button>
            </div>
          )}
        </div>

        {errorReportes && panelState !== 'cerrado' && (
          <p className="text-xs text-red-500 px-4 py-3 shrink-0">{errorReportes}</p>
        )}
        
        {!cargandoReportes && reportesAMostrar.length === 0 && !errorReportes && panelState !== 'cerrado' && (
          <div className="px-4 py-6 text-center shrink-0">
            <p className="text-sm text-gray-500">
              {tabReportes === 'mios' 
                ? 'No has creado ningún reporte todavía.' 
                : 'No hay reportes en un radio de 5 km. ¡Sé el primero en reportar!'}
            </p>
          </div>
        )}

        {/* Layout de Tarjetas (Horizontal o Grid) */}
        {reportesAMostrar.length > 0 && panelState !== 'cerrado' && (
          <div className={`pointer-events-auto flex-1 w-full p-4 transition-all duration-500 ${
            panelState === 'expandido' 
              ? 'grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6 overflow-y-auto custom-scrollbar' 
              : 'flex gap-4 overflow-x-auto snap-x snap-mandatory custom-scrollbar'
          }`}>
            {reportesAMostrar.map((reporte) => (
              <div key={reporte.id} className={panelState === 'expandido' ? 'w-full h-full flex' : 'snap-start shrink-0 w-[260px]'}>
                <div className="w-full h-full">
                  <TarjetaReporte 
                    reporte={reporte} 
                    isOwner={reporte.citizenId === usuario?.profileId}
                    onEdit={setReporteAEditar}
                    onDelete={handleEliminarReporte}
                  />
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Panel Inferior Flotante (Explorar/Crear en móvil o desktop) */}
      <div className="fixed bottom-20 md:bottom-6 left-1/2 -translate-x-1/2 z-[1000] flex flex-col items-center gap-4 w-full max-w-md px-4 pointer-events-none">
        
        {mensajeAlerta && (
          <div className="pointer-events-auto w-full mb-2">
            <AlertaMensaje mensaje={mensajeAlerta} tipo="exito" onCerrar={() => setMensajeAlerta(null)} />
          </div>
        )}

        {/* Indicador de carga sutil */}
        {cargandoReportes && panelState === 'cerrado' && (
          <div className="pointer-events-auto bg-surface/80 backdrop-blur-md px-4 py-1.5 rounded-full shadow-lg border border-white/20">
            <p className="text-[10px] text-gray-500 font-medium animate-pulse tracking-wide uppercase">
              Actualizando zona...
            </p>
          </div>
        )}
      </div>

      {/* Modal de creación */}
      <ModalCrearReporte 
        abierto={modalAbierto} 
        onCerrar={() => setModalAbierto(false)} 
        posicion={posicion}
        onExito={(response) => {
          recargar()
          setPanelState('medio') // Abre el panel para que vea su reporte creado
          
          if (response) {
            const isClustered = response.citizenId !== usuario?.profileId || new Date(response.createdAt).getTime() < Date.now() - 5000;
            if (isClustered) {
              setMensajeAlerta('¡Tu reporte se ha fusionado con uno existente! Gracias por tu apoyo sumando prioridad.');
              setTimeout(() => setMensajeAlerta(null), 5000);
            }
          }
        }}
      />

      {/* Modal de edición */}
      {reporteAEditar && (
        <ModalEditarReporte 
          abierto={!!reporteAEditar} 
          onCerrar={() => setReporteAEditar(null)} 
          reporte={reporteAEditar}
          onExito={recargar}
        />
      )}
    </div>
  )
}

export default DashboardPage
