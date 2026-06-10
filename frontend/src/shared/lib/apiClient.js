import axios from 'axios'

// Callback registrado por AuthProvider para manejar sesiones expiradas.
// El interceptor nunca toca React directamente; sólo llama a esta función.
let _onUnauthorized = null

export function setUnauthorizedHandler(fn) {
  _onUnauthorized = fn
}

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: { 'Content-Type': 'application/json' },
})

// Adjunta el JWT a cada petición si existe en localStorage
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('rtk_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Detecta 401 y delega el logout al AuthProvider sin causar un page reload
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && _onUnauthorized) {
      _onUnauthorized()
    }
    return Promise.reject(error)
  }
)

export default apiClient
