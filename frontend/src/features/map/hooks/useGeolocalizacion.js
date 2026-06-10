import { useState, useEffect } from 'react'

// Lima, Perú como fallback cuando el usuario niega el GPS
const POSICION_DEFECTO = { latitude: -12.0464, longitude: -77.0428 }

export function useGeolocalizacion() {
  const [posicion, setPosicion] = useState(null)
  const [error, setError] = useState(null)
  const [cargando, setCargando] = useState(true)

  useEffect(() => {
    if (!navigator.geolocation) {
      setError('Tu navegador no soporta geolocalización.')
      setPosicion(POSICION_DEFECTO)
      setCargando(false)
      return
    }

    navigator.geolocation.getCurrentPosition(
      (pos) => {
        setPosicion({
          latitude: pos.coords.latitude,
          longitude: pos.coords.longitude,
        })
        setCargando(false)
      },
      (err) => {
        setError(
          err.code === 1
            ? 'Permiso de ubicación denegado. Mostrando Lima.'
            : 'No se pudo obtener tu ubicación. Mostrando Lima.'
        )
        setPosicion(POSICION_DEFECTO)
        setCargando(false)
      },
      { enableHighAccuracy: true, timeout: 10000, maximumAge: 60000 }
    )
  }, [])

  return { posicion, error, cargando }
}
