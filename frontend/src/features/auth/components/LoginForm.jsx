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
      <h2 className="text-2xl font-bold text-gray-100 mb-1 tracking-tight">Iniciar sesión</h2>
      <p className="text-sm text-gray-400 mb-8 font-light">Ingresa tus credenciales para continuar</p>

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

      <p className="text-center text-sm text-gray-400 mt-6">
        ¿No tienes cuenta?{' '}
        <Link to="/registro" className="text-primary-400 font-semibold hover:text-primary-300 hover:underline focus:outline-none focus:underline transition-colors">
          Regístrate aquí
        </Link>
      </p>
    </>
  )
}

export default LoginForm
