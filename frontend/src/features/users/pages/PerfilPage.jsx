import { useState, useCallback } from 'react'
import { User, Mail, Phone, ShieldCheck, Calendar, Edit2, X, Check, Award } from 'lucide-react'
import Navbar from '../../../shared/ui/Navbar'
import Spinner from '../../../shared/ui/Spinner'
import AlertaMensaje from '../../../shared/ui/AlertaMensaje'
import CampoInput from '../../../shared/ui/CampoInput'
import Boton from '../../../shared/ui/Boton'
import { usePerfil } from '../hooks/usePerfil'

const ETIQUETAS_ROL = {
  CITIZEN: { label: 'Ciudadano', cls: 'bg-verde-100 text-verde-700' },
  SUPERVISOR: { label: 'Supervisor', cls: 'bg-azul-100 text-azul-700' },
  ADMIN: { label: 'Administrador', cls: 'bg-purple-100 text-purple-700' },
}

const REGEX_TELEFONO = /^(\+?\d{7,15})?$/

function PerfilPage() {
  const { perfil, cargando, error, actualizarPerfil } = usePerfil()
  const [editando, setEditando] = useState(false)
  const [formulario, setFormulario] = useState({})
  const [errores, setErrores] = useState({})
  const [guardando, setGuardando] = useState(false)
  const [mensajeExito, setMensajeExito] = useState('')
  const [mensajeError, setMensajeError] = useState('')

  const abrirEdicion = useCallback(() => {
    setFormulario({
      firstName: perfil.firstName ?? '',
      lastName: perfil.lastName ?? '',
      phone: perfil.phone ?? '',
    })
    setErrores({})
    setMensajeError('')
    setEditando(true)
  }, [perfil])

  const cancelarEdicion = useCallback(() => {
    setEditando(false)
    setErrores({})
  }, [])

  function manejarCambio(e) {
    const { name, value } = e.target
    setFormulario((prev) => ({ ...prev, [name]: value }))
    setErrores((prev) => (prev[name] ? { ...prev, [name]: '' } : prev))
  }

  function validar() {
    const nuevos = {}
    if (!formulario.firstName?.trim()) nuevos.firstName = 'El nombre es obligatorio'
    if (!formulario.lastName?.trim()) nuevos.lastName = 'El apellido es obligatorio'
    if (formulario.phone && !REGEX_TELEFONO.test(formulario.phone)) {
      nuevos.phone = 'Ingresa un número válido (7-15 dígitos, puede empezar con +)'
    }
    setErrores(nuevos)
    return Object.keys(nuevos).length === 0
  }

  async function manejarGuardar(e) {
    e.preventDefault()
    if (!validar()) return
    setGuardando(true)
    setMensajeError('')
    try {
      await actualizarPerfil({
        firstName: formulario.firstName.trim(),
        lastName: formulario.lastName.trim(),
        phone: formulario.phone.trim() || null,
      })
      setMensajeExito('Perfil actualizado correctamente')
      setEditando(false)
      setTimeout(() => setMensajeExito(''), 3500)
    } catch {
      setMensajeError('No se pudo guardar. Intenta de nuevo.')
    } finally {
      setGuardando(false)
    }
  }

  if (cargando) {
    return (
      <div className="flex flex-col h-screen bg-gray-100">
        <Navbar />
        <div className="flex flex-1 items-center justify-center">
          <Spinner />
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="flex flex-col h-screen bg-gray-100">
        <Navbar />
        <div className="flex flex-1 items-center justify-center p-4">
          <AlertaMensaje mensaje={error} tipo="error" />
        </div>
      </div>
    )
  }

  const rolConfig = ETIQUETAS_ROL[perfil?.role] ?? ETIQUETAS_ROL.CITIZEN
  const fechaRegistro = perfil?.createdAt
    ? new Date(perfil.createdAt).toLocaleDateString('es-PE', { year: 'numeric', month: 'long', day: 'numeric' })
    : '—'

  return (
    <div className="flex flex-col min-h-screen bg-gray-100">
      <Navbar />

      <main className="flex-1 max-w-lg mx-auto w-full px-4 py-6 space-y-4">
        {/* Cabecera de perfil */}
        <div className="bg-white rounded-2xl shadow-sm p-5 flex items-center gap-4">
          <div className="w-14 h-14 rounded-full bg-azul-100 flex items-center justify-center shrink-0">
            <User size={28} className="text-azul-600" />
          </div>
          <div className="flex-1 min-w-0">
            <p className="font-bold text-gray-900 text-base truncate">{perfil?.fullName ?? '—'}</p>
            <span className={`inline-block text-xs font-semibold px-2.5 py-0.5 rounded-full mt-1 ${rolConfig.cls}`}>
              {rolConfig.label}
            </span>
          </div>
          {!editando && (
            <button
              onClick={abrirEdicion}
              className="shrink-0 p-2 rounded-xl text-gray-400 hover:text-azul-600 hover:bg-azul-50 transition-colors"
              aria-label="Editar perfil"
            >
              <Edit2 size={18} />
            </button>
          )}
        </div>

        {/* Mensaje de éxito */}
        {mensajeExito && <AlertaMensaje mensaje={mensajeExito} tipo="exito" />}

        {/* Datos de solo lectura (cuando no está editando) */}
        {!editando && (
          <div className="bg-white rounded-2xl shadow-sm divide-y divide-gray-100">
            <InfoFila icono={Mail} etiqueta="Correo" valor={perfil?.email ?? '—'} />
            <InfoFila icono={Phone} etiqueta="Teléfono" valor={perfil?.phone || 'No registrado'} />
            <InfoFila icono={ShieldCheck} etiqueta="Rol" valor={rolConfig.label} />
            {perfil?.role === 'CITIZEN' && (
              <InfoFila icono={Award} etiqueta="Puntos Cívicos" valor={`${perfil?.civicScore ?? 0} pts`} />
            )}
            <InfoFila icono={Calendar} etiqueta="Miembro desde" valor={fechaRegistro} />
          </div>
        )}

        {/* Formulario de edición */}
        {editando && (
          <form onSubmit={manejarGuardar} className="bg-white rounded-2xl shadow-sm p-5 space-y-4" noValidate>
            <p className="text-sm font-semibold text-gray-700">Editar información</p>

            <CampoInput
              etiqueta="Nombre *"
              name="firstName"
              value={formulario.firstName}
              onChange={manejarCambio}
              error={errores.firstName}
              maxLength={80}
            />
            <CampoInput
              etiqueta="Apellido *"
              name="lastName"
              value={formulario.lastName}
              onChange={manejarCambio}
              error={errores.lastName}
              maxLength={80}
            />
            <CampoInput
              etiqueta="Teléfono (opcional)"
              name="phone"
              type="tel"
              placeholder="+51 999 999 999"
              value={formulario.phone}
              onChange={manejarCambio}
              error={errores.phone}
              maxLength={20}
            />

            {mensajeError && <AlertaMensaje mensaje={mensajeError} tipo="error" onCerrar={() => setMensajeError('')} />}

            <div className="flex gap-3 pt-1">
              <Boton type="button" variante="secundario" onClick={cancelarEdicion} className="flex-1" icono={X}>
                Cancelar
              </Boton>
              <Boton type="submit" variante="primario" cargando={guardando} className="flex-1" icono={guardando ? undefined : Check}>
                {guardando ? 'Guardando...' : 'Guardar'}
              </Boton>
            </div>
          </form>
        )}
      </main>
    </div>
  )
}

function InfoFila({ icono: Icono, etiqueta, valor }) {
  return (
    <div className="flex items-center gap-3 px-5 py-3.5">
      <Icono size={16} className="text-gray-400 shrink-0" />
      <span className="text-xs text-gray-500 w-28 shrink-0">{etiqueta}</span>
      <span className="text-sm text-gray-800 font-medium truncate">{valor}</span>
    </div>
  )
}

export default PerfilPage
