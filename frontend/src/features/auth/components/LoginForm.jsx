import { Link } from 'react-router-dom'
import { Mail, Lock, LogIn } from 'lucide-react'
import CampoInput from '../../../shared/ui/CampoInput'
import Boton from '../../../shared/ui/Boton'
import AlertaMensaje from '../../../shared/ui/AlertaMensaje'

// Componente puramente visual: no contiene estado ni lógica de negocio.
// Toda la lógica vive en useLogin.js.
function LoginForm({ formulario, errores, mensajeError, cargando, onCambio, onEnvio, onCerrarError }) {
  return (
    <>
      <h2 className="text-xl font-semibold text-gray-800 mb-1">Iniciar sesión</h2>
      <p className="text-sm text-gray-500 mb-6">Ingresa tus credenciales para continuar</p>

      {mensajeError && (
        <div className="mb-4">
          <AlertaMensaje mensaje={mensajeError} tipo="error" onCerrar={onCerrarError} />
        </div>
      )}

      <form onSubmit={onEnvio} className="space-y-4" noValidate>
        <CampoInput
          etiqueta="Correo electrónico"
          name="email"
          type="email"
          placeholder="tu@email.com"
          value={formulario.email}
          onChange={onCambio}
          error={errores.email}
          icono={Mail}
          autoComplete="email"
          autoFocus
        />

        <CampoInput
          etiqueta="Contraseña"
          name="password"
          type="password"
          placeholder="Tu contraseña"
          value={formulario.password}
          onChange={onCambio}
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
        <Link to="/registro" className="text-verde-600 font-semibold hover:underline focus:outline-none focus:underline">
          Regístrate aquí
        </Link>
      </p>
    </>
  )
}

export default LoginForm
