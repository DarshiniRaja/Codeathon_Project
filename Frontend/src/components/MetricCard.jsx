export function MetricCard({ label, value }) {
  return (
    <article className="metric-card">
      <p className="label">{label}</p>
      <p className="value">{value ?? '—'}</p>
    </article>
  )
}
