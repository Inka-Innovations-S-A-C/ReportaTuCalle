import Modal from '../../../shared/ui/Modal'
import FormularioReporte from './FormularioReporte'

function ModalCrearReporte({ abierto, onCerrar, posicion, onExito }) {
  const manejarExito = (response) => {
    onExito?.(response)
    onCerrar()
  }

  return (
    <Modal abierto={abierto} onCerrar={onCerrar} titulo="Nuevo reporte ciudadano">
      <FormularioReporte
        posicion={posicion}
        onExito={manejarExito}
        onCancelar={onCerrar}
      />
    </Modal>
  )
}

export default ModalCrearReporte
