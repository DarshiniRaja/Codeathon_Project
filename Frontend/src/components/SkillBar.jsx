import { PROFICIENCY } from '../api/config'

export function SkillBar({ level, max = 5 }) {
  const n = Number(level) || 0
  return (
    <div className="skill-bar" title={`${n} · ${PROFICIENCY[n] || 'None'}`}>
      {Array.from({ length: max }, (_, i) => (
        <span key={i} className={`skill-pip${i < n ? ' is-on' : ''}`} />
      ))}
      <span className="muted">{n ? `${n} ${PROFICIENCY[n]}` : 'None'}</span>
    </div>
  )
}

export function ProficiencySelect({ id, value, onChange, name = 'proficiency' }) {
  return (
    <select id={id} name={name} value={value} onChange={onChange} required>
      <option value="">Select level</option>
      {Object.entries(PROFICIENCY).map(([level, label]) => (
        <option key={level} value={level}>
          {level} · {label}
        </option>
      ))}
    </select>
  )
}
