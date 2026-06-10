import { useEffect } from 'react'
import { MapContainer, TileLayer, Marker, Popup, Circle, Polyline, useMap } from 'react-leaflet'
import L from 'leaflet'
import EstadoBadge from '../../../shared/ui/EstadoBadge'
import Spinner from '../../../shared/ui/Spinner'

// Colores del marcador por estado del reporte
const COLOR_ESTADO = {
  PENDING:     '#f59e0b',
  IN_PROGRESS: '#3b82f6',
  RESOLVED:    '#22c55e',
  REJECTED:    '#9ca3af',
}

// DivIcon CSS — evita los problemas de importación de imágenes de Leaflet en Vite
function crearIconoReporte(status, reportCount) {
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
    width:18px; height:18px;
    background:#2563eb;
    border:3px solid white;
    border-radius:50%;
    box-shadow:0 0 0 4px rgba(37,99,235,0.25);
  "></div>`,
  iconSize: [18, 18],
  iconAnchor: [9, 9],
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

function MapaReportes({ posicion, reportes, cargandoGPS, ruta }) {
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

      {/* Marcadores de reportes */}
      {reportes.map((reporte) => (
        <Marker
          key={reporte.id}
          position={[reporte.latitude, reporte.longitude]}
          icon={crearIconoReporte(reporte.status, reporte.reportCount ?? 1)}
        >
          <Popup maxWidth={220} className="rtk-popup">
            <div className="space-y-1.5 py-0.5">
              <div className="flex items-start justify-between gap-2">
                <p className="font-semibold text-sm text-gray-800 leading-snug pr-1">
                  {reporte.title}
                </p>
                <EstadoBadge estado={reporte.status} />
              </div>
              <p className="text-xs text-gray-500 leading-relaxed line-clamp-3">
                {reporte.description}
              </p>
              {reporte.reportCount > 1 && (
                <p className="text-xs text-verde-600 font-medium">
                  {reporte.reportCount} ciudadanos reportaron esto
                </p>
              )}
              {reporte.imageUrl && (
                <img
                  src={reporte.imageUrl}
                  alt={reporte.title}
                  className="w-full h-28 object-cover rounded-lg mt-1"
                  loading="lazy"
                />
              )}
            </div>
          </Popup>
        </Marker>
      ))}
    </MapContainer>
  )
}

export default MapaReportes
