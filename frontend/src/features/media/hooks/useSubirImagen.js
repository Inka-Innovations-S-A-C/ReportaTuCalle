import { useState, useCallback } from 'react'
import { subirImagen } from '../services/mediaService'

export function useSubirImagen() {
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState(null)

  const subir = useCallback(async (archivo) => {
    if (!archivo) return null
    setError(null)
    setCargando(true)
    try {
      const url = await subirImagen(archivo)
      return url
    } catch {
      setError('No se pudo subir la imagen. Intenta con otro archivo.')
      return null
    } finally {
      setCargando(false)
    }
  }, [])

  return { subir, cargando, error }
}
