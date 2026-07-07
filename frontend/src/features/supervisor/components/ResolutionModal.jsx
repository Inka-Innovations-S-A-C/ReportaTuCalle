import { useState } from 'react'
import { Upload, X } from 'lucide-react'
import Spinner from '../../../shared/ui/Spinner'
import { subirImagen } from '../../media/services/mediaService'

export default function ResolutionModal({ isOpen, onClose, onResolve, reportId }) {
  const [file, setFile] = useState(null)
  const [preview, setPreview] = useState('')
  const [isUploading, setIsUploading] = useState(false)
  const [error, setError] = useState('')

  if (!isOpen) return null

  const handleClose = () => {
    setFile(null)
    setPreview('')
    setError('')
    setIsUploading(false)
    onClose()
  }

  const handleFileChange = (e) => {
    const selected = e.target.files[0]
    if (selected) {
      setFile(selected)
      setPreview(URL.createObjectURL(selected))
      setError('')
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!file) {
      setError('Por favor selecciona una foto de la resolución')
      return
    }

    try {
      setIsUploading(true)
      setError('')
      const imageUrl = await subirImagen(file)
      await onResolve(reportId, imageUrl)
      handleClose()
    } catch (err) {
      setError('Error al subir la imagen. Intenta nuevamente.')
      setIsUploading(false)
    }
  }

  return (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 animate-fade-in">
      <div className="bg-white rounded-2xl w-full max-w-md shadow-2xl overflow-hidden flex flex-col">
        <div className="flex justify-between items-center p-5 border-b border-gray-100">
          <h2 className="text-xl font-bold text-gray-800">Resolución de Reporte</h2>
          <button onClick={handleClose} className="text-gray-400 hover:text-gray-600 transition-colors">
            <X size={24} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 flex flex-col gap-6">
          <div className="flex flex-col gap-2">
            <label className="text-sm font-semibold text-gray-700">Foto del Trabajo Terminado</label>
            <p className="text-xs text-gray-500 mb-2">Sube una imagen para confirmar que el problema ha sido solucionado. Esta será visible para los ciudadanos.</p>
            
            <label className="flex flex-col items-center justify-center w-full h-48 border-2 border-dashed border-gray-300 rounded-xl cursor-pointer hover:bg-gray-50 hover:border-azul-400 transition-colors relative overflow-hidden">
              {preview ? (
                <img src={preview} alt="Vista previa" className="absolute inset-0 w-full h-full object-cover" />
              ) : (
                <div className="flex flex-col items-center justify-center pt-5 pb-6">
                  <Upload className="w-10 h-10 mb-3 text-gray-400" />
                  <p className="mb-2 text-sm text-gray-500"><span className="font-semibold text-azul-600">Haz clic para subir</span> o arrastra y suelta</p>
                  <p className="text-xs text-gray-500">PNG, JPG (MAX. 5MB)</p>
                </div>
              )}
              <input type="file" className="hidden" accept="image/*" onChange={handleFileChange} disabled={isUploading} />
            </label>
          </div>

          {error && <p className="text-red-500 text-sm font-medium">{error}</p>}

          <div className="flex gap-3 mt-2">
            <button
              type="button"
              onClick={handleClose}
              disabled={isUploading}
              className="flex-1 py-2.5 rounded-xl text-gray-600 font-semibold hover:bg-gray-100 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={isUploading || !file}
              className="flex-1 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-600 disabled:opacity-50 disabled:bg-gray-400 text-white font-bold shadow-sm transition-all flex justify-center items-center gap-2"
            >
              {isUploading ? <Spinner className="h-5 w-5 border-white" /> : 'Confirmar Resolución'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
