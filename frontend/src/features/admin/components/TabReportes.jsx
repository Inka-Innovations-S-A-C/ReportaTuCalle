import { useState, useEffect } from 'react'
import apiClient from '../../../shared/lib/apiClient'
import AlertaMensaje from '../../../shared/ui/AlertaMensaje'
import Spinner from '../../../shared/ui/Spinner'
import EstadoBadge from '../../../shared/ui/EstadoBadge'
import { User, MapPin } from 'lucide-react'

function TabReportes() {
  const [reportes, setReportes] = useState([])
  const [supervisores, setSupervisores] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)
  const [mensaje, setMensaje] = useState('')
  const [filtroSupervisor, setFiltroSupervisor] = useState('')

  useEffect(() => {
    cargarDatos()
  }, [])

  async function cargarDatos() {
    try {
      setCargando(true)
      const [resReportes, resAccounts, resUsers] = await Promise.all([
        apiClient.get('/reports'),
        apiClient.get('/auth/accounts'),
        apiClient.get('/users')
      ])
      
      const reportsSorted = resReportes.data.data.sort((a,b) => b.id - a.id)
      setReportes(reportsSorted)

      const supersAccounts = resAccounts.data.data.filter(u => u.role === 'SUPERVISOR')
      const supersAccountIds = supersAccounts.map(a => a.id)
      
      const supersProfiles = resUsers.data.data.filter(u => supersAccountIds.includes(u.accountId))
      
      const supersFinal = supersProfiles.map(p => {
        const acc = supersAccounts.find(a => a.id === p.accountId)
        return { id: p.id, email: acc.email, fullName: p.fullName }
      })

      setSupervisores(supersFinal)

    } catch (err) {
      setError('Error al cargar datos')
    } finally {
      setCargando(false)
    }
  }

  async function asignarReporte(reportId, supervisorId) {
    try {
      const assignedToUserId = supervisorId ? parseInt(supervisorId) : null
      await apiClient.put(`/reports/${reportId}/assign`, { assignedToUserId })
      
      setMensaje(assignedToUserId ? 'Reporte asignado exitosamente' : 'Asignación removida')
      setTimeout(() => setMensaje(''), 4000)
      
      // Update state directly to avoid spinner flash
      setReportes(prev => prev.map(r => 
        r.id === reportId ? { 
          ...r, 
          assignedToUserId, 
          status: assignedToUserId ? 'ASSIGNED' : 'PENDING' 
        } : r
      ))
    } catch (err) {
      setError('Error al actualizar asignación')
      setTimeout(() => setError(null), 4000)
    }
  }

  if (cargando) return <div className="py-10 text-center"><Spinner /></div>

  const reportesFiltrados = filtroSupervisor
    ? (filtroSupervisor === 'unassigned' 
        ? reportes.filter(r => !r.assignedToUserId)
        : reportes.filter(r => r.assignedToUserId === parseInt(filtroSupervisor)))
    : reportes

  return (
    <div className="space-y-4 animate-fade-in relative">
      {error && <AlertaMensaje tipo="error" mensaje={error} flotante={true} />}
      {mensaje && <AlertaMensaje tipo="exito" mensaje={mensaje} flotante={true} />}
      
      {/* Filtro por Supervisor */}
      <div className="bg-white p-3 rounded-xl border border-gray-100 shadow-sm flex items-center justify-between">
        <label className="text-sm font-semibold text-gray-600 flex items-center gap-2">
          <User size={16} className="text-gray-400" />
          Filtrar reportes por Supervisor:
        </label>
        <select 
          className="text-xs text-gray-800 bg-white border border-gray-300 rounded-lg px-2 py-1.5 focus:outline-none focus:ring-2 focus:ring-verde-500 focus:border-verde-500 disabled:bg-gray-100 disabled:text-gray-400"
          value={filtroSupervisor}
          onChange={(e) => setFiltroSupervisor(e.target.value)}
        >
          <option value="">Todos los reportes</option>
          <option value="unassigned">Sin asignar</option>
          {supervisores.map(sup => (
            <option key={sup.id} value={sup.id}>
              {sup.email} ({sup.fullName})
            </option>
          ))}
        </select>
      </div>

      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <table className="w-full text-left text-sm">
          <thead className="bg-gray-50 border-b border-gray-100 text-gray-500">
            <tr>
              <th className="px-5 py-3 font-medium w-12">ID</th>
              <th className="px-5 py-3 font-medium">Reporte</th>
              <th className="px-5 py-3 font-medium">Estado</th>
              <th className="px-5 py-3 font-medium">Asignado a</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-50">
            {reportesFiltrados.length === 0 ? (
              <tr>
                <td colSpan="4" className="px-5 py-8 text-center text-gray-400 text-sm">
                  No hay reportes para este supervisor.
                </td>
              </tr>
            ) : (
            reportesFiltrados.map(r => (
              <tr key={r.id} className="hover:bg-gray-50/50">
                <td className="px-5 py-3 text-gray-400">#{r.id}</td>
                <td className="px-5 py-3">
                  <div className="font-medium text-gray-800">{r.title}</div>
                  <div className="text-xs text-gray-500 flex items-center gap-1 mt-0.5">
                    <MapPin size={12} /> {r.latitude.toFixed(4)}, {r.longitude.toFixed(4)}
                  </div>
                </td>
                <td className="px-5 py-3">
                  <EstadoBadge estado={r.status} />
                </td>
                <td className="px-5 py-3">
                  <div className="flex items-center gap-2">
                    <select 
                      className="text-xs text-gray-900 bg-white border border-gray-200 rounded-lg p-1.5 focus:ring-2 focus:ring-verde-500 focus:outline-none disabled:bg-gray-50 disabled:text-gray-500"
                      value={r.assignedToUserId || ''}
                      onChange={(e) => asignarReporte(r.id, e.target.value)}
                      disabled={r.status === 'RESOLVED' || r.status === 'REJECTED'}
                    >
                      <option value="">Sin asignar</option>
                      {supervisores.map(sup => (
                        <option key={sup.id} value={sup.id}>
                          {sup.email} ({sup.fullName})
                        </option>
                      ))}
                    </select>
                  </div>
                </td>
              </tr>
            )))}
          </tbody>
        </table>
        {reportes.length === 0 && (
          <div className="py-10 text-center text-gray-400 text-sm">No hay reportes todavía</div>
        )}
      </div>
    </div>
  )
}

export default TabReportes
