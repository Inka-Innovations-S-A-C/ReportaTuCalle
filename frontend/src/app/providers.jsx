import { AuthProvider } from '../context/AuthContext'

// Composición de todos los providers globales de la app.
// Agregar aquí futuros providers (ThemeProvider, QueryClientProvider, etc.)
// sin tocar main.jsx.
function AppProviders({ children }) {
  return <AuthProvider>{children}</AuthProvider>
}

export default AppProviders
