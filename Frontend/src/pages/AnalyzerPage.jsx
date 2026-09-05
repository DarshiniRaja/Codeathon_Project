import { useEffect, useState } from 'react'
import { getJobs, getSkillGap, getStudents } from '../api/services'
import { DataTable } from '../components/DataTable'
import { SkillBar } from '../components/SkillBar'
import { ErrorState, LoadingState } from '../components/Status'
import { useAuth } from '../auth/AuthContext'

function statusClass(status) {
  const s = String(status || '').toLowerCase()
  if (s === 'matched') return 'badge-matched'
  if (s === 'missing') return 'badge-missing'
  return 'badge-gap'
}

export function AnalyzerPage() {
  const { user, isAdmin } = useAuth()
  const [students, setStudents] = useState([])
  const [jobs, setJobs] = useState([])
  const [studentId, setStudentId] = useState(user?.studentId ? String(user.studentId) : '')
  const [jobId, setJobId] = useState('')
  const [result, setResult] = useState(null)
  const [error, setError] = useState('')
  const [loadError, setLoadError] = useState('')
  const [loading, setLoading] = useState(true)
  const [analyzing, setAnalyzing] = useState(false)

  useEffect(() => {
    let cancelled = false
    Promise.all([isAdmin ? getStudents() : Promise.resolve([]), getJobs()])
      .then(([studentRows, jobRows]) => {
        if (cancelled) return
        setStudents(studentRows)
        setJobs(jobRows)
      })
      .catch((err) => {
        if (!cancelled) setLoadError(err.message)
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [isAdmin])

  async function handleAnalyze(e) {
    e.preventDefault()
    setError('')
    setResult(null)
    if (!studentId || !jobId) {
      setError('Select a student and a job.')
      return
    }
    setAnalyzing(true)
    try {
      const data = await getSkillGap(studentId, jobId)
      setResult(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setAnalyzing(false)
    }
  }

  if (loading) return <LoadingState label="Loading analyzer…" />
  if (loadError) return <ErrorState message={loadError} />

  return (
    <>
      <section className="panel">
        <div className="panel-head">
          <h2>Skill Gap Analyzer</h2>
        </div>
        <form className="filters" onSubmit={handleAnalyze}>
          {isAdmin ? (
            <label>
              Student
              <select value={studentId} onChange={(e) => setStudentId(e.target.value)} required>
                <option value="">Select student</option>
                {students.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.name}
                  </option>
                ))}
              </select>
            </label>
          ) : (
            <label>
              Student
              <input value={user?.name || ''} disabled />
            </label>
          )}
          <label>
            Job
            <select value={jobId} onChange={(e) => setJobId(e.target.value)} required>
              <option value="">Select job</option>
              {jobs.map((j) => (
                <option key={j.id} value={j.id}>
                  {j.title}
                </option>
              ))}
            </select>
          </label>
          <button type="submit" className="btn btn-primary" disabled={analyzing}>
            {analyzing ? 'Analyzing…' : 'Analyze'}
          </button>
        </form>
        {error ? <p className="form-error">{error}</p> : null}
      </section>

      {result ? (
        <section className="panel">
          <div className="match-hero">
            <div className="match-ring" style={{ '--p': result.overallMatchPercent }}>
              <span>{result.overallMatchPercent}%</span>
            </div>
            <div>
              <h2>Overall match</h2>
              <p className="muted">
                {result.studentName} · {result.jobTitle}
              </p>
              <p className="muted">Values come from the skill-gap API. The UI does not recalculate them.</p>
            </div>
          </div>
          <DataTable
            emptyTitle="No skill comparison returned"
            rows={result.skills}
            rowKey="skill"
            columns={[
              { key: 'skill', header: 'Skill' },
              { key: 'current', header: 'Current', render: (row) => <SkillBar level={row.currentLevel} /> },
              { key: 'required', header: 'Required', render: (row) => <SkillBar level={row.requiredLevel} /> },
              { key: 'gap', header: 'Gap' },
              {
                key: 'status',
                header: 'Status',
                render: (row) => <span className={`badge ${statusClass(row.status)}`}>{row.status}</span>,
              },
            ]}
          />
        </section>
      ) : null}
    </>
  )
}
