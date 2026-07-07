import { useState, useEffect } from 'react'
import apiClient from '../../../shared/lib/apiClient'
import AlertaMensaje from '../../../shared/ui/AlertaMensaje'
import Spinner from '../../../shared/ui/Spinner'
import { User, ShieldAlert, CheckCircle2 } from 'lucide-react'

function TabUsuarios() {
  const [usuarios, setUsuarios] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)
  const [mensaje, setMensaje] = useState('')
  const [busqueda, setBusqueda] = useState('')
  const [emailPromover, setEmailPromover] = useState('')

  useEffect(() => {
    cargarUsuarios()
  }, [])

  async function cargarUsuarios() {
    try {
      setCargando(true)
      const { data: res } = await apiClient.get('/auth/accounts')
      setUsuarios(res.data)
    } catch (err) {
      setError('Error al cargar usuarios')
    } finally {
      setCargando(false)
    }
  }

  async function cambiarRol(id, email, nuevoRol) {
    try {
      await apiClient.put(`/auth/accounts/${id}/role`, { role: nuevoRol })
      setMensaje(`Rol de ${email} actualizado a ${nuevoRol}`)
      setTimeout(() => setMensaje(''), 4000)
      cargarUsuarios()
    } catch (err) {
      setError('Error al cambiar el rol')
      setTimeout(() => setError(null), 4000)
    }
  }

  function formatRole(role) {
    if (role === 'CITIZEN') return 'Ciudadano'
    if (role === 'SUPERVISOR') return 'Supervisor'
    if (role === 'ADMIN') return 'Administrador'
    return role
  }

  if (cargando) return <div className="py-10 text-center"><Spinner /></div>

  // Filtramos la tabla para mostrar SOLO al personal (SUPERVISOR o ADMIN)
  const personalStaff = usuarios.filter(u => u.role === 'SUPERVISOR' || u.role === 'ADMIN')
  const staffFiltrado = personalStaff.filter(u => 
    u.email.toLowerCase().includes(busqueda.toLowerCase())
  )

  const ciudadanoEncontrado = usuarios.find(u => 
    u.role === 'CITIZEN' && u.email.toLowerCase() === emailPromover.toLowerCase()
  )

  return (
    <div className="space-y-6 animate-fade-in relative">
      {error && <AlertaMensaje tipo="error" mensaje={error} flotante={true} />}
      {mensaje && <AlertaMensaje tipo="exito" mensaje={mensaje} flotante={true} />}
      
      {/* Sección para promover ciudadanos (Previene miss-clicks) */}
      <div className="bg-blue-50/50 p-4 rounded-2xl border border-blue-100 flex flex-col md:flex-row gap-4 items-end">
        <div className="flex-1 w-full">
          <label className="block text-xs font-bold text-blue-800 uppercase tracking-wider mb-2">
            Añadir Nuevo Supervisor
          </label>
          <div className="flex items-center gap-2 bg-white px-4 py-2 rounded-xl border border-blue-200 shadow-sm focus-within:border-blue-500 focus-within:ring-2 focus-within:ring-blue-200 transition-all">
            <User size={18} className="text-blue-400" />
            <input 
              type="email" 
              placeholder="Escribe el correo exacto del ciudadano a promover" 
              value={emailPromover}
              onChange={(e) => setEmailPromover(e.target.value)}
              className="flex-1 border-none focus:ring-0 text-sm text-gray-900 placeholder:text-gray-400 py-1 bg-transparent"
            />
          </div>
        </div>
        <div className="w-full md:w-auto">
          {ciudadanoEncontrado ? (
            <button 
              onClick={() => {
                cambiarRol(ciudadanoEncontrado.id, ciudadanoEncontrado.email, 'SUPERVISOR')
                setEmailPromover('')
              }}
              className="w-full md:w-auto bg-blue-600 hover:bg-blue-700 text-white font-bold py-2.5 px-6 rounded-xl shadow-sm transition-colors flex items-center justify-center gap-2"
            >
              <ShieldAlert size={16} />
              Ascender a Supervisor
            </button>
          ) : (
            <button 
              disabled
              className="w-full md:w-auto bg-gray-200 text-gray-400 font-bold py-2.5 px-6 rounded-xl cursor-not-allowed flex items-center justify-center gap-2"
            >
              <ShieldAlert size={16} />
              Ascender a Supervisor
            </button>
          )}
        </div>
      </div>
      {emailPromover && !ciudadanoEncontrado && (
        <p className="text-xs text-gray-500 -mt-4 px-2">No se encontró ningún ciudadano con ese correo exacto.</p>
      )}

      {/* Tabla de Personal (Staff) */}
      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="p-4 border-b border-gray-100 bg-gray-50/50 flex flex-col sm:flex-row justify-between items-center gap-4">
          <h3 className="font-bold text-gray-700">Equipo de Trabajo (Staff)</h3>
          <div className="flex items-center gap-2 bg-white px-3 py-1.5 rounded-lg border border-gray-200 w-full sm:w-64 focus-within:border-gray-400 transition-colors">
            <User size={14} className="text-gray-400" />
            <input
  	      type="text"
 	      placeholder="Buscar personal"
  	      value={busqueda}
  	      onChange={(e) => setBusqueda(e.target.value)}
 	      className="flex-1 border-none focus:ring-0 text-xs text-gray-900 placeholder:text-gray-400 py-0.5 bg-transparent"
/>
          </div>
        </div>
        
        <table className="w-full text-left text-sm text-gray-600">
          <thead>
            <tr className="bg-white border-b border-gray-100 uppercase text-[10px] tracking-wider text-gray-400">
              <th className="px-5 py-3 font-semibold">Usuario</th>
              <th className="px-5 py-3 font-semibold">Rol Actual</th>
              <th className="px-5 py-3 font-semibold text-right">Acciones</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-50">
            {staffFiltrado.length === 0 ? (
              <tr>
                <td colSpan="3" className="px-5 py-8 text-center text-gray-400 text-sm">
                  No se encontró personal activo.
                </td>
              </tr>
            ) : (
              staffFiltrado.map(u => (
                <tr key={u.id} className="hover:bg-gray-50/30 transition-colors">
                <td className="px-5 py-3">
                  <div className="flex items-center gap-3">
                    <div className={`p-2 rounded-lg ${u.role === 'ADMIN' ? 'bg-red-50 text-red-500' : 'bg-purple-50 text-purple-500'}`}>
                      <User size={16} />
                    </div>
                    <span className="font-medium text-gray-700">{u.email}</span>
                  </div>
                </td>
                <td className="px-5 py-3">
                  <span className={`px-2.5 py-1 rounded-md text-xs font-bold ${
                    u.role === 'ADMIN' ? 'bg-red-100 text-red-700' : 'bg-purple-100 text-purple-700'
                  }`}>
                    {formatRole(u.role)}
                  </span>
                </td>
                <td className="px-5 py-3 text-right">
                  {u.role === 'SUPERVISOR' && (
                    <button 
                      onClick={() => cambiarRol(u.id, u.email, 'CITIZEN')}
                      className="text-xs bg-white border border-gray-200 text-gray-600 hover:border-red-200 hover:bg-red-50 hover:text-red-600 px-3 py-1.5 rounded-lg font-bold transition-all"
                    >
                      Revocar Permisos
                    </button>
                  )}
                  {u.role === 'ADMIN' && (
                    <span className="text-xs text-gray-400 italic">Protegido</span>
                  )}
                </td>
              </tr>
            )))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default TabUsuarios
