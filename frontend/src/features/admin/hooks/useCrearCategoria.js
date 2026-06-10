import { useState, useCallback } from 'react'
import { crearCategoria } from '../services/adminCategoryService'

const ESTADO_INICIAL = {
  name: '',
  description: '',
  markerColor: '#ef4444',
  algorithmType: 'NONE',
}

export function useCrearCategoria({ onExito } = {}) {
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
  }, [])

  const limpiarError = useCallback(() => setMensajeError(''), [])

  function validar() {
    const nuevos = {}
    if (!formulario.name.trim()) nuevos.name = 'El nombre es obligatorio'
    else if (formulario.name.trim().length < 3) nuevos.name = 'Mínimo 3 caracteres'
    if (!formulario.description.trim()) nuevos.description = 'La descripción es obligatoria'
    if (!formulario.markerColor) nuevos.markerColor = 'Elige un color'
    setErrores(nuevos)
    return Object.keys(nuevos).length === 0
  }

  async function manejarEnvio(e) {
    e.preventDefault()
    setMensajeError('')
    if (!validar()) return
    setCargando(true)
    try {
      const nueva = await crearCategoria({
        name: formulario.name.trim(),
        description: formulario.description.trim(),
        markerColor: formulario.markerColor,
        algorithmType: formulario.algorithmType,
      })
      setFormulario(ESTADO_INICIAL)
      onExito?.(nueva)
    } catch (err) {
      const status = err.response?.status
      if (status === 409) setMensajeError('Ya existe una categoría con ese nombre.')
      else if (status === 403) setMensajeError('No tienes permisos para crear categorías.')
      else setMensajeError('Ocurrió un error. Intenta de nuevo.')
    } finally {
      setCargando(false)
    }
  }

  return { formulario, errores, mensajeError, cargando, manejarCambio, setCampo, manejarEnvio, limpiarError }
}
