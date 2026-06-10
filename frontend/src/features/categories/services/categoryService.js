import apiClient from '../../../shared/lib/apiClient'

export async function obtenerCategorias() {
  const { data: envelope } = await apiClient.get('/categories')
  return envelope.data // → CategoryResponse[]
}
