import { useState, useEffect } from 'react'
import { obtenerCategorias } from '../services/categoryService'

export function useCategorias() {
  const [categorias, setCategorias] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    let cancelado = false
    setCargando(true)
    obtenerCategorias()
      .then((data) => { if (!cancelado) setCategorias(data) })
      .catch(() => { if (!cancelado) setError('No se pudieron cargar las categorías') })
      .finally(() => { if (!cancelado) setCargando(false) })
    return () => { cancelado = true }
  }, [])

  return { categorias, cargando, error }
}
