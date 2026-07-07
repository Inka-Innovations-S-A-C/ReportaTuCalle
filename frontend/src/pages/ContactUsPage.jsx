import Navbar from '../shared/ui/Navbar'
import { useState } from 'react'
import { Mail, MessageSquare, Send, CheckCircle } from 'lucide-react'
import { useAuth } from '../context/AuthContext'

function ContactUsPage() {
  const { usuario } = useAuth()
  const [enviado, setEnviado] = useState(false)
  const [cargando, setCargando] = useState(false)

  const manejarEnvio = (e) => {
    e.preventDefault()
    setCargando(true)
    // Simular envío de correo al backend
    setTimeout(() => {
      setCargando(false)
      setEnviado(true)
    }, 1500)
  }

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      
      <main className="container mx-auto px-4 py-12 max-w-2xl animate-fade-in mt-16">
        <div className="glass-panel p-8 rounded-3xl relative overflow-hidden">
          {/* Decorative background elements */}
          <div className="absolute top-0 right-0 -mr-16 -mt-16 w-64 h-64 bg-primary-600/10 rounded-full blur-3xl"></div>
          <div className="absolute bottom-0 left-0 -ml-16 -mb-16 w-64 h-64 bg-accent-600/10 rounded-full blur-3xl"></div>
          
          <div className="relative z-10">
            <div className="flex items-center gap-4 mb-6">
              <div className="bg-primary-500/20 p-3 rounded-2xl">
                <MessageSquare className="text-primary-400" size={32} />
              </div>
              <div>
                <h1 className="text-3xl font-bold text-white">Contáctanos</h1>
                <p className="text-gray-400 mt-1">¿Quieres reportar un problema de la app o solicitar ser administrador? ¡Escríbenos!</p>
              </div>
            </div>

            {enviado ? (
              <div className="bg-surfaceHighlight/50 border border-success/30 rounded-2xl p-8 text-center animate-scale-up">
                <CheckCircle className="text-success mx-auto mb-4" size={48} />
                <h3 className="text-xl font-bold text-white mb-2">¡Mensaje enviado con éxito!</h3>
                <p className="text-gray-400 mb-6">Hemos recibido tu solicitud. Nuestro equipo la revisará y se pondrá en contacto contigo muy pronto.</p>
                <button 
                  onClick={() => setEnviado(false)}
                  className="bg-surface/80 hover:bg-surface border border-primary-500/30 text-white font-semibold py-2 px-6 rounded-xl transition-all"
                >
                  Enviar otro mensaje
                </button>
              </div>
            ) : (
              <form onSubmit={manejarEnvio} className="space-y-6">
                <div>
                  <label className="block text-sm font-semibold text-gray-300 mb-2">Correo Electrónico</label>
                  <div className="relative">
                    <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
                    <input 
                      type="email" 
                      required
                      defaultValue={usuario?.email || ''}
                      readOnly={!!usuario?.email}
                      className="input-campo w-full pl-11 py-3"
                      placeholder="tu@correo.com"
                    />
                  </div>
                  {usuario?.email && (
                    <p className="text-xs text-primary-400 mt-1.5 ml-1">Usando el correo de tu cuenta actual.</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-300 mb-2">Motivo</label>
                  <select required className="input-campo w-full py-3 px-4">
                    <option value="">Selecciona un motivo...</option>
                    <option value="admin">Solicitar rol de Administrador</option>
                    <option value="bug">Reportar un problema técnico</option>
                    <option value="other">Otro</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-300 mb-2">
                    Documento de Acreditación (Opcional)
                  </label>
                  <input 
                    type="file" 
                    accept=".pdf,.jpg,.jpeg,.png"
                    className="w-full text-sm text-gray-400 file:mr-4 file:py-2.5 file:px-4 file:rounded-xl file:border-0 file:text-sm file:font-semibold file:bg-primary-500/20 file:text-primary-400 hover:file:bg-primary-500/30 transition-all cursor-pointer"
                  />
                  <p className="text-xs text-gray-500 mt-1.5 ml-1">Para ser Administrador, sube tu acreditación municipal (.pdf, .jpg, .png)</p>
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-300 mb-2">Mensaje</label>
                  <textarea 
                    required
                    rows={4}
                    className="input-campo w-full py-3 px-4 resize-none"
                    placeholder="Explica brevemente por qué te gustaría tener este rol o cuál es tu problema..."
                  ></textarea>
                </div>

                <button 
                  type="submit" 
                  disabled={cargando}
                  className="w-full bg-gradient-to-r from-primary-600 to-primary-500 hover:from-primary-500 hover:to-primary-400 text-white font-bold py-3.5 px-4 rounded-xl shadow-glow transition-all hover:-translate-y-0.5 flex items-center justify-center gap-2 disabled:opacity-50"
                >
                  {cargando ? (
                    <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  ) : (
                    <>
                      <Send size={18} />
                      Enviar Mensaje
                    </>
                  )}
                </button>
              </form>
            )}
          </div>
        </div>
      </main>
    </div>
  )
}

export default ContactUsPage
