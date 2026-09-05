export function LoadingState({ label = 'Loading…' }) {
  return (
    <div className="state" role="status">
      {label}
    </div>
  )
}

export function EmptyState({ title, detail }) {
  return (
    <div className="state">
      <strong>{title}</strong>
      {detail ? <p className="muted">{detail}</p> : null}
    </div>
  )
}

export function ErrorState({ message }) {
  return (
    <div className="state error" role="alert">
      {message || 'Something went wrong.'}
    </div>
  )
}
