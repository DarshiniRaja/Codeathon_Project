import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { addJobSkill, getJob } from '../api/services'
import { DataTable } from '../components/DataTable'
import { ProficiencySelect, SkillBar } from '../components/SkillBar'
import { ErrorState, LoadingState } from '../components/Status'
import { useAuth } from '../auth/AuthContext'

export function JobDetailsPage() {
  const { id } = useParams()
  const { isAdmin } = useAuth()
  const [job, setJob] = useState(null)
  const [error, setError] = useState('')
  const [formError, setFormError] = useState('')
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [skill, setSkill] = useState('')
  const [requiredLevel, setRequiredLevel] = useState('')
  const [mandatory, setMandatory] = useState(true)

  useEffect(() => {
    let cancelled = false
    getJob(id)
      .then((data) => {
        if (!cancelled) setJob(data)
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
  }, [id])

  async function handleSubmit(e) {
    e.preventDefault()
    setFormError('')
    const level = Number(requiredLevel)
    if (!skill.trim()) {
      setFormError('Skill name is required.')
      return
    }
    if (!Number.isInteger(level) || level < 1 || level > 5) {
      setFormError('Choose a required level from 1 to 5.')
      return
    }
    setSaving(true)
    try {
      const updated = await addJobSkill(id, {
        skill: skill.trim(),
        requiredLevel: level,
        mandatory,
      })
      setJob(updated)
      setSkill('')
      setRequiredLevel('')
      setMandatory(true)
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <LoadingState label="Loading job…" />
  if (error) return <ErrorState message={error} />
  if (!job) return <ErrorState message="Job not found." />

  return (
    <>
      <section className="panel">
        <div className="panel-head">
          <div>
            <h2>{job.title}</h2>
            <p className="muted">{job.department}</p>
          </div>
          <Link to="/jobs">All jobs</Link>
        </div>
        {job.description ? <p>{job.description}</p> : null}
      </section>

      {isAdmin ? (
        <section className="panel">
          <div className="panel-head">
            <h2>Add required skill</h2>
          </div>
          <form className="form-grid" onSubmit={handleSubmit}>
            <label>
              Skill
              <input value={skill} onChange={(e) => setSkill(e.target.value)} />
            </label>
            <label>
              Required level
              <ProficiencySelect
                name="requiredLevel"
                value={requiredLevel}
                onChange={(e) => setRequiredLevel(e.target.value)}
              />
            </label>
            <label className="full" style={{ fontWeight: 500 }}>
              <input type="checkbox" checked={mandatory} onChange={(e) => setMandatory(e.target.checked)} /> Mandatory
            </label>
            {formError ? <p className="form-error full">{formError}</p> : null}
            <div className="full">
              <button type="submit" className="btn btn-primary" disabled={saving}>
                {saving ? 'Saving…' : 'Save required skill'}
              </button>
            </div>
          </form>
        </section>
      ) : null}

      <section className="panel">
        <div className="panel-head">
          <h2>Required skills</h2>
        </div>
        <DataTable
          emptyTitle="No required skills yet"
          rows={job.requiredSkills}
          rowKey="skill"
          columns={[
            { key: 'skill', header: 'Skill' },
            { key: 'level', header: 'Required level', render: (row) => <SkillBar level={row.requiredLevel} /> },
            {
              key: 'mandatory',
              header: 'Status',
              render: (row) => (
                <span className={`badge ${row.mandatory ? 'badge-required' : 'badge-optional'}`}>
                  {row.mandatory ? 'Mandatory' : 'Optional'}
                </span>
              ),
            },
          ]}
        />
      </section>
    </>
  )
}
