import { MapPin } from 'lucide-react'

// Layout compartido para LoginPage y RegisterPage.
// Renderiza el fondo degradado, logo, título y la tarjeta blanca que envuelve al formulario.
function AuthLayout({ subtitulo, children }) {
  return (
    <div className="min-h-screen bg-background bg-glass-gradient flex items-center justify-center p-4 relative overflow-hidden">
      {/* Dynamic background shapes */}
      <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] rounded-full bg-primary-600/20 blur-[100px] animate-float"></div>
      <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] rounded-full bg-accent-600/20 blur-[100px] animate-float" style={{ animationDelay: '2s' }}></div>
      
      <div className="w-full max-w-md relative z-10 animate-slide-up">

        {/* Logo + branding */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-primary-500 to-accent-500 rounded-2xl mb-4 shadow-glow">
            <MapPin className="text-white drop-shadow-md" size={32} aria-hidden="true" />
          </div>
          <h1 className="text-3xl font-bold text-white tracking-tight">ReportaTuCalle</h1>
          {subtitulo && (
            <p className="text-gray-400 mt-1.5 text-sm font-medium">{subtitulo}</p>
          )}
        </div>

        {/* Tarjeta del formulario */}
        <div className="glass-panel rounded-3xl p-6 sm:p-8">
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
