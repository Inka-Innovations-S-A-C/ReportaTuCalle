import apiClient from '../../../shared/lib/apiClient'

export async function obtenerMiPerfil() {
  const { data: envelope } = await apiClient.get('/users/me')
  return envelope.data // → UserProfileResponse
}

export async function actualizarMiPerfil(datos) {
  const { data: envelope } = await apiClient.put('/users/me', datos)
  return envelope.data // → UserProfileResponse
}
