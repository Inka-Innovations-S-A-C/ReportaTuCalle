import { useState, useEffect } from 'react'
import { Polyline, Marker } from 'react-leaflet'
import L from 'leaflet'

// Icono del camión de la brigada que irá pintando la ruta
const camionIcon = L.divIcon({
  className: 'bg-transparent',
  html: `<div class="w-8 h-8 bg-white rounded-full flex items-center justify-center shadow-lg border-2 border-fuchsia-500 shadow-fuchsia-500/50"><span class="text-lg">🚚</span></div>`,
  iconSize: [32, 32],
  iconAnchor: [16, 16],
})

export default function AnimatedPolyline({ positions, color, algorithmType = 'ROUTING' }) {
  if (!positions || positions.length < 2) return null;

  const activeColor = color || '#3b82f6'; // Default to blue
  
  // Configurar estilos visuales de la línea según el algoritmo físico
  let baseWeight = 4;
  let finalDashArray = null;

  if (algorithmType === 'CONNECTIVITY') {
    // Redes e infraestructura (cables): Línea punteada
    baseWeight = 5;
    finalDashArray = "10, 15";
  } else if (algorithmType === 'FLOW') {
    // Flujo (agua/alcantarillado): Trazos largos simulando corriente
    baseWeight = 6;
    finalDashArray = "30, 10";
  } else {
    // ROUTING (vehículos): Línea sólida y gruesa
    baseWeight = 5;
    finalDashArray = null;
  }

  const markerPos = positions[positions.length - 1];

  return (
    <>
      {/* Sombra/borde sutil */}
      <Polyline 
        positions={positions} 
        color={activeColor} 
        weight={baseWeight + 3} 
        opacity={0.3} 
        dashArray={finalDashArray}
      />
      
      {/* Línea principal */}
      <Polyline 
        positions={positions} 
        color={activeColor} 
        weight={baseWeight} 
        opacity={1} 
        dashArray={finalDashArray}
        lineCap="round"
        lineJoin="round"
      />
      
      {/* Camión o marcador en la punta (Opcional, lo dejamos fijo al final) */}
      {markerPos && (
        <Marker position={markerPos} icon={camionIcon} zIndexOffset={2000} />
      )}
    </>
  )
}
