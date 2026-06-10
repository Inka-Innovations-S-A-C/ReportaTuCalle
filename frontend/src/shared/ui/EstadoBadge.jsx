const CONFIG = {
  PENDING:     { label: 'Pendiente',  cls: 'bg-amber-100 text-amber-700 ring-amber-200' },
  IN_PROGRESS: { label: 'En proceso', cls: 'bg-azul-100 text-azul-700 ring-azul-200' },
  RESOLVED:    { label: 'Resuelto',   cls: 'bg-verde-100 text-verde-700 ring-verde-200' },
  REJECTED:    { label: 'Rechazado',  cls: 'bg-gray-100 text-gray-500 ring-gray-200' },
}

function EstadoBadge({ estado }) {
  const { label, cls } = CONFIG[estado] ?? CONFIG.PENDING
  return (
    <span className={`inline-flex items-center text-[11px] font-semibold px-2 py-0.5 rounded-full ring-1 ${cls}`}>
      {label}
    </span>
  )
}

export default EstadoBadge
