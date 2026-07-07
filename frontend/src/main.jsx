import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import 'leaflet/dist/leaflet.css'
import 'leaflet.markercluster/dist/MarkerCluster.css'
import 'leaflet.markercluster/dist/MarkerCluster.Default.css'
import App from './App.jsx'
import AppProviders from './app/providers.jsx'
import './index.css'

class ErrorBoundary extends React.Component {
  constructor(props) { super(props); this.state = { error: null, componentStack: null } }
  static getDerivedStateFromError(error) { return { error } }
  componentDidCatch(error, info) { this.setState({ componentStack: info.componentStack }) }
  render() {
    if (this.state.error) {
      return (
        <div style={{ padding: 32, fontFamily: 'monospace', background: '#fee2e2', minHeight: '100vh' }}>
          <h1 style={{ color: '#dc2626', marginBottom: 16 }}>Error en la aplicación</h1>
          <pre style={{ whiteSpace: 'pre-wrap', color: '#7f1d1d' }}>{this.state.error.toString()}</pre>
          <h2 style={{ color: '#dc2626', margin: '16px 0 8px' }}>Component Stack:</h2>
          <pre style={{ whiteSpace: 'pre-wrap', color: '#991b1b', fontSize: 13 }}>{this.state.componentStack}</pre>
        </div>
      )
    }
    return this.props.children
  }
}

ReactDOM.createRoot(document.getElementById('root')).render(
  <ErrorBoundary>
    <BrowserRouter>
      <AppProviders>
        <App />
      </AppProviders>
    </BrowserRouter>
  </ErrorBoundary>,
)
