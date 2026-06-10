import apiClient from '../../../shared/lib/apiClient'

export async function crearCategoria(datos) {
  const { data: envelope } = await apiClient.post('/categories', datos)
  return envelope.data // → CategoryResponse
}
