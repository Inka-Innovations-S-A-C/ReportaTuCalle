import { useState, useCallback } from 'react'
import { optimizarRuta } from '../services/reportService'

export function useRutaOptimizada() {
  const [ruta, setRuta] = useState(null)
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState(null)

  const calcularRuta = useCallback(async ({ categoryId, startLatitude, startLongitude, radiusInMeters }) => {
    setCargando(true)
    setError(null)
    setRuta(null)
    try {
      const resultado = await optimizarRuta({ categoryId, startLatitude, startLongitude, radiusInMeters })
      setRuta(resultado)
      return resultado
    } catch {
      setError('No se pudo calcular la ruta. Verifica que haya reportes de esa categoría en el radio indicado.')
    } finally {
      setCargando(false)
    }
  }, [])

  const limpiarRuta = useCallback(() => setRuta(null), [])

  return { ruta, cargando, error, calcularRuta, limpiarRuta }
}
