import { createContext, useContext, useState, useEffect } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null)
  const [cargando, setCargando] = useState(true)

  // Al montar, recuperar sesión guardada en localStorage
  useEffect(() => {
    const tokenGuardado = localStorage.getItem('rtk_token')
    const usuarioGuardado = localStorage.getItem('rtk_usuario')

    if (tokenGuardado && usuarioGuardado) {
      try {
        setUsuario(JSON.parse(usuarioGuardado))
      } catch {
        // Si los datos están corruptos, limpiar sesión
        localStorage.removeItem('rtk_token')
        localStorage.removeItem('rtk_usuario')
      }
    }

    setCargando(false)
  }, [])

  const iniciarSesion = (token, datosUsuario) => {
    localStorage.setItem('rtk_token', token)
    localStorage.setItem('rtk_usuario', JSON.stringify(datosUsuario))
    setUsuario(datosUsuario)
  }

  const cerrarSesion = () => {
    localStorage.removeItem('rtk_token')
    localStorage.removeItem('rtk_usuario')
    setUsuario(null)
  }

  const estaAutenticado = Boolean(usuario)

  return (
    <AuthContext.Provider value={{ usuario, cargando, estaAutenticado, iniciarSesion, cerrarSesion }}>
      {children}
    </AuthContext.Provider>
  )
}

// Hook personalizado para consumir el contexto fácilmente
export function useAuth() {
  const contexto = useContext(AuthContext)
  if (!contexto) {
    throw new Error('useAuth debe usarse dentro de un AuthProvider')
  }
  return contexto
}
