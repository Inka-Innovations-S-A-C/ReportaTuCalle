import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL
const MODO_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

// ---------------------------------------------------------------------------
// MODO MOCK — funciona sin backend. Credenciales de prueba:
//   Login:    usuario "ciudadano"  contraseña "123456"
//             usuario "admin"      contraseña "admin123"
//   Registro: cualquier dato válido en el formulario
// ---------------------------------------------------------------------------

const USUARIOS_MOCK = [
  { id: 1, username: 'ciudadano', password: '123456',   email: 'ciudadano@rtk.com' },
  { id: 2, username: 'admin',     password: 'admin123', email: 'admin@rtk.com' },
]

function simularDelay(ms = 700) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

async function loginMock({ username, password }) {
  await simularDelay()
  const usuario = USUARIOS_MOCK.find(
    (u) => u.username === username && u.password === password
  )
  if (!usuario) {
    const error = new Error('Credenciales incorrectas')
    error.response = { status: 401 }
    throw error
  }
  const { password: _pw, ...datosSeguros } = usuario
  return { token: 'mock-jwt-token-rtk-2026', ...datosSeguros }
}

async function registroMock({ username, email, password }) {
  await simularDelay()
  const yaExiste = USUARIOS_MOCK.find((u) => u.username === username)
  if (yaExiste) {
    const error = new Error('Usuario ya existe')
    error.response = { status: 409 }
    throw error
  }
  // Simula registro exitoso (no persiste entre recargas, es solo demo)
  USUARIOS_MOCK.push({ id: Date.now(), username, email, password })
  return { mensaje: 'Usuario registrado correctamente' }
}

// ---------------------------------------------------------------------------
// CLIENTE AXIOS — usado cuando VITE_USE_MOCK=false
// ---------------------------------------------------------------------------

const apiClient = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' },
})

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('rtk_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

apiClient.interceptors.response.use(
  (respuesta) => respuesta,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('rtk_token')
      localStorage.removeItem('rtk_usuario')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// ---------------------------------------------------------------------------
// FUNCIONES EXPORTADAS — eligen mock o real según la variable de entorno
// ---------------------------------------------------------------------------

export async function registrarUsuario(datos) {
  if (MODO_MOCK) return registroMock(datos)
  const respuesta = await apiClient.post('/auth/register', datos)
  return respuesta.data
}

export async function iniciarSesion(credenciales) {
  if (MODO_MOCK) return loginMock(credenciales)
  const respuesta = await apiClient.post('/auth/login', credenciales)
  return respuesta.data
}

export default apiClient
