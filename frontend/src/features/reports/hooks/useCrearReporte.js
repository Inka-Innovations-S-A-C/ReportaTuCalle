import { useState, useCallback } from 'react'
import { crearReporte } from '../services/reportService'

const ESTADO_INICIAL = { categoryId: null, title: '', description: '', imageUrl: null }

export function useCrearReporte({ posicion, onExito }) {
  const [formulario, setFormulario] = useState(ESTADO_INICIAL)
  const [errores, setErrores] = useState({})
  const [mensajeError, setMensajeError] = useState('')
  const [cargando, setCargando] = useState(false)

  const manejarCambio = useCallback((e) => {
    const { name, value } = e.target
    setFormulario((prev) => ({ ...prev, [name]: value }))
    setErrores((prev) => (prev[name] ? { ...prev, [name]: '' } : prev))
  }, [])

  const setCampo = useCallback((name, value) => {
    setFormulario((prev) => ({ ...prev, [name]: value }))
    setErrores((prev) => (prev[name] ? { ...prev, [name]: '' } : prev))
  }, [])

  const limpiarError = useCallback(() => setMensajeError(''), [])

  const resetear = useCallback(() => {
    setFormulario(ESTADO_INICIAL)
    setErrores({})
    setMensajeError('')
  }, [])

  function validar() {
    const nuevos = {}
    if (!formulario.categoryId) nuevos.categoryId = 'Selecciona un tipo de incidente'
    if (!formulario.title.trim()) nuevos.title = 'El título es obligatorio'
    else if (formulario.title.length > 150) nuevos.title = 'Máximo 150 caracteres'
    if (!formulario.description.trim()) nuevos.description = 'La descripción es obligatoria'
    if (!posicion) nuevos.ubicacion = 'Esperando tu ubicación GPS...'
    setErrores(nuevos)
    return Object.keys(nuevos).length === 0
  }

  async function manejarEnvio(e) {
    if (e) e.preventDefault()
    setMensajeError('')
    if (!validar()) return

    setCargando(true)
    try {
      await crearReporte({
        categoryId: formulario.categoryId,
        title: formulario.title.trim(),
        description: formulario.description.trim(),
        latitude: posicion.latitude,
        longitude: posicion.longitude,
        imageUrl: formulario.imageUrl ?? undefined,
      })
      resetear()
      onExito?.()
    } catch (error) {
      const status = error.response?.status
      const msg = error.response?.data?.message
      if (status === 400) {
        setMensajeError(msg || 'Revisa los datos del formulario.')
      } else if (!error.response) {
        setMensajeError('Sin conexión con el servidor.')
      } else {
        setMensajeError('Error al enviar el reporte. Intenta de nuevo.')
      }
    } finally {
      setCargando(false)
    }
  }

  return {
    formulario,
    errores,
    mensajeError,
    cargando,
    manejarCambio,
    setCampo,
    manejarEnvio,
    limpiarError,
    resetear,
  }
}
