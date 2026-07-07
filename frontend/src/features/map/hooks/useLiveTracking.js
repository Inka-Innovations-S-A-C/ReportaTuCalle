import { useState, useEffect, useRef } from 'react'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

export function useLiveTracking() {
  const [supervisoresVivos, setSupervisoresVivos] = useState({})
  const clientRef = useRef(null)

  useEffect(() => {
    const socket = new SockJS(`${import.meta.env.VITE_API_URL.replace('/api/v1', '')}/ws-tracking`)
    const stompClient = new Client({
      webSocketFactory: () => socket,
      debug: (str) => {
        // console.log(str)
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    })

    stompClient.onConnect = () => {
      stompClient.subscribe('/topic/supervisor-locations', (message) => {
        const payload = JSON.parse(message.body)
        setSupervisoresVivos((prev) => ({
          ...prev,
          [payload.supervisorId]: {
            latitude: payload.latitude,
            longitude: payload.longitude,
            timestamp: Date.now()
          }
        }))
      })
    }

    stompClient.activate()
    clientRef.current = stompClient

    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate()
      }
    }
  }, [])

  // Limpiar supervisores que no envían actualización hace más de 30 segundos
  useEffect(() => {
    const interval = setInterval(() => {
      const ahora = Date.now()
      setSupervisoresVivos(prev => {
        const nuevo = { ...prev }
        let cambio = false
        Object.keys(nuevo).forEach(id => {
          if (ahora - nuevo[id].timestamp > 30000) {
            delete nuevo[id]
            cambio = true
          }
        })
        return cambio ? nuevo : prev
      })
    }, 10000)
    return () => clearInterval(interval)
  }, [])

  // Función para enviar (usada solo por el supervisor)
  const enviarUbicacion = (supervisorId, latitude, longitude) => {
    if (clientRef.current && clientRef.current.connected) {
      clientRef.current.publish({
        destination: '/app/supervisor.location',
        body: JSON.stringify({ supervisorId, latitude, longitude })
      })
    }
  }

  return { supervisoresVivos, enviarUbicacion }
}
