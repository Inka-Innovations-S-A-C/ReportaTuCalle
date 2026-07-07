import { useState, useEffect } from 'react'
import Modal from '../../../shared/ui/Modal'
import apiClient from '../../../shared/lib/apiClient'
import { useCategorias } from '../../categories/hooks/useCategorias'
import SelectorCategoria from './SelectorCategoria'
import CampoInput from '../../../shared/ui/CampoInput'
import CampoTextarea from '../../../shared/ui/CampoTextarea'
import Boton from '../../../shared/ui/Boton'
import AlertaMensaje from '../../../shared/ui/AlertaMensaje'
import SubidaImagen from '../../media/components/SubidaImagen'

function ModalEditarReporte({ abierto, onCerrar, reporte, onExito }) {
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState('')
  const { categorias, cargando: cargandoCats } = useCategorias()
  const [errores, setErrores] = useState({})
  
  const [formulario, setFormulario] = useState({
    categoryId: '',
    title: '',
    description: '',
    imageUrl: ''
  })

  useEffect(() => {
    if (reporte) {
      setFormulario({
        categoryId: reporte.categoryId,
        title: reporte.title,
        description: reporte.description,
        imageUrl: reporte.imageUrl || ''
      })
      setError('')
      setErrores({})
    }
  }, [reporte])

  const manejarCambio = (e) => {
    const { name, value } = e.target
    setFormulario(prev => ({ ...prev, [name]: value }))
    setErrores((prev) => (prev[name] ? { ...prev, [name]: '' } : prev))
  }

  const setCampo = (name, value) => {
    setFormulario((prev) => ({ ...prev, [name]: value }))
    setErrores((prev) => (prev[name] ? { ...prev, [name]: '' } : prev))
  }

  const validar = () => {
    const nuevos = {}
    if (!formulario.categoryId) nuevos.categoryId = 'Selecciona un tipo de incidente'
    if (!formulario.title.trim()) nuevos.title = 'El título es obligatorio'
    else if (formulario.title.length > 150) nuevos.title = 'Máximo 150 caracteres'
    if (!formulario.description.trim()) nuevos.description = 'La descripción es obligatoria'
    setErrores(nuevos)
    return Object.keys(nuevos).length === 0
  }

  const manejarEnvio = async (e) => {
    e.preventDefault()
    if (!validar()) return
    
    try {
      setCargando(true)
      setError('')
      await apiClient.put(`/reports/${reporte.id}`, {
        categoryId: Number(formulario.categoryId),
        title: formulario.title.trim(),
        description: formulario.description.trim(),
        imageUrl: formulario.imageUrl || undefined
      })
      onExito?.()
      onCerrar()
    } catch (err) {
      setError('Error al actualizar el reporte')
    } finally {
      setCargando(false)
    }
  }

  return (
    <Modal abierto={abierto} onCerrar={onCerrar} titulo="Editar reporte ciudadano">
      <form onSubmit={manejarEnvio} className="space-y-5" noValidate>
        {/* Selector de categoría */}
        <SelectorCategoria
          categorias={categorias}
          cargando={cargandoCats}
          valor={formulario.categoryId}
          onChange={(id) => setCampo('categoryId', id)}
          error={errores.categoryId}
        />

        {/* Título */}
        <CampoInput
          etiqueta="Título *"
          name="title"
          type="text"
          placeholder="Ej: Bache profundo en Jr. Lima 340"
          value={formulario.title}
          onChange={manejarCambio}
          error={errores.title}
          maxLength={150}
        />

        {/* Descripción */}
        <CampoTextarea
          etiqueta="Descripción *"
          name="description"
          placeholder="Describe el problema con detalle: tamaño, peligrosidad, tiempo que lleva así..."
          value={formulario.description}
          onChange={manejarCambio}
          error={errores.description}
          filas={3}
        />

        {/* Foto */}
        <SubidaImagen onImagenSubida={(url) => setCampo('imageUrl', url)} />

        {/* Error global */}
        {error && (
          <AlertaMensaje mensaje={error} tipo="error" onCerrar={() => setError('')} />
        )}

        {/* Botones */}
        <div className="flex gap-3 pt-1">
          <Boton type="button" variante="secundario" onClick={onCerrar} className="flex-1">
            Cancelar
          </Boton>
          <Boton type="submit" variante="primario" cargando={cargando} className="flex-1">
            {cargando ? 'Guardando...' : 'Guardar cambios'}
          </Boton>
        </div>
      </form>
    </Modal>
  )
}

export default ModalEditarReporte
