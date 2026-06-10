import { useState, useEffect, useCallback } from 'react'
import { obtenerMiPerfil, actualizarMiPerfil } from '../services/userService'

export function usePerfil() {
  const [perfil, setPerfil] = useState(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    let cancelado = false
    setCargando(true)
    obtenerMiPerfil()
      .then((data) => { if (!cancelado) setPerfil(data) })
      .catch(() => { if (!cancelado) setError('No se pudo cargar el perfil') })
      .finally(() => { if (!cancelado) setCargando(false) })
    return () => { cancelado = true }
  }, [])

  const actualizarPerfil = useCallback(async (datos) => {
    const actualizado = await actualizarMiPerfil(datos)
    setPerfil(actualizado)
    return actualizado
  }, [])

  return { perfil, cargando, error, actualizarPerfil }
}
