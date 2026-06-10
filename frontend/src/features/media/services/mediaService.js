import apiClient from '../../../shared/lib/apiClient'

// La ruta del backend tiene el path duplicado por diseño del MediaController:
// @RequestMapping("/api/v1/media") + @PostMapping("/api/v1/upload")
export async function subirImagen(archivo) {
  const formData = new FormData()
  formData.append('file', archivo)

  const { data: envelope } = await apiClient.post('/media/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return envelope.data.url // → string con la URL pública de la imagen
}
