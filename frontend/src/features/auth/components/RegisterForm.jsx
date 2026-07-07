import { Link } from 'react-router-dom'
import { User, Lock, Mail, UserPlus } from 'lucide-react'
import CampoInput from '../../../shared/ui/CampoInput'
import Boton from '../../../shared/ui/Boton'
import AlertaMensaje from '../../../shared/ui/AlertaMensaje'

// Componente puramente visual: no contiene estado ni lógica de negocio.
// Toda la lógica vive en useRegistro.js.
function RegisterForm({
  formulario,
  errores,
  mensajeError,
  mensajeExito,
  cargando,
  onCambio,
  onEnvio,
  onCerrarError,
}) {
  return (
    <>
      <h2 className="text-2xl font-bold text-gray-100 mb-1 tracking-tight">Crear cuenta</h2>
      <p className="text-sm text-gray-400 mb-8 font-light">Únete a la comunidad ciudadana</p>

      {mensajeError && (
        <div className="mb-4">
          <AlertaMensaje mensaje={mensajeError} tipo="error" onCerrar={onCerrarError} />
        </div>
      )}
      {mensajeExito && (
        <div className="mb-4">
          <AlertaMensaje mensaje={mensajeExito} tipo="exito" />
        </div>
      )}

      <form onSubmit={onEnvio} className="space-y-4" noValidate>
        {/* Nombre y apellido en fila en pantallas medianas */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <CampoInput
            etiqueta="Nombre *"
            name="firstName"
            type="text"
            placeholder="Juan"
            value={formulario.firstName}
            onChange={onCambio}
            error={errores.firstName}
            icono={User}
            autoComplete="given-name"
            autoFocus
          />
          <CampoInput
            etiqueta="Apellido *"
            name="lastName"
            type="text"
            placeholder="Pérez"
            value={formulario.lastName}
            onChange={onCambio}
            error={errores.lastName}
            icono={User}
            autoComplete="family-name"
          />
        </div>

        <CampoInput
          etiqueta="Correo electrónico *"
          name="email"
          type="email"
          placeholder="tu@email.com"
          value={formulario.email}
          onChange={onCambio}
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
          onChange={onCambio}
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
          onChange={onCambio}
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

      <p className="text-center text-sm text-gray-400 mt-6">
        ¿Ya tienes cuenta?{' '}
        <Link to="/login" className="text-primary-400 font-semibold hover:text-primary-300 hover:underline focus:outline-none focus:underline transition-colors">
          Iniciar sesión
        </Link>
      </p>
    </>
  )
}

export default RegisterForm
