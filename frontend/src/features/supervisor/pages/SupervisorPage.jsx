import { useState, useEffect, useRef } from 'react'
import { getAssignedReports, getAllReports, selfAssignReport, optimizeRoute, updateReportStatus, bulkAssignReports } from '../services/supervisorService'
import { MapContainer, TileLayer, Marker, Popup, Circle } from 'react-leaflet'
import MarkerClusterGroup from 'react-leaflet-cluster'
import L from 'leaflet'
import { Route, Play, StopCircle, ArrowLeft, LogOut, CheckCircle, FileText, CheckCircle2, ChevronDown, Menu, X } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import Spinner from '../../../shared/ui/Spinner'
import AlertaMensaje from '../../../shared/ui/AlertaMensaje'
import ResolutionModal from '../components/ResolutionModal'
import HistorialModal from '../components/HistorialModal'
import { crearIconoReporte, crearIconoCluster } from '../../map/components/MapaReportes'
import { useCategorias } from '../../categories/hooks/useCategorias'
import { useAuth } from '../../../context/AuthContext'
import { useLiveTracking } from '../../map/hooks/useLiveTracking'
import { useMap } from 'react-leaflet'
import AnimatedPolyline from '../components/AnimatedPolyline'

// Fix for leaflet default icons
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
})

const DEFAULT_CENTER = [-12.0464, -77.0428]

const ICONO_SUPERVISOR = L.divIcon({
  className: '',
  html: `<div style="
    width: 40px; height: 40px;
    background: #ef4444;
    border: 3px solid white;
    border-radius: 50% 50% 50% 0;
    transform: rotate(-45deg);
    box-shadow: -2px 2px 8px rgba(0,0,0,0.4);
    display: flex; align-items: center; justify-content: center;
  ">
    <div style="transform: rotate(45deg); font-size: 18px;">🚚</div>
  </div>`,
  iconSize: [40, 40],
  iconAnchor: [20, 40],
  popupAnchor: [0, -40],
})

function ControlVistaSupervisor({ posicion }) {
  const map = useMap()
  useEffect(() => {
    if (posicion && posicion.latitude && posicion.longitude) {
      map.setView([posicion.latitude, posicion.longitude], 15)
    }
  }, [posicion, map])
  return null
}

function SupervisorPage() {
  const navigate = useNavigate()
  const { usuario, cerrarSesion } = useAuth()
  const [reportesAsignados, setReportesAsignados] = useState([])
  const [reportesPendientes, setReportesPendientes] = useState([])
  const [reportesResueltos, setReportesResueltos] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)
  const [errorRuta, setErrorRuta] = useState(null)
  
  const [tabActual, setTabActual] = useState('asignados') 
  const [panelRutaAbierto, setPanelRutaAbierto] = useState(true)
  const [panelControlAbierto, setPanelControlAbierto] = useState(true)

  const { categorias, cargando: cargandoCats } = useCategorias()
  
  const [rutas, setRutas] = useState({})
  const [configRuta, setConfigRuta] = useState({ categoryId: '' })
  const [calculandoRuta, setCalculandoRuta] = useState(false)
  const [ubicacionSupervisor, setUbicacionSupervisor] = useState(null)

  const [resolvingReportId, setResolvingReportId] = useState(null)
  const [historialAbierto, setHistorialAbierto] = useState(false)
  const [mapRef, setMapRef] = useState(null)

  // SIMULACIÓN PARA PRESENTACIÓN
  const [simulando, setSimulando] = useState(false)
  const animacionRef = useRef(null)

  const { enviarUbicacion } = useLiveTracking()

  useEffect(() => {
    cargarReportes()
    
    // Obtener ubicación en tiempo real (simulado/real con watchPosition)
    if (navigator.geolocation) {
      const watchId = navigator.geolocation.watchPosition(
        (pos) => {
          const nuevaPos = { latitude: pos.coords.latitude, longitude: pos.coords.longitude }
          setUbicacionSupervisor(nuevaPos)
          
          // Emitir al WebSocket
          if (usuario?.id) {
            enviarUbicacion(usuario.id, nuevaPos.latitude, nuevaPos.longitude)
          }
        },
        (err) => console.warn('No se pudo obtener la ubicación del supervisor', err),
        { enableHighAccuracy: true, maximumAge: 10000, timeout: 5000 }
      )
      return () => navigator.geolocation.clearWatch(watchId)
    }
  }, [usuario])

  useEffect(() => {
    const token = localStorage.getItem('rtk_token')
    if (!token) return

    const url = `${import.meta.env.VITE_API_URL}/admin/stream?token=${token}`
    const eventSource = new EventSource(url)

    let debounceTimer;
    const triggerReload = () => {
      clearTimeout(debounceTimer)
      debounceTimer = setTimeout(() => {
        cargarReportes()
      }, 300)
    }

    eventSource.addEventListener('REPORT_CREATED', triggerReload)
    eventSource.addEventListener('REPORT_UPDATED', triggerReload)
    eventSource.addEventListener('REPORT_ASSIGNED', triggerReload)

    return () => {
      clearTimeout(debounceTimer)
      eventSource.close()
    }
  }, [])

  useEffect(() => {
    if (mapRef) {
      const timer = setTimeout(() => {
        mapRef.invalidateSize()
      }, 350)
      return () => clearTimeout(timer)
    }
  }, [panelControlAbierto, mapRef])

  async function cargarReportes() {
    try {
      setCargando(true)
      const [asignados, todos] = await Promise.all([
        getAssignedReports(),
        getAllReports()
      ])
      const asignadosArr = Array.isArray(asignados) ? asignados : []
      const todosArr = Array.isArray(todos) ? todos : []
      
      const asignadosLimpios = asignadosArr.filter(r => r.status !== 'RESOLVED' && r.status !== 'PENDING')
      setReportesAsignados(asignadosLimpios)
      setReportesResueltos(asignadosArr.filter(r => r.status === 'RESOLVED'))
      setReportesPendientes(todosArr.filter(r => r.status === 'PENDING'))
    } catch (err) {
      console.error('Error al cargar reportes en SupervisorPage:', err)
      setError('Error al cargar los reportes.')
    } finally {
      setCargando(false)
    }
  }

  const reportesVisibles = tabActual === 'pendientes' ? reportesPendientes : reportesAsignados

  const reportesFiltrados = configRuta.categoryId 
    ? reportesVisibles.filter(r => r.categoryId.toString() === configRuta.categoryId)
    : reportesVisibles;

  const reportesPorCategoria = reportesFiltrados.reduce((acc, rep) => {
    if (!acc[rep.categoryId]) acc[rep.categoryId] = []
    acc[rep.categoryId].push(rep)
    return acc
  }, {})

  async function handleOptimizar(e) {
    if (e) e.preventDefault()
    const catId = configRuta.categoryId
    if (!catId) return

    const todosLosReportes = [...reportesPendientes, ...reportesAsignados]
    const primerReporte = todosLosReportes.find(r => r.categoryId.toString() === catId)
    
    // Usar la ubicación real del supervisor si está disponible, sino usar el primer reporte
    const startLat = ubicacionSupervisor ? ubicacionSupervisor.latitude : (primerReporte ? primerReporte.latitude : null)
    const startLng = ubicacionSupervisor ? ubicacionSupervisor.longitude : (primerReporte ? primerReporte.longitude : null)

    if (!startLat || !startLng) {
      setErrorRuta('No hay ubicación GPS disponible ni reportes de esta categoría para empezar.')
      setTimeout(() => setErrorRuta(null), 4000)
      return
    }

    try {
      setCalculandoRuta(catId)
      const reportesCategoria = reportesAsignados.filter(r => r.categoryId.toString() === catId)
      const reportIds = reportesCategoria.map(r => r.id)
      
      if (reportIds.length === 0) {
        setErrorRuta('No hay reportes de esta categoría asignados a ti para optimizar.')
        setTimeout(() => setErrorRuta(null), 4000)
        setCalculandoRuta(false)
        return
      }

      const rutaOptima = await optimizeRoute(catId, startLat, startLng, reportIds)
      
      if (rutaOptima && rutaOptima.orderedStops) {
        const waypoints = rutaOptima.orderedStops.map(stop => [stop.latitude, stop.longitude])
        const reportIds = rutaOptima.orderedStops.map(stop => stop.clusterId).filter(Boolean)
        const categoryObj = categorias.find(c => c.id.toString() === catId)
        const categoryColor = categoryObj?.markerColor || '#2563eb'
        const algorithmType = categoryObj?.algorithmType || 'ROUTING'
        
        // --- LLAMADA A OSRM SOLO PARA TSP (ROUTING) ---
        let finalPath = waypoints;
        
        if (algorithmType === 'ROUTING') {
          try {
            const coordsStr = waypoints.map(p => `${p[1]},${p[0]}`).join(';')
            const osrmRes = await fetch(`https://router.project-osrm.org/route/v1/driving/${coordsStr}?overview=full&geometries=geojson`)
            const osrmData = await osrmRes.json()
            if (osrmData.code === 'Ok' && osrmData.routes.length > 0) {
              finalPath = osrmData.routes[0].geometry.coordinates.map(coord => [coord[1], coord[0]])
            }
          } catch (e) {
            console.warn("OSRM no disponible, cayendo a líneas rectas", e)
          }
        }

        // Reemplazamos el estado completo para evitar que las rutas de diferentes categorías se solapen
        setRutas({ 
          [catId]: { path: finalPath, waypoints, reportIds, metadata: rutaOptima.metadata || {}, color: categoryColor, algorithmType } 
        })
      } else {
        setErrorRuta('No hay suficientes puntos para calcular una ruta óptima.')
        setTimeout(() => setErrorRuta(null), 4000)
      }
    } catch (err) {
      setErrorRuta('Error al calcular la ruta óptima.')
      setTimeout(() => setErrorRuta(null), 4000)
    } finally {
      setCalculandoRuta(false)
    }
  }

  function limpiarRuta() {
    setRutas({})
    setConfigRuta({ categoryId: '' })
    setPanelRutaAbierto(false)
  }

  async function handleCambiarEstado(reportId, nuevoEstado) {
    if (nuevoEstado === 'RESOLVED') {
      setResolvingReportId(reportId)
      return
    }

    try {
      await updateReportStatus(reportId, nuevoEstado)
      cargarReportes() // Refresca las listas
    } catch (err) {
      alert('Error al actualizar el estado')
    }
  }

  async function handleResolver(reportId, imageUrl) {
    try {
      await updateReportStatus(reportId, 'RESOLVED', imageUrl)
      cargarReportes()
    } catch (err) {
      alert('Error al resolver el reporte')
      throw err // Para que el modal sepa que falló
    }
  }

  async function handleAsignar(reportId) {
    try {
      await selfAssignReport(reportId)
      cargarReportes() // Mueve el reporte a 'asignados'
    } catch (err) {
      alert('Error al asignarse el reporte')
    }
  }

  async function handleAsignarRuta() {
    if (!configRuta.categoryId || !rutas[configRuta.categoryId]) return
    const ids = rutas[configRuta.categoryId].reportIds
    if (!ids || ids.length === 0) return

    try {
      await bulkAssignReports(ids)
      cargarReportes()
      limpiarRuta()
      setTabActual('asignados')
    } catch (err) {
      alert('Error al asignar la ruta')
    }
  }

  function enfocarEnMapa(lat, lng) {
    if (mapRef) {
      mapRef.flyTo([lat, lng], 18, { animate: true, duration: 1.5 })
    }
  }

  if (cargando && reportesAsignados.length === 0) return <div className="p-10 text-center"><Spinner /></div>
  if (error) return <div className="p-10"><AlertaMensaje tipo="error" mensaje={error} /></div>

  // SIMULACIÓN PARA PRESENTACIÓN: Mover el camión por la ruta calculada

  function simularRecorrido() {
    const rutaId = Object.keys(rutas)[0]
    if (!rutaId) return alert('Primero calcula una ruta óptima.')
    
    const camino = rutas[rutaId].path
    if (camino.length < 2) return

    setSimulando(true)
    let indexTramo = 0
    let subPaso = 0
    const TOTAL_PASOS_INTERMEDIOS = 20 // Cuántos pasitos dar entre punto y punto para que se vea suave

    animacionRef.current = setInterval(() => {
      if (indexTramo >= camino.length - 1) {
        clearInterval(animacionRef.current)
        setSimulando(false)
        return
      }

      const puntoA = camino[indexTramo]
      const puntoB = camino[indexTramo + 1]

      // Interpolación lineal simple para movimiento suave
      const latActual = puntoA[0] + ((puntoB[0] - puntoA[0]) * (subPaso / TOTAL_PASOS_INTERMEDIOS))
      const lngActual = puntoA[1] + ((puntoB[1] - puntoA[1]) * (subPaso / TOTAL_PASOS_INTERMEDIOS))

      const nuevaPos = { latitude: latActual, longitude: lngActual }
      setUbicacionSupervisor(nuevaPos)
      if (usuario?.id) {
        enviarUbicacion(usuario.id, latActual, lngActual)
      }

      subPaso++
      if (subPaso >= TOTAL_PASOS_INTERMEDIOS) {
        subPaso = 0
        indexTramo++
      }
    }, 150) // Actualizar cada 150ms
  }

  function detenerSimulacion() {
    if (animacionRef.current) clearInterval(animacionRef.current)
    setSimulando(false)
  }

  // --- Fin simulación ---

  return (
    <div className="flex flex-col h-screen overflow-hidden bg-gray-50">
      
      {/* Header del Supervisor (Sin basura visual) */}
      <div className="bg-white border-b border-gray-200 px-6 py-3 flex items-center justify-between shadow-sm z-20">
        <div className="flex items-center gap-3">
          <button
            onClick={() => setPanelControlAbierto(!panelControlAbierto)}
            className="p-1.5 bg-gray-100 text-gray-600 hover:bg-gray-200 hover:text-gray-900 rounded-lg transition-colors mr-1 shadow-sm"
            aria-label="Alternar panel"
          >
            <Menu size={20} />
          </button>
          <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center shadow-md">
            <Route size={18} className="text-white" />
          </div>
          <h1 className="text-xl font-bold text-gray-800">
            Panel Supervisor
          </h1>
        </div>
        
        <div className="flex items-center gap-3">
          <button 
            onClick={() => setHistorialAbierto(true)}
            className="flex items-center gap-2 text-sm font-semibold text-emerald-700 hover:text-emerald-800 bg-emerald-50 hover:bg-emerald-100 px-3 py-1.5 rounded-lg border border-emerald-200 transition-colors shadow-sm"
          >
            <CheckCircle2 size={16} />
            Historial Resueltos
          </button>

          <div className="w-px h-6 bg-gray-300 mx-1"></div>

          <button 
            onClick={() => navigate('/dashboard')}
            className="flex items-center gap-2 text-sm font-semibold text-gray-600 hover:text-blue-600 bg-gray-50 hover:bg-blue-50 px-3 py-1.5 rounded-lg border border-gray-200 transition-colors"
          >
            <ArrowLeft size={16} />
            Volver al Dashboard
          </button>
          
          <div className="w-px h-6 bg-gray-300 mx-1"></div>
          
          <button 
            onClick={cerrarSesion}
            className="flex items-center gap-2 text-sm font-semibold text-red-600 hover:text-red-700 bg-red-50 hover:bg-red-100 px-3 py-1.5 rounded-lg border border-red-100 transition-colors"
          >
            <LogOut size={16} />
            Salir
          </button>
        </div>
      </div>

      <div className="flex flex-1 overflow-hidden relative">
        {/* Sidebar */}
        <div className={`transition-all duration-300 ease-in-out bg-white border-r border-gray-200 flex flex-col h-full shadow-lg z-10 relative overflow-hidden ${panelControlAbierto ? 'w-[26%] min-w-[290px]' : 'w-0 opacity-0 border-none'}`}>
          <div className="p-3 px-4 border-b border-gray-100 shrink-0 min-w-[290px]">
            <div className="flex items-center gap-4 mb-2">
              <h1 className="text-xl font-bold text-gray-800">Panel de Control</h1>
            </div>
          <p className="text-xs text-gray-500">Gestión centralizada de reportes</p>
          
          <div className="flex bg-gray-100 rounded-lg p-1 mt-4">
            <button
              onClick={() => setTabActual('pendientes')}
              className={`flex-1 text-xs font-bold py-1.5 rounded-md transition-all ${tabActual === 'pendientes' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-500 hover:text-gray-700'}`}
            >
              Pendientes ({reportesPendientes.length})
            </button>
            <button
              onClick={() => setTabActual('asignados')}
              className={`flex-1 text-xs font-bold py-1.5 rounded-md transition-all ${tabActual === 'asignados' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-500 hover:text-gray-700'}`}
            >
              Mis Asignados ({reportesAsignados.length})
            </button>
          </div>
        </div>
        
        <div className="overflow-y-auto flex-1 p-3 px-4 space-y-4 bg-gray-50/50 min-w-[290px]">
          {Object.entries(reportesPorCategoria).length === 0 ? (
            <div className="text-center mt-10">
              <p className="text-gray-400 text-sm">No hay reportes en esta sección.</p>
            </div>
          ) : (
            Object.entries(reportesPorCategoria).map(([catId, reps]) => (
              <div key={catId} className="bg-white rounded-xl p-4 border border-gray-100 shadow-sm">
                <div className="flex justify-between items-center mb-4">
                  <h3 className="font-bold text-gray-800">Categoría #{catId} ({reps.length})</h3>
                </div>
                
                <ul className="space-y-4">
                  {reps.map(r => (
                    <li 
                      key={r.id} 
                      onClick={() => enfocarEnMapa(r.latitude, r.longitude)}
                      className="bg-gray-50 p-4 rounded-xl border border-gray-100 shadow-sm hover:shadow-md hover:border-blue-200 transition-all cursor-pointer group"
                    >
                      <div className="flex justify-between items-start gap-2">
                        <div className="font-semibold text-sm text-gray-800 group-hover:text-blue-700 transition-colors">{r.title}</div>
                        <span className={`text-[9px] uppercase tracking-wider font-bold px-2 py-0.5 rounded flex-shrink-0 ${
                          r.status === 'RESOLVED' ? 'bg-emerald-100 text-emerald-800' :
                          r.status === 'ASSIGNED' ? 'bg-blue-100 text-blue-800' :
                          r.status === 'IN_PROGRESS' ? 'bg-yellow-100 text-yellow-800' :
                          'bg-gray-200 text-gray-700'
                        }`}>
                          {r.status === 'PENDING' ? 'PENDIENTE' : 
                           r.status === 'ASSIGNED' ? 'ASIGNADO' : 
                           r.status === 'IN_PROGRESS' ? 'EN PROGRESO' : 
                           r.status === 'RESOLVED' ? 'RESUELTO' : r.status}
                        </span>
                      </div>
                      <div className="text-xs text-gray-500 mt-1.5 leading-relaxed line-clamp-2">{r.description}</div>
                      
                      {r.reportCount > 1 && (
                        <div className="mt-2 text-[11px] font-bold text-emerald-600 bg-emerald-50 inline-block px-2 py-1 rounded-md">
                          🤝 {r.reportCount} ciudadanos respaldan esto
                        </div>
                      )}

                      {r.imageUrl && (
                        <div className="flex gap-2 mb-3 mt-3">
                          <div className="flex-1 relative group">
                            <img src={r.imageUrl} alt="Original" className="w-full h-32 object-cover rounded-lg border border-gray-100" loading="lazy" />
                            <div className="absolute inset-x-0 bottom-0 bg-black/50 text-[10px] text-white text-center py-1 rounded-b-lg font-medium opacity-0 group-hover:opacity-100 transition-opacity">Original</div>
                          </div>
                          {r.status === 'RESOLVED' && r.resolutionImageUrl && (
                            <div className="flex-1 relative group">
                              <img src={r.resolutionImageUrl} alt="Resolución" className="w-full h-32 object-cover rounded-lg border-2 border-emerald-500" loading="lazy" />
                              <div className="absolute inset-x-0 bottom-0 bg-emerald-500/90 text-[10px] text-white text-center py-1 rounded-b-lg font-bold">Resuelto</div>
                            </div>
                          )}
                        </div>
                      )}
                      
                      <div className="flex justify-center mt-4 pt-4 border-t border-gray-100 w-full">
                        <div className="flex gap-2 w-full">
                          {tabActual === 'pendientes' && (
                            <button 
                              onClick={(e) => { e.stopPropagation(); handleAsignar(r.id) }}
                              className="text-[11px] font-bold bg-gray-900 hover:bg-black text-white px-3 py-1.5 rounded-md transition-colors w-full"
                            >
                              Asignarme
                            </button>
                          )}

                          {tabActual === 'asignados' && r.status !== 'RESOLVED' && (
                            <div className="flex gap-2 w-full">
                              <button 
                                onClick={(e) => { e.stopPropagation(); handleCambiarEstado(r.id, 'RESOLVED') }}
                                className="flex-1 text-[11px] font-bold bg-emerald-500 hover:bg-emerald-600 text-white px-2 py-1.5 rounded-md transition-colors shadow-sm text-center"
                              >
                                Marcar Resuelto
                              </button>
                              <button 
                                onClick={(e) => { e.stopPropagation(); handleCambiarEstado(r.id, 'PENDING') }}
                                className="flex-1 text-[11px] font-bold text-gray-600 hover:text-red-600 bg-gray-100 hover:bg-red-50 px-2 py-1.5 rounded-md transition-colors border border-gray-200 hover:border-red-200 text-center"
                              >
                                Soltar Reporte
                              </button>
                            </div>
                          )}
                        </div>
                      </div>
                    </li>
                  ))}
                </ul>
              </div>
            ))
          )}
        </div>
      </div>

      {/* Área del mapa */}
      <div className="flex-1 h-full relative z-0">
        
        {/* Panel flotante de ruta óptima */}
        {panelRutaAbierto ? (
          <div className="absolute top-4 right-4 z-[1000] bg-white/95 backdrop-blur shadow-xl rounded-2xl p-4 pt-5 animate-fade-in border border-gray-200">
            <button
              onClick={() => setPanelRutaAbierto(false)}
              className="absolute -top-3 -right-3 bg-red-500 hover:bg-red-600 text-white shadow-md border-2 border-white transition-colors p-1.5 rounded-full"
              aria-label="Cerrar panel"
            >
              <X size={16} strokeWidth={3} />
            </button>
            <form onSubmit={handleOptimizar} className="flex flex-col gap-3 w-full max-w-fit">
              <div className="flex flex-col gap-1.5 w-full">
                <label className="text-xs font-semibold text-gray-500 uppercase tracking-wider">Categoría a Optimizar</label>
                <div className="flex gap-2 w-full">
                  <select
                    value={configRuta.categoryId}
                    onChange={(e) => setConfigRuta({ ...configRuta, categoryId: e.target.value })}
                    className="flex-1 py-2 px-3 border border-gray-300 rounded-lg text-sm bg-white text-gray-900 focus:ring-2 focus:ring-blue-500 focus:outline-none focus:border-blue-500 shadow-sm"
                    required
                  >
                    <option value="">Selecciona...</option>
                    {categorias.map((c) => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                  
                  <button
                    type="submit"
                    disabled={calculandoRuta || !configRuta.categoryId}
                    className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 disabled:opacity-50 text-white text-sm font-semibold px-4 py-2 rounded-lg transition-all shadow-sm flex-shrink-0"
                  >
                    {calculandoRuta ? <Spinner className="h-3.5 w-3.5 border-white" /> : <Route size={14} />}
                    {calculandoRuta ? 'Calculando...' : 'Calcular'}
                  </button>
                </div>
                {errorRuta && (
                  <div className="text-xs text-red-500 font-medium bg-red-50 border border-red-100 p-2 rounded-lg mt-1">
                    {errorRuta}
                  </div>
                )}
              </div>
              
              {configRuta.categoryId && rutas[configRuta.categoryId] && (
                <div className="flex items-center gap-2 flex-wrap pt-2 border-t border-gray-100">
                  <span className="text-[11px] text-blue-700 font-bold bg-blue-50 border border-blue-200 px-2 py-1 rounded-full uppercase tracking-wide flex items-center gap-1">
                    <CheckCircle2 size={12} />
                    {rutas[configRuta.categoryId].algorithmType === 'FLOW' ? 'Optimización de Flujo' :
                     rutas[configRuta.categoryId].algorithmType === 'CONNECTIVITY' ? 'Cobertura Zonal' : 
                     'Ruta Vehicular Óptima'}
                  </span>
                  {rutas[configRuta.categoryId].metadata?.maxFlow !== undefined && (
                    <span className="text-[11px] text-purple-700 font-bold bg-purple-50 border border-purple-200 px-2 py-1 rounded-full uppercase tracking-wide">
                      Flujo Max: {rutas[configRuta.categoryId].metadata.maxFlow} u/s
                    </span>
                  )}
                  {rutas[configRuta.categoryId].metadata?.totalWeight !== undefined && (
                    <span className="text-[11px] text-orange-700 font-bold bg-orange-50 border border-orange-200 px-2 py-1 rounded-full uppercase tracking-wide">
                      Costo: {rutas[configRuta.categoryId].metadata.totalWeight.toFixed(2)}
                    </span>
                  )}
                  <span className="text-[11px] text-gray-700 font-bold bg-gray-100 border border-gray-200 px-2 py-1 rounded-full">
                    {rutas[configRuta.categoryId].waypoints.length} nodos
                  </span>
                  
                  {tabActual === 'pendientes' && (
                    <button
                      type="button"
                      onClick={handleAsignarRuta}
                      className="text-xs bg-emerald-500 hover:bg-emerald-600 text-white font-bold px-3 py-1.5 rounded-lg shadow-sm transition-colors animate-pulse-once"
                    >
                      Asignarme Ruta Completa
                    </button>
                  )}
                  
                  {/* Botones de simulación para presentación */}
                  {!simulando ? (
                    <button
                      type="button"
                      onClick={simularRecorrido}
                      className="text-xs bg-gray-800 hover:bg-gray-900 text-white font-bold px-3 py-1.5 rounded-lg shadow-sm transition-colors flex items-center gap-1"
                    >
                      <Route size={14} />
                      Iniciar Recorrido Piloto
                    </button>
                  ) : (
                    <button
                      type="button"
                      onClick={detenerSimulacion}
                      className="text-xs bg-red-600 hover:bg-red-700 text-white font-bold px-3 py-1.5 rounded-lg shadow-sm transition-colors animate-pulse flex items-center gap-1"
                    >
                      <X size={14} />
                      Finalizar Recorrido
                    </button>
                  )}

                  <button
                    type="button"
                    onClick={() => setRutas({})}
                    className="bg-blue-500 hover:bg-blue-600 text-white text-xs font-bold px-3 py-1.5 rounded-lg shadow-sm transition-colors flex items-center gap-1"
                    aria-label="Limpiar ruta calculada"
                    title="Limpiar ruta calculada"
                  >
                    <X size={14} strokeWidth={3} /> Limpiar
                  </button>
                </div>
              )}
            </form>
          </div>
        ) : (
          <button
            onClick={() => setPanelRutaAbierto(true)}
            className="absolute top-4 right-4 z-[1000] flex items-center gap-2 bg-white/90 backdrop-blur-md border border-gray-200 hover:border-blue-400 text-blue-600 hover:text-blue-700 text-sm font-semibold px-4 py-2.5 rounded-2xl shadow-lg transition-all"
            aria-label="Optimizar Ruta"
          >
            <Route size={16} />
            <span>Optimizar Ruta</span>
            <ChevronDown size={14} className="ml-1" />
          </button>
        )}

        <MapContainer
          center={reportesFiltrados.length > 0 ? [reportesFiltrados[0].latitude, reportesFiltrados[0].longitude] : DEFAULT_CENTER}
          zoom={13}
          className="w-full h-full"
          ref={setMapRef}
        >
          <TileLayer
            url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
            attribution='&copy; OpenStreetMap &copy; CARTO'
          />
          
          {/* Círculo de radio visual para la categoría activa */}
          {configRuta.categoryId && (() => {
            return null
          })()}
          
          {/* Marcador del Supervisor */}
          {ubicacionSupervisor ? (
            <Marker 
              position={[ubicacionSupervisor.latitude, ubicacionSupervisor.longitude]}
              icon={ICONO_SUPERVISOR}
              zIndexOffset={1000}
            >
              <Popup className="rtk-popup"><p className="text-xs font-bold text-white p-1">Tu ubicación actual (GPS)</p></Popup>
            </Marker>
          ) : (
            /* Fallback: Si no hay GPS, mostrar el camión en el inicio de la ruta calculada */
            Object.values(rutas).length > 0 && Object.values(rutas)[0].path.length > 0 && (
              <Marker 
                position={Object.values(rutas)[0].path[0]}
                icon={ICONO_SUPERVISOR}
                zIndexOffset={1000}
              >
                <Popup className="rtk-popup"><p className="text-xs font-bold text-white p-1">Inicio de ruta (GPS inactivo)</p></Popup>
              </Marker>
            )
          )}

          <MarkerClusterGroup 
            chunkedLoading 
            maxClusterRadius={40}
            iconCreateFunction={crearIconoCluster}
            spiderfyOnMaxZoom={true}
            showCoverageOnHover={false}
          >
            {reportesFiltrados.map(r => (
              <Marker 
                key={r.id} 
                position={[r.latitude, r.longitude]}
                icon={crearIconoReporte(r.status, r.reportCount ?? 1)}
                reporteStatus={r.status}
              >
                <Popup className="rtk-popup">
                  <div className="min-w-[150px]">
                    <strong className="text-white text-sm block mb-1">{r.title}</strong>
                    <span className="text-xs text-gray-300 block mb-2">{r.status}</span>
                    {r.reportCount > 1 && (
                      <span className="text-xs text-emerald-600 bg-emerald-50 px-2 py-1 rounded font-bold block mb-2">
                        {r.reportCount} respaldos (Super Reporte)
                      </span>
                    )}
                    {r.imageUrl && <img src={r.imageUrl} className="w-full h-24 object-cover rounded" />}
                  </div>
                </Popup>
              </Marker>
            ))}
          </MarkerClusterGroup>

          {/* Dibujar rutas optimizadas calculadas en cualquier pestaña */}
          {Object.entries(rutas).map(([catId, data]) => (
            <AnimatedPolyline key={catId} positions={data.path} color={data.color} algorithmType={data.algorithmType} />
          ))}
        </MapContainer>
      </div>

      </div>
      <ResolutionModal 
        isOpen={!!resolvingReportId}
        onClose={() => setResolvingReportId(null)}
        onResolve={handleResolver}
        reportId={resolvingReportId}
      />
      
      {historialAbierto && (
        <HistorialModal 
          isOpen={historialAbierto} 
          onClose={() => setHistorialAbierto(false)} 
          resueltos={reportesResueltos}
          onRevertir={(id) => handleCambiarEstado(id, 'IN_PROGRESS')}
        />
      )}
    </div>
  )
}

export default SupervisorPage
