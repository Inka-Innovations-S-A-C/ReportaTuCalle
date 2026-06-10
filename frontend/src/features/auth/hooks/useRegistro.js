import { useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../../context/AuthContext'
import { registrarUsuario } from '../services/authService'

const ESTADO_INICIAL = { firstName: '', lastName: '', email: '', password: '', confirmarPassword: '' }
const REGEX_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export function useRegistro() {
  const { iniciarSesion } = useAuth()
  const navegar = useNavigate()

  const [formulario, setFormulario] = useState(ESTADO_INICIAL)
  const [errores, setErrores] = useState({})
  const [mensajeError, setMensajeError] = useState('')
  const [mensajeExito, setMensajeExito] = useState('')
  const [cargando, setCargando] = useState(false)

  const manejarCambio = useCallback((e) => {
    const { name, value } = e.target
    setFormulario((prev) => ({ ...prev, [name]: value }))
    setErrores((prev) => (prev[name] ? { ...prev, [name]: '' } : prev))
  }, [])

  const limpiarError = useCallback(() => setMensajeError(''), [])

  function validar() {
    const nuevos = {}

    if (!formulario.firstName.trim()) {
      nuevos.firstName = 'El nombre es obligatorio'
    } else if (formulario.firstName.length > 100) {
      nuevos.firstName = 'Máximo 100 caracteres'
    }

    if (!formulario.lastName.trim()) {
      nuevos.lastName = 'El apellido es obligatorio'
    } else if (formulario.lastName.length > 100) {
      nuevos.lastName = 'Máximo 100 caracteres'
    }

    if (!formulario.email.trim()) {
      nuevos.email = 'El correo es obligatorio'
    } else if (!REGEX_EMAIL.test(formulario.email)) {
      nuevos.email = 'Ingresa un correo válido'
    }

    if (!formulario.password) {
      nuevos.password = 'La contraseña es obligatoria'
    } else if (formulario.password.length < 6) {
      nuevos.password = 'Mínimo 6 caracteres'
    }

    if (!formulario.confirmarPassword) {
      nuevos.confirmarPassword = 'Confirma tu contraseña'
    } else if (formulario.password !== formulario.confirmarPassword) {
      nuevos.confirmarPassword = 'Las contraseñas no coinciden'
    }

    setErrores(nuevos)
    return Object.keys(nuevos).length === 0
  }

  async function manejarEnvio(e) {
    e.preventDefault()
    setMensajeError('')
    setMensajeExito('')
    if (!validar()) return

    setCargando(true)
    try {
      // Backend espera: { firstName, lastName, email, password }
      const respuesta = await registrarUsuario({
        firstName: formulario.firstName,
        lastName: formulario.lastName,
        email: formulario.email,
        password: formulario.password,
      })
      // El backend devuelve token en el registro → auto-login directo al dashboard
      if (respuesta?.token) {
        iniciarSesion(respuesta.token, { email: formulario.email })
        navegar('/dashboard', { replace: true })
      } else {
        setMensajeExito('¡Cuenta creada! Redirigiendo al login...')
        setTimeout(() => navegar('/login', { replace: true }), 1500)
      }
    } catch (error) {
      const status = error.response?.status
      const mensajeServidor = error.response?.data?.message ?? error.response?.data?.error

      if (status === 409) {
        setMensajeError('Ese nombre de usuario ya está en uso. Elige otro.')
      } else if (status === 400) {
        setMensajeError(mensajeServidor ?? 'Los datos ingresados no son válidos.')
      } else if (!error.response) {
        setMensajeError('No se pudo conectar con el servidor. Verifica tu conexión.')
      } else {
        setMensajeError('Ocurrió un error al registrarte. Intenta más tarde.')
      }
    } finally {
      setCargando(false)
    }
  }

  return {
    formulario,
    errores,
    mensajeError,
    mensajeExito,
    cargando,
    manejarCambio,
    manejarEnvio,
    limpiarError,
  }
}
