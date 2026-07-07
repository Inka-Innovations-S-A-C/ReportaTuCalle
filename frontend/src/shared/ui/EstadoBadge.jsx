const CONFIG = {
  PENDING:     { label: 'Pendiente',  cls: 'bg-warning/20 text-warning ring-warning/30 shadow-[0_0_10px_rgba(245,158,11,0.2)]' },
  IN_PROGRESS: { label: 'En proceso', cls: 'bg-primary-500/20 text-primary-400 ring-primary-500/30 shadow-[0_0_10px_rgba(99,102,241,0.2)]' },
  RESOLVED:    { label: 'Resuelto',   cls: 'bg-accent-500/20 text-accent-400 ring-accent-500/30 shadow-[0_0_10px_rgba(16,185,129,0.2)]' },
  REJECTED:    { label: 'Rechazado',  cls: 'bg-danger/20 text-danger ring-danger/30 shadow-[0_0_10px_rgba(239,68,68,0.2)]' },
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
