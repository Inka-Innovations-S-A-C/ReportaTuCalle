import { useState, useEffect, useCallback } from 'react'
import { obtenerReportesCercanos } from '../services/reportService'

export function useReportesCercanos(posicion) {
  const [reportes, setReportes] = useState([])
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState(null)

  const cargar = useCallback(async () => {
    if (!posicion) return
    setCargando(true)
    setError(null)
    try {
      const data = await obtenerReportesCercanos(posicion.latitude, posicion.longitude)
      setReportes(data ?? [])
    } catch {
      setError('No se pudieron cargar los reportes cercanos.')
    } finally {
      setCargando(false)
    }
  }, [posicion])

  useEffect(() => { cargar() }, [cargar])

  return { reportes, cargando, error, recargar: cargar }
}
