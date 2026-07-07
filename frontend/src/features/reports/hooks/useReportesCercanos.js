import { useState, useEffect, useCallback } from 'react'
import { obtenerReportesCercanos } from '../services/reportService'

export function useReportesCercanos(posicion) {
  const [reportes, setReportes] = useState([])
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState(null)
  const [notificacion, setNotificacion] = useState(null)
  const token = localStorage.getItem('rtk_token')

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

  useEffect(() => {
    if (!token) return
    const url = `${import.meta.env.VITE_API_URL}/reports/stream?token=${token}`
    const eventSource = new EventSource(url)
    
    eventSource.addEventListener('REPORT_CREATED', (e) => {
      try {
        const newReport = JSON.parse(e.data)
        setReportes(prev => {
          // Evitar duplicados
          if (prev.some(r => r.id === newReport.id)) return prev;
          return [newReport, ...prev];
        })
      } catch (err) {}
    })
    
    eventSource.addEventListener('REPORT_UPDATED', (e) => {
      try {
        const data = JSON.parse(e.data)
        setReportes(prev => {
          const oldReport = prev.find(r => r.id === data.id)
          
          // Ejecutamos notificaciones fuera del ciclo de renderizado (React safe)
          setTimeout(() => {
            if (data.status === 'RESOLVED' && (!oldReport || oldReport.status !== 'RESOLVED')) {
              setNotificacion(`El reporte "${data.title}" ha sido resuelto. ¡Gracias por tu participación!`)
              setTimeout(() => setNotificacion(null), 6000)
            } else if (oldReport && oldReport.status === 'RESOLVED' && data.status !== 'RESOLVED') {
              setNotificacion(`Atención: El reporte "${data.title}" volvió a revisión. Se retiraron los puntos temporales.`)
              setTimeout(() => setNotificacion(null), 8000)
            }
          }, 0)
          
          return prev.map(r => r.id === data.id ? data : r)
        })
      } catch (err) {
        // Ignorar errores de parseo
      }
    })

    eventSource.addEventListener('REPORT_ASSIGNED', (e) => {
      try {
        const data = JSON.parse(e.data)
        setReportes(prev => prev.map(r => r.id === data.id ? data : r))
      } catch (err) {}
    })
    
    return () => {
      eventSource.close()
    }
  }, [token])

  return { reportes, cargando, error, recargar: cargar, notificacion }
}
