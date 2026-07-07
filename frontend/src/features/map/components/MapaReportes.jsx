import { useEffect } from 'react'
import { MapContainer, TileLayer, Marker, Popup, Circle, Polyline, useMap } from 'react-leaflet'
import MarkerClusterGroup from 'react-leaflet-cluster'
import L from 'leaflet'
import EstadoBadge from '../../../shared/ui/EstadoBadge'
import Spinner from '../../../shared/ui/Spinner'
import { useLiveTracking } from '../hooks/useLiveTracking'

// Fix for leaflet default icons
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
})

// Colores del marcador por estado del reporte
const COLOR_ESTADO = {
  PENDING:     '#f59e0b',
  IN_PROGRESS: '#3b82f6',
  RESOLVED:    '#22c55e',
  REJECTED:    '#9ca3af',
}

const ICONO_SUPERVISOR_VIVO = L.divIcon({
  className: '',
  html: `<div style="
    width: 32px; height: 32px;
    background: #ef4444;
    border: 2px solid white;
    border-radius: 50% 50% 50% 0;
    transform: rotate(-45deg);
    box-shadow: -2px 2px 8px rgba(0,0,0,0.4);
    display: flex; align-items: center; justify-content: center;
    transition: all 0.3s ease;
  ">
    <div style="transform: rotate(45deg); font-size: 14px;">🚚</div>
  </div>`,
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
})

// DivIcon CSS — evita los problemas de importación de imágenes de Leaflet en Vite
export function crearIconoReporte(status, reportCount) {
  const color = COLOR_ESTADO[status] ?? COLOR_ESTADO.PENDING
  const base = 16
  // El radio crece logarítmicamente con el nº de endorsements
  const size = Math.min(base + Math.log2(reportCount + 1) * 5, 40)
  const mostrarConteo = reportCount > 1

  return L.divIcon({
    className: '',
    html: `<div style="
      width:${size}px; height:${size}px;
      background:${color};
      border:2.5px solid white;
      border-radius:50%;
      box-shadow:0 2px 8px rgba(0,0,0,0.3);
      display:flex; align-items:center; justify-content:center;
      color:white; font-size:${size < 24 ? 9 : 11}px; font-weight:700;
      cursor:pointer;
    ">${mostrarConteo ? reportCount : ''}</div>`,
    iconSize: [size, size],
    iconAnchor: [size / 2, size / 2],
    popupAnchor: [0, -(size / 2 + 4)],
  })
}

const ICONO_USUARIO = L.divIcon({
  className: '',
  html: `<div style="
    width: 36px; height: 36px;
    background: #2563eb;
    border: 3px solid white;
    border-radius: 50% 50% 50% 0;
    transform: rotate(-45deg);
    box-shadow: -2px 2px 6px rgba(0,0,0,0.4);
    display: flex; align-items: center; justify-content: center;
  ">
    <div style="width: 12px; height: 12px; background: white; border-radius: 50%; margin: 2px;"></div>
  </div>`,
  iconSize: [36, 36],
  iconAnchor: [18, 36],
  popupAnchor: [0, -36],
})

// Centra el mapa en la posición del usuario cuando llega
function ControlVista({ posicion }) {
  const mapa = useMap()
  useEffect(() => {
    if (posicion) {
      mapa.setView([posicion.latitude, posicion.longitude], 16, { animate: true })
    }
  }, [posicion, mapa])
  return null
}

function crearIconoParada(numero) {
  return L.divIcon({
    className: '',
    html: `<div style="
      width:26px; height:26px;
      background:#7c3aed;
      border:2.5px solid white;
      border-radius:50%;
      box-shadow:0 2px 8px rgba(0,0,0,0.35);
      display:flex; align-items:center; justify-content:center;
      color:white; font-size:11px; font-weight:700;
    ">${numero}</div>`,
    iconSize: [26, 26],
    iconAnchor: [13, 13],
    popupAnchor: [0, -17],
  })
}

// Función para crear un icono de cluster dinámico (Gráfico de Dona por Estado)
export const crearIconoCluster = (cluster) => {
  const marcadores = cluster.getAllChildMarkers()
  const total = marcadores.length

  // Contar cuántos reportes hay de cada estado en este cluster
  const conteoEstados = { PENDING: 0, IN_PROGRESS: 0, RESOLVED: 0, REJECTED: 0 }
  marcadores.forEach((marcador) => {
    const estado = marcador.options.reporteStatus || 'PENDING'
    conteoEstados[estado] = (conteoEstados[estado] || 0) + 1
  })

  // Crear el CSS del gradient cónico para el gráfico de dona
  let gradientStops = []
  let acumulado = 0
  
  Object.entries(conteoEstados).forEach(([estado, count]) => {
    if (count > 0) {
      const porcentaje = (count / total) * 100
      const color = COLOR_ESTADO[estado]
      gradientStops.push(`${color} ${acumulado}% ${acumulado + porcentaje}%`)
      acumulado += porcentaje
    }
  })

  const conicGradient = gradientStops.length > 0 
    ? `conic-gradient(${gradientStops.join(', ')})`
    : `conic-gradient(#f59e0b 0% 100%)`

  const size = Math.min(40 + (total * 1.5), 70) // Crece un poco con la cantidad

  // Retornamos un DivIcon nativo de Leaflet
  return L.divIcon({
    html: `
      <div style="
        width: ${size}px; height: ${size}px;
        background: ${conicGradient};
        border-radius: 50%;
        display: flex; align-items: center; justify-content: center;
        box-shadow: 0 4px 10px rgba(0,0,0,0.3);
        transition: transform 0.2s ease-in-out;
      " class="hover:scale-110">
        <div style="
          width: ${size - 12}px; height: ${size - 12}px;
          background: #ffffff;
          border-radius: 50%;
          display: flex; align-items: center; justify-content: center;
          font-weight: bold; font-size: ${size < 50 ? 12 : 14}px;
          color: #1f2937;
        ">
          ${total}
        </div>
      </div>
    `,
    className: 'custom-marker-cluster',
    iconSize: L.point(size, size, true),
    iconAnchor: [size / 2, size / 2],
  })
}

function MapaReportes({ posicion, reportes, cargandoGPS, ruta, usuario, onAutoAsignar }) {
  const { supervisoresVivos } = useLiveTracking()
  
  const centro = posicion
    ? [posicion.latitude, posicion.longitude]
    : [-12.0464, -77.0428] // Lima por defecto

  if (cargandoGPS) {
    return (
      <div className="w-full h-full flex items-center justify-center bg-gray-100">
        <div className="flex flex-col items-center gap-3 text-gray-500">
          <Spinner />
          <p className="text-sm">Obteniendo tu ubicación...</p>
        </div>
      </div>
    )
  }

  return (
    <MapContainer
      center={centro}
      zoom={15}
      className="w-full h-full"
      zoomControl={false}
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />

      <ControlVista posicion={posicion} />

      {/* Marcador del usuario */}
      {posicion && (
        <>
          <Circle
            center={[posicion.latitude, posicion.longitude]}
            radius={80}
            pathOptions={{ color: '#2563eb', fillColor: '#2563eb', fillOpacity: 0.08, weight: 1 }}
          />
          <Marker
            position={[posicion.latitude, posicion.longitude]}
            icon={ICONO_USUARIO}
          >
            <Popup>
              <p className="text-xs font-medium text-gray-700">Tu ubicación actual</p>
            </Popup>
          </Marker>
        </>
      )}

      {/* Ruta optimizada: polyline + marcadores numerados */}
      {ruta?.orderedStops?.length > 0 && (
        <>
          <Polyline
            positions={ruta.orderedStops.map((p) => [p.latitude, p.longitude])}
            pathOptions={{ color: '#7c3aed', weight: 4, opacity: 0.85, dashArray: '8 6' }}
          />
          {ruta.orderedStops.map((parada, idx) => (
            <Marker
              key={`ruta-${idx}`}
              position={[parada.latitude, parada.longitude]}
              icon={crearIconoParada(idx + 1)}
            >
              <Popup>
                <p className="text-xs font-semibold text-purple-700">Parada {idx + 1}</p>
                <p className="text-xs text-gray-500 font-mono mt-0.5">
                  {parada.latitude.toFixed(5)}, {parada.longitude.toFixed(5)}
                </p>
              </Popup>
            </Marker>
          ))}
        </>
      )}

      {/* Marcadores de reportes con Clustering Inteligente */}
      <MarkerClusterGroup
        chunkedLoading
        maxClusterRadius={50}
        iconCreateFunction={crearIconoCluster}
        spiderfyOnMaxZoom={true}
        showCoverageOnHover={false}
      >
        {reportes.map((reporte) => (
          <Marker
            key={reporte.id}
            position={[reporte.latitude, reporte.longitude]}
            icon={crearIconoReporte(reporte.status, reporte.reportCount ?? 1)}
            reporteStatus={reporte.status} // Pasamos el status al marcador para que el cluster lo lea
          >
            <Popup maxWidth={220} className="rtk-popup">
              <div className="space-y-1.5 py-0.5">
                <div className="flex items-start justify-between gap-2">
                  <p className="font-semibold text-sm text-white leading-snug pr-1">
                    {reporte.title}
                  </p>
                  <EstadoBadge estado={reporte.status} />
                </div>
                <p className="text-xs text-gray-300 leading-relaxed line-clamp-3">
                  {reporte.description}
                </p>
                {reporte.reportCount > 1 && (
                  <p className="text-xs text-verde-600 font-medium">
                    {reporte.reportCount} ciudadanos reportaron esto
                  </p>
                )}
                {reporte.imageUrl && (
                  <div className="flex gap-2 mt-1">
                    <div className="flex-1 relative group">
                      <img
                        src={reporte.imageUrl}
                        alt="Reporte Original"
                        className="w-full h-24 object-cover rounded-lg border border-gray-600/50"
                        loading="lazy"
                      />
                      <div className="absolute inset-x-0 bottom-0 bg-black/60 text-[10px] text-white text-center py-0.5 rounded-b-lg font-medium opacity-0 group-hover:opacity-100 transition-opacity">
                        Original
                      </div>
                    </div>
                    {reporte.status === 'RESOLVED' && reporte.resolutionImageUrl && (
                      <div className="flex-1 relative group">
                        <img
                          src={reporte.resolutionImageUrl}
                          alt="Resolución"
                          className="w-full h-24 object-cover rounded-lg border-2 border-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.3)]"
                          loading="lazy"
                        />
                        <div className="absolute inset-x-0 bottom-0 bg-emerald-500/90 text-[10px] text-white text-center py-0.5 rounded-b-lg font-bold">
                          Resuelto
                        </div>
                      </div>
                    )}
                  </div>
                )}
                {usuario?.role === 'SUPERVISOR' && reporte.status === 'PENDING' && (
                  <button
                    onClick={() => onAutoAsignar(reporte.id)}
                    className="mt-2 w-full bg-gradient-to-r from-primary-600 to-primary-500 hover:from-primary-500 hover:to-primary-400 text-white text-xs font-bold py-2 rounded-lg shadow transition-all"
                  >
                    Asignarme este reporte
                  </button>
                )}
              </div>
            </Popup>
          </Marker>
        ))}
      </MarkerClusterGroup>

      {/* Camioncitos en vivo moviéndose por la ciudad (modo Uber) */}
      {Object.entries(supervisoresVivos).map(([id, sup]) => (
        <Marker 
          key={`sup-${id}`} 
          position={[sup.latitude, sup.longitude]} 
          icon={ICONO_SUPERVISOR_VIVO}
          zIndexOffset={1000}
        >
          <Popup><p className="text-xs font-bold text-gray-800 text-center">Unidad Municipal<br/>en camino 🚚</p></Popup>
        </Marker>
      ))}
    </MapContainer>
  )
}

export default MapaReportes
