import AuthLayout from '../components/AuthLayout'
import LoginForm from '../components/LoginForm'
import { useLogin } from '../hooks/useLogin'

function LoginPage() {
  const { formulario, errores, mensajeError, cargando, manejarCambio, manejarEnvio, limpiarError } =
    useLogin()

  return (
    <AuthLayout subtitulo="Reporta problemas en tu distrito">
      <LoginForm
        formulario={formulario}
        errores={errores}
        mensajeError={mensajeError}
        cargando={cargando}
        onCambio={manejarCambio}
        onEnvio={manejarEnvio}
        onCerrarError={limpiarError}
      />
    </AuthLayout>
  )
}

export default LoginPage
