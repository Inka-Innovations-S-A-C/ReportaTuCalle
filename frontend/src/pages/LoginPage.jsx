import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { MapPin, User, Lock, LogIn } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { iniciarSesion as loginAPI } from '../services/authService'
import CampoInput from '../components/CampoInput'
import Boton from '../components/Boton'
import AlertaMensaje from '../components/AlertaMensaje'

function LoginPage() {
  const { iniciarSesion } = useAuth()
  const navegar = useNavigate()

  const [formulario, setFormulario] = useState({ username: '', password: '' })
  const [errores, setErrores] = useState({})
  const [mensajeError, setMensajeError] = useState('')
  const [cargando, setCargando] = useState(false)

  const manejarCambio = (e) => {
    const { name, value } = e.target
    setFormulario((prev) => ({ ...prev, [name]: value }))
    // Limpiar error del campo al escribir
    if (errores[name]) setErrores((prev) => ({ ...prev, [name]: '' }))
  }

  const validarFormulario = () => {
    const nuevosErrores = {}
    if (!formulario.username.trim()) nuevosErrores.username = 'El usuario es obligatorio'
    if (!formulario.password) nuevosErrores.password = 'La contraseña es obligatoria'
    setErrores(nuevosErrores)
    return Object.keys(nuevosErrores).length === 0
  }

  const manejarEnvio = async (e) => {
    e.preventDefault()
    setMensajeError('')

    if (!validarFormulario()) return

    setCargando(true)
    try {
      const respuesta = await loginAPI({
        username: formulario.username,
        password: formulario.password,
      })

      // El backend devuelve { token, username } u objeto de usuario
      const token = respuesta.token || respuesta.accessToken
      const datosUsuario = {
        username: respuesta.username || formulario.username,
        email: respuesta.email,
        id: respuesta.id,
      }

      iniciarSesion(token, datosUsuario)
      navegar('/dashboard')
    } catch (error) {
      const codigoEstado = error.response?.status
      if (codigoEstado === 401 || codigoEstado === 403) {
        setMensajeError('Usuario o contraseña incorrectos. Intenta de nuevo.')
      } else if (codigoEstado === 404) {
        setMensajeError('El usuario no existe. ¿Quizás quieres registrarte?')
      } else if (!error.response) {
        setMensajeError('No se pudo conectar con el servidor. Verifica tu conexión.')
      } else {
        setMensajeError('Ocurrió un error inesperado. Intenta más tarde.')
      }
    } finally {
      setCargando(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-azul-900 via-azul-800 to-verde-800 flex items-center justify-center p-4">
      <div className="w-full max-w-md">

        {/* Encabezado con logo */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-verde-500 rounded-2xl mb-4 shadow-lg">
            <MapPin className="text-white" size={32} />
          </div>
          <h1 className="text-3xl font-bold text-white">ReportaTuCalle</h1>
          <p className="text-gray-300 mt-1 text-sm">Reporta problemas en tu distrito</p>
        </div>

        {/* Tarjeta del formulario */}
        <div className="tarjeta">
          <h2 className="text-xl font-semibold text-gray-800 mb-6">Iniciar sesión</h2>

          <AlertaMensaje
            mensaje={mensajeError}
            tipo="error"
            onCerrar={() => setMensajeError('')}
          />

          <form onSubmit={manejarEnvio} className="mt-4 space-y-4" noValidate>
            <CampoInput
              etiqueta="Usuario"
              name="username"
              type="text"
              placeholder="Tu nombre de usuario"
              value={formulario.username}
              onChange={manejarCambio}
              error={errores.username}
              icono={User}
              autoComplete="username"
              autoFocus
            />

            <CampoInput
              etiqueta="Contraseña"
              name="password"
              type="password"
              placeholder="Tu contraseña"
              value={formulario.password}
              onChange={manejarCambio}
              error={errores.password}
              icono={Lock}
              autoComplete="current-password"
            />

            <Boton
              type="submit"
              variante="primario"
              cargando={cargando}
              icono={LogIn}
              className="mt-2"
            >
              {cargando ? 'Ingresando...' : 'Iniciar sesión'}
            </Boton>
          </form>

          <p className="text-center text-sm text-gray-500 mt-6">
            ¿No tienes cuenta?{' '}
            <Link to="/registro" className="text-verde-600 font-semibold hover:underline">
              Regístrate aquí
            </Link>
          </p>
        </div>

        <p className="text-center text-xs text-gray-400 mt-6">
          © {new Date().getFullYear()} Municipalidad · ReportaTuCalle
        </p>
      </div>
    </div>
  )
}

export default LoginPage
