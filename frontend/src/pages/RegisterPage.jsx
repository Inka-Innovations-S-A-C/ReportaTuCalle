import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { MapPin, User, Lock, Mail, UserPlus } from 'lucide-react'
import { registrarUsuario } from '../services/authService'
import CampoInput from '../components/CampoInput'
import Boton from '../components/Boton'
import AlertaMensaje from '../components/AlertaMensaje'

function RegisterPage() {
  const navegar = useNavigate()

  const [formulario, setFormulario] = useState({
    username: '',
    email: '',
    password: '',
    confirmarPassword: '',
  })
  const [errores, setErrores] = useState({})
  const [mensajeError, setMensajeError] = useState('')
  const [mensajeExito, setMensajeExito] = useState('')
  const [cargando, setCargando] = useState(false)

  const manejarCambio = (e) => {
    const { name, value } = e.target
    setFormulario((prev) => ({ ...prev, [name]: value }))
    if (errores[name]) setErrores((prev) => ({ ...prev, [name]: '' }))
  }

  const validarFormulario = () => {
    const nuevosErrores = {}

    if (!formulario.username.trim()) {
      nuevosErrores.username = 'El usuario es obligatorio'
    } else if (formulario.username.length < 3) {
      nuevosErrores.username = 'El usuario debe tener al menos 3 caracteres'
    }

    if (formulario.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formulario.email)) {
      nuevosErrores.email = 'Ingresa un email válido'
    }

    if (!formulario.password) {
      nuevosErrores.password = 'La contraseña es obligatoria'
    } else if (formulario.password.length < 6) {
      nuevosErrores.password = 'La contraseña debe tener al menos 6 caracteres'
    }

    if (!formulario.confirmarPassword) {
      nuevosErrores.confirmarPassword = 'Confirma tu contraseña'
    } else if (formulario.password !== formulario.confirmarPassword) {
      nuevosErrores.confirmarPassword = 'Las contraseñas no coinciden'
    }

    setErrores(nuevosErrores)
    return Object.keys(nuevosErrores).length === 0
  }

  const manejarEnvio = async (e) => {
    e.preventDefault()
    setMensajeError('')
    setMensajeExito('')

    if (!validarFormulario()) return

    setCargando(true)
    try {
      await registrarUsuario({
        username: formulario.username,
        email: formulario.email || undefined,
        password: formulario.password,
      })

      setMensajeExito('¡Cuenta creada exitosamente! Redirigiendo al login...')
      setTimeout(() => navegar('/login'), 2000)
    } catch (error) {
      const codigoEstado = error.response?.status
      const mensajeServidor = error.response?.data?.message || error.response?.data?.error

      if (codigoEstado === 409) {
        setMensajeError('Ese nombre de usuario ya está en uso. Elige otro.')
      } else if (codigoEstado === 400) {
        setMensajeError(mensajeServidor || 'Los datos ingresados no son válidos.')
      } else if (!error.response) {
        setMensajeError('No se pudo conectar con el servidor. Verifica tu conexión.')
      } else {
        setMensajeError('Ocurrió un error al registrarte. Intenta más tarde.')
      }
    } finally {
      setCargando(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-azul-900 via-azul-800 to-verde-800 flex items-center justify-center p-4">
      <div className="w-full max-w-md">

        {/* Encabezado */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-verde-500 rounded-2xl mb-4 shadow-lg">
            <MapPin className="text-white" size={32} />
          </div>
          <h1 className="text-3xl font-bold text-white">ReportaTuCalle</h1>
          <p className="text-gray-300 mt-1 text-sm">Crea tu cuenta ciudadana</p>
        </div>

        {/* Tarjeta del formulario */}
        <div className="tarjeta">
          <h2 className="text-xl font-semibold text-gray-800 mb-6">Crear cuenta</h2>

          <AlertaMensaje mensaje={mensajeError} tipo="error" onCerrar={() => setMensajeError('')} />
          <AlertaMensaje mensaje={mensajeExito} tipo="exito" />

          <form onSubmit={manejarEnvio} className="mt-4 space-y-4" noValidate>
            <CampoInput
              etiqueta="Nombre de usuario *"
              name="username"
              type="text"
              placeholder="Ej: juan.perez"
              value={formulario.username}
              onChange={manejarCambio}
              error={errores.username}
              icono={User}
              autoComplete="username"
              autoFocus
            />

            <CampoInput
              etiqueta="Correo electrónico (opcional)"
              name="email"
              type="email"
              placeholder="tu@email.com"
              value={formulario.email}
              onChange={manejarCambio}
              error={errores.email}
              icono={Mail}
              autoComplete="email"
            />

            <CampoInput
              etiqueta="Contraseña *"
              name="password"
              type="password"
              placeholder="Mínimo 6 caracteres"
              value={formulario.password}
              onChange={manejarCambio}
              error={errores.password}
              icono={Lock}
              autoComplete="new-password"
            />

            <CampoInput
              etiqueta="Confirmar contraseña *"
              name="confirmarPassword"
              type="password"
              placeholder="Repite tu contraseña"
              value={formulario.confirmarPassword}
              onChange={manejarCambio}
              error={errores.confirmarPassword}
              icono={Lock}
              autoComplete="new-password"
            />

            <Boton
              type="submit"
              variante="primario"
              cargando={cargando}
              icono={UserPlus}
              className="mt-2"
            >
              {cargando ? 'Creando cuenta...' : 'Registrarme'}
            </Boton>
          </form>

          <p className="text-center text-sm text-gray-500 mt-6">
            ¿Ya tienes cuenta?{' '}
            <Link to="/login" className="text-verde-600 font-semibold hover:underline">
              Iniciar sesión
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

export default RegisterPage
