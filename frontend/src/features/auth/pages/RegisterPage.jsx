import AuthLayout from '../components/AuthLayout'
import RegisterForm from '../components/RegisterForm'
import { useRegistro } from '../hooks/useRegistro'

function RegisterPage() {
  const {
    formulario,
    errores,
    mensajeError,
    mensajeExito,
    cargando,
    manejarCambio,
    manejarEnvio,
    limpiarError,
  } = useRegistro()

  return (
    <AuthLayout subtitulo="Crea tu cuenta ciudadana">
      <RegisterForm
        formulario={formulario}
        errores={errores}
        mensajeError={mensajeError}
        mensajeExito={mensajeExito}
        cargando={cargando}
        onCambio={manejarCambio}
        onEnvio={manejarEnvio}
        onCerrarError={limpiarError}
      />
    </AuthLayout>
  )
}

export default RegisterPage
