import { MapPin } from 'lucide-react'

// Layout compartido para LoginPage y RegisterPage.
// Renderiza el fondo degradado, logo, título y la tarjeta blanca que envuelve al formulario.
function AuthLayout({ subtitulo, children }) {
  return (
    <div className="min-h-screen bg-gradient-to-br from-azul-900 via-azul-800 to-verde-800 flex items-center justify-center p-4">
      <div className="w-full max-w-md">

        {/* Logo + branding */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-verde-500 rounded-2xl mb-4 shadow-lg">
            <MapPin className="text-white" size={32} aria-hidden="true" />
          </div>
          <h1 className="text-3xl font-bold text-white tracking-tight">ReportaTuCalle</h1>
          {subtitulo && (
            <p className="text-gray-300 mt-1.5 text-sm">{subtitulo}</p>
          )}
        </div>

        {/* Tarjeta del formulario */}
        <div className="bg-white rounded-2xl shadow-2xl px-6 py-8 sm:px-8">
          {children}
        </div>

        <p className="text-center text-xs text-gray-400 mt-6">
          © {new Date().getFullYear()} Municipalidad · ReportaTuCalle
        </p>
      </div>
    </div>
  )
}

export default AuthLayout
