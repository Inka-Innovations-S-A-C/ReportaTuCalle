import apiClient from '../../../shared/lib/apiClient'

export async function crearReporte(datos) {
  const { data: envelope } = await apiClient.post('/reports', datos)
  return envelope.data // → ReportResponse
}

export async function obtenerReportesCercanos(latitude, longitude, radius = 5000) {
  const { data: envelope } = await apiClient.get('/reports/nearby', {
    params: { latitude, longitude, radius },
  })
  return envelope.data // → ReportResponse[]
}

export async function optimizarRuta({ categoryId, startLatitude, startLongitude, radiusInMeters = 5000 }) {
  const { data: envelope } = await apiClient.post('/reports/optimize-route', null, {
    params: { categoryId, startLatitude, startLongitude, radiusInMeters },
  })
  return envelope.data // → OptimizedRoute { orderedStops, totalDistanceKm }
}
