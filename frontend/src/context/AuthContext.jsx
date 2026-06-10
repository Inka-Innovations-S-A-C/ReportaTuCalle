import { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { setUnauthorizedHandler } from '../shared/lib/apiClient'

// ---------------------------------------------------------------------------
// Helpers de JWT — decodifican el payload sin verificar la firma (solo cliente)
// ---------------------------------------------------------------------------

function parsearPayloadJwt(token) {
  try {
    // El payload es el segundo segmento del JWT, codificado en base64url
    const segmento = token.split('.')[1]
    return JSON.parse(atob(segmento.replace(/-/g, '+').replace(/_/g, '/')))
  } catch {
    return null
  }
}

function tokenEsValido(token) {
  const payload = parsearPayloadJwt(token)
  if (!payload?.exp) return false
  // `exp` está en segundos; Date.now() en milisegundos
  return payload.exp * 1000 > Date.now()
}

// ---------------------------------------------------------------------------
// Contexto
// ---------------------------------------------------------------------------

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const navegar = useNavigate()
  const [usuario, setUsuario] = useState(null)
  const [cargando, setCargando] = useState(true)

  // Restaura la sesión guardada al montar, descartando tokens expirados
  useEffect(() => {
    const token = localStorage.getItem('rtk_token')
    const usuarioRaw = localStorage.getItem('rtk_usuario')

    if (token && usuarioRaw && tokenEsValido(token)) {
      try {
        const parsed = JSON.parse(usuarioRaw)
        // Asegura que el role siempre venga del JWT (fuente de verdad)
        if (!parsed.role) {
          const payload = parsearPayloadJwt(token)
          parsed.role = payload?.role ?? 'CITIZEN'
        }
        setUsuario(parsed)
      } catch {
        localStorage.removeItem('rtk_token')
        localStorage.removeItem('rtk_usuario')
      }
    } else if (token || usuarioRaw) {
      // Token expirado o datos incompletos → limpiar sin redirigir aún
      localStorage.removeItem('rtk_token')
      localStorage.removeItem('rtk_usuario')
    }

    setCargando(false)
  }, [])

  const cerrarSesion = useCallback(() => {
    localStorage.removeItem('rtk_token')
    localStorage.removeItem('rtk_usuario')
    setUsuario(null)
    navegar('/login', { replace: true })
  }, [navegar])

  // Registra cerrarSesion como handler del interceptor 401 de Axios.
  // Así el interceptor puede hacer logout sin importar React directamente.
  useEffect(() => {
    setUnauthorizedHandler(cerrarSesion)
    return () => setUnauthorizedHandler(null)
  }, [cerrarSesion])

  const iniciarSesion = useCallback((token, datosUsuario) => {
    const payload = parsearPayloadJwt(token)
    const usuarioCompleto = { ...datosUsuario, role: payload?.role ?? 'CITIZEN' }
    localStorage.setItem('rtk_token', token)
    localStorage.setItem('rtk_usuario', JSON.stringify(usuarioCompleto))
    setUsuario(usuarioCompleto)
  }, [])

  return (
    <AuthContext.Provider
      value={{
        usuario,
        cargando,
        estaAutenticado: Boolean(usuario),
        iniciarSesion,
        cerrarSesion,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth debe usarse dentro de un AuthProvider')
  return ctx
}
