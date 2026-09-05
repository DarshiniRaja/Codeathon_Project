import { useEffect, useState } from 'react'
import { getJobs, getRecommendations, getStudents } from '../api/services'
import { DataTable } from '../components/DataTable'
import { SkillBar } from '../components/SkillBar'
import { ErrorState, LoadingState } from '../components/Status'
import { useAuth } from '../auth/AuthContext'

function priorityClass(priority) {
  const value = String(priority || '').toLowerCase()
  if (value === 'high') return 'badge-high'
  if (value === 'medium') return 'badge-medium'
  return 'badge-low'
}

export function RecommendationsPage() {
  const { user, isAdmin } = useAuth()
  const [students, setStudents] = useState([])
  const [jobs, setJobs] = useState([])
  const [studentId, setStudentId] = useState(user?.studentId ? String(user.studentId) : '')
  const [jobId, setJobId] = useState('')
  const [result, setResult] = useState(null)
  const [error, setError] = useState('')
  const [loadError, setLoadError] = useState('')
  const [loading, setLoading] = useState(true)
  const [fetching, setFetching] = useState(false)

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

  async function handleLoad(e) {
    e.preventDefault()
    setError('')
    setResult(null)
    if (!studentId || !jobId) {
      setError('Select a student and a job.')
      return
    }
    setFetching(true)
    try {
      const data = await getRecommendations(studentId, jobId)
      setResult(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setFetching(false)
    }
  }

  if (loading) return <LoadingState label="Loading recommendations…" />
  if (loadError) return <ErrorState message={loadError} />

  return (
    <>
      <section className="panel">
        <div className="panel-head">
          <h2>Recommendations</h2>
        </div>
        <form className="filters" onSubmit={handleLoad}>
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
          <button type="submit" className="btn btn-primary" disabled={fetching}>
            {fetching ? 'Loading…' : 'Load recommendations'}
          </button>
        </form>
        {error ? <p className="form-error">{error}</p> : null}
      </section>

      {result ? (
        <section className="panel">
          <div className="panel-head">
            <h2>
              {result.studentName} · {result.jobTitle}
            </h2>
          </div>
          <DataTable
            emptyTitle="No recommendations — skills already match this job"
            rows={result.items}
            rowKey="skill"
            columns={[
              {
                key: 'priority',
                header: 'Priority',
                render: (row) => <span className={`badge ${priorityClass(row.priority)}`}>{row.priority}</span>,
              },
              { key: 'skill', header: 'Skill' },
              { key: 'current', header: 'Current', render: (row) => <SkillBar level={row.currentLevel} /> },
              { key: 'target', header: 'Target', render: (row) => <SkillBar level={row.targetLevel} /> },
              { key: 'reason', header: 'Reason' },
            ]}
          />
        </section>
      ) : null}
    </>
  )
}
