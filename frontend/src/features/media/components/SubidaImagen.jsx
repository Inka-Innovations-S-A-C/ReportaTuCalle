import { useState, useRef } from 'react'
import { Camera, X, Loader2 } from 'lucide-react'
import { useSubirImagen } from '../hooks/useSubirImagen'

function SubidaImagen({ onImagenSubida }) {
  const { subir, cargando, error } = useSubirImagen()
  const [preview, setPreview] = useState(null)
  const inputRef = useRef(null)

  const manejarArchivo = async (e) => {
    const archivo = e.target.files?.[0]
    if (!archivo) return

    // Preview local inmediato
    const reader = new FileReader()
    reader.onload = (ev) => setPreview(ev.target.result)
    reader.readAsDataURL(archivo)

    // Sube al backend y notifica la URL al padre
    const url = await subir(archivo)
    if (url) onImagenSubida(url)
  }

  const limpiar = () => {
    setPreview(null)
    onImagenSubida(null)
    if (inputRef.current) inputRef.current.value = ''
  }

  return (
    <div className="flex flex-col gap-1">
      <span className="text-sm font-medium text-gray-700">Foto (opcional)</span>

      {preview ? (
        <div className="relative w-full h-36 rounded-xl overflow-hidden border border-gray-200">
          <img src={preview} alt="Preview" className="w-full h-full object-cover" />
          {cargando && (
            <div className="absolute inset-0 bg-black/40 flex items-center justify-center">
              <Loader2 size={24} className="text-white animate-spin" />
            </div>
          )}
          {!cargando && (
            <button
              type="button"
              onClick={limpiar}
              className="absolute top-2 right-2 bg-black/50 text-white rounded-full p-1 hover:bg-black/70"
              aria-label="Quitar imagen"
            >
              <X size={14} />
            </button>
          )}
        </div>
      ) : (
        <label className="flex flex-col items-center justify-center gap-2 h-28 border-2 border-dashed border-gray-300 rounded-xl cursor-pointer hover:border-verde-400 hover:bg-verde-50 transition-colors">
          <Camera size={24} className="text-gray-400" aria-hidden="true" />
          <span className="text-xs text-gray-500">Toca para agregar una foto</span>
          <input
            ref={inputRef}
            type="file"
            accept="image/*"
            capture="environment"
            onChange={manejarArchivo}
            className="hidden"
          />
        </label>
      )}

      {error && <p className="text-xs text-red-600">{error}</p>}
    </div>
  )
}

export default SubidaImagen
