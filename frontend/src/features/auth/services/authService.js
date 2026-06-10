import apiClient from '../../../shared/lib/apiClient'

// ---------------------------------------------------------------------------
// MODO MOCK — activo cuando VITE_USE_MOCK=true en .env.local
// Credenciales: ciudadano/123456 · admin/admin123
// ---------------------------------------------------------------------------
const MODO_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

// Credenciales mock: ciudadano@rtk.com/123456 · admin@rtk.com/admin123
const USUARIOS_MOCK = [
  { id: 1, email: 'ciudadano@rtk.com', password: '123456',   firstName: 'Ana',   lastName: 'Ciudadana' },
  { id: 2, email: 'admin@rtk.com',     password: 'admin123', firstName: 'Admin',  lastName: 'Sistema' },
]

function delay(ms = 700) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

async function loginMock({ email, password }) {
  await delay()
  const usuario = USUARIOS_MOCK.find((u) => u.email === email && u.password === password)
  if (!usuario) {
    const err = new Error('Credenciales incorrectas')
    err.response = { status: 401 }
    throw err
  }
  return { token: 'mock-jwt-token-rtk-2026' }
}

async function registroMock({ email }) {
  await delay()
  if (USUARIOS_MOCK.find((u) => u.email === email)) {
    const err = new Error('Email ya registrado')
    err.response = { status: 409 }
    throw err
  }
  USUARIOS_MOCK.push({ id: Date.now(), email, password: '', firstName: '', lastName: '' })
  return { mensaje: 'Usuario registrado correctamente' }
}

// ---------------------------------------------------------------------------
// API REAL — apunta a VITE_API_URL (debe ser http://localhost:8080/api/v1)
// ---------------------------------------------------------------------------

// El backend envuelve las respuestas en { success, message, data: { token }, timestamp }
// Extraemos solo el payload interno para mantener la interfaz simple en los hooks.

export async function iniciarSesion(credenciales) {
  if (MODO_MOCK) return loginMock(credenciales)
  const { data: envelope } = await apiClient.post('/auth/login', credenciales)
  return envelope.data  // → { token }
}

export async function registrarUsuario(datos) {
  if (MODO_MOCK) return registroMock(datos)
  const { data: envelope } = await apiClient.post('/auth/register', datos)
  return envelope.data  // → { token }
}
