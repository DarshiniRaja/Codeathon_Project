import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { addStudentSkill, getStudent, getStudentSkills } from '../api/services'
import { DataTable } from '../components/DataTable'
import { ProficiencySelect, SkillBar } from '../components/SkillBar'
import { ErrorState, LoadingState } from '../components/Status'
import { useAuth } from '../auth/AuthContext'

export function ManageSkillsPage() {
  const { id } = useParams()
  const { user, isAdmin } = useAuth()
  const studentId = id || String(user?.studentId || '')
  const [student, setStudent] = useState(null)
  const [skills, setSkills] = useState([])
  const [skill, setSkill] = useState('')
  const [proficiency, setProficiency] = useState('')
  const [error, setError] = useState('')
  const [formError, setFormError] = useState('')
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (!studentId) {
      setError('No student profile is linked to this account.')
      setLoading(false)
      return
    }
    let cancelled = false
    Promise.all([getStudent(studentId), getStudentSkills(studentId)])
      .then(([profile, list]) => {
        if (!cancelled) {
          setStudent(profile)
          setSkills(list)
        }
      })
      .catch((err) => {
        if (!cancelled) setError(err.message)
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [studentId])

  async function handleSubmit(e) {
    e.preventDefault()
    setFormError('')
    const level = Number(proficiency)
    if (!skill.trim()) {
      setFormError('Skill name is required.')
      return
    }
    if (!Number.isInteger(level) || level < 1 || level > 5) {
      setFormError('Choose a proficiency from 1 to 5.')
      return
    }
    setSaving(true)
    try {
      const list = await addStudentSkill(studentId, { skill: skill.trim(), proficiency: level })
      setSkills(list)
      setSkill('')
      setProficiency('')
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <LoadingState label="Loading skills…" />
  if (error) return <ErrorState message={error} />

  const backTo = isAdmin ? `/students/${studentId}` : '/profile'

  return (
    <>
      <section className="panel">
        <div className="panel-head">
          <h2>Manage skills · {student?.name}</h2>
          <Link to={backTo}>Back to profile</Link>
        </div>
        <form className="form-grid" onSubmit={handleSubmit}>
          <label>
            Skill
            <input value={skill} onChange={(e) => setSkill(e.target.value)} placeholder="e.g. Python" />
          </label>
          <label>
            Proficiency
            <ProficiencySelect value={proficiency} onChange={(e) => setProficiency(e.target.value)} />
          </label>
          {formError ? <p className="form-error full">{formError}</p> : null}
          <div className="full">
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Saving…' : 'Add / update skill'}
            </button>
          </div>
        </form>
      </section>
      <section className="panel">
        <DataTable
          emptyTitle="No skills yet"
          rows={skills}
          rowKey="skill"
          columns={[
            { key: 'skill', header: 'Skill' },
            { key: 'level', header: 'Level', render: (row) => <SkillBar level={row.proficiency} /> },
          ]}
        />
      </section>
    </>
  )
}
