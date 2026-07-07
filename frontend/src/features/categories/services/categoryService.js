import apiClient from '../../../shared/lib/apiClient'

export async function obtenerCategorias() {
  const { data: envelope } = await apiClient.get('/categories')
  return envelope.data // → CategoryResponse[]
}

export async function actualizarCategoria(id, data) {
  const { data: envelope } = await apiClient.put(`/categories/${id}`, data)
  return envelope.data
}

export async function eliminarCategoria(id) {
  await apiClient.delete(`/categories/${id}`)
}
