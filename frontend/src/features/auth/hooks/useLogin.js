import { useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../../context/AuthContext'
import { iniciarSesion as loginAPI } from '../services/authService'

const ESTADO_INICIAL = { email: '', password: '' }
const REGEX_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export function useLogin() {
  const { iniciarSesion } = useAuth()
  const navegar = useNavigate()

  const [formulario, setFormulario] = useState(ESTADO_INICIAL)
  const [errores, setErrores] = useState({})
  const [mensajeError, setMensajeError] = useState('')
  const [cargando, setCargando] = useState(false)

  const manejarCambio = useCallback((e) => {
    const { name, value } = e.target
    setFormulario((prev) => ({ ...prev, [name]: value }))
    setErrores((prev) => (prev[name] ? { ...prev, [name]: '' } : prev))
  }, [])

  const limpiarError = useCallback(() => setMensajeError(''), [])

  function validar() {
    const nuevos = {}
    if (!formulario.email.trim()) {
      nuevos.email = 'El correo es obligatorio'
    } else if (!REGEX_EMAIL.test(formulario.email)) {
      nuevos.email = 'Ingresa un correo válido'
    }
    if (!formulario.password) nuevos.password = 'La contraseña es obligatoria'
    setErrores(nuevos)
    return Object.keys(nuevos).length === 0
  }

  async function manejarEnvio(e) {
    e.preventDefault()
    setMensajeError('')
    if (!validar()) return

    setCargando(true)
    try {
      // Backend espera: { email, password } → devuelve { token }
      const respuesta = await loginAPI({
        email: formulario.email,
        password: formulario.password,
      })

      iniciarSesion(respuesta.token, { email: formulario.email })
      navegar('/dashboard', { replace: true })
    } catch (error) {
      const status = error.response?.status
      if (status === 401 || status === 403) {
        setMensajeError('Correo o contraseña incorrectos. Intenta de nuevo.')
      } else if (status === 404) {
        setMensajeError('No existe una cuenta con ese correo. ¿Quieres registrarte?')
      } else if (!error.response) {
        setMensajeError('No se pudo conectar con el servidor. Verifica tu conexión.')
      } else {
        setMensajeError('Ocurrió un error inesperado. Intenta más tarde.')
      }
    } finally {
      setCargando(false)
    }
  }

  return { formulario, errores, mensajeError, cargando, manejarCambio, manejarEnvio, limpiarError }
}
