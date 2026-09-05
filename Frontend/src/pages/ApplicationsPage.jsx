import { useCallback, useEffect, useState } from 'react'
import { createApplication, getApplications, getJobs, getStudents } from '../api/services'
import { DataTable } from '../components/DataTable'
import { ErrorState, LoadingState } from '../components/Status'
import { useAuth } from '../auth/AuthContext'

export function ApplicationsPage() {
  const { user, isAdmin } = useAuth()
  const [rows, setRows] = useState([])
  const [students, setStudents] = useState([])
  const [jobs, setJobs] = useState([])
  const [studentId, setStudentId] = useState(user?.studentId ? String(user.studentId) : '')
  const [jobId, setJobId] = useState('')
  const [error, setError] = useState('')
  const [formError, setFormError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  const refresh = useCallback(async () => {
    const [apps, jobRows, studentRows] = await Promise.all([
      getApplications(),
      getJobs(),
      isAdmin ? getStudents() : Promise.resolve([]),
    ])
    setJobs(jobRows)
    setStudents(studentRows)
    setRows(isAdmin ? apps : apps.filter((a) => a.studentId === user.studentId))
  }, [isAdmin, user?.studentId])

  useEffect(() => {
    let cancelled = false
    refresh()
      .catch((err) => {
        if (!cancelled) setError(err.message)
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [refresh])

  async function handleSubmit(e) {
    e.preventDefault()
    setFormError('')
    setSuccess('')
    if (!studentId || !jobId) {
      setFormError('Student and job are required.')
      return
    }
    setSaving(true)
    try {
      await createApplication({ studentId: Number(studentId), jobId: Number(jobId) })
      setSuccess('Application submitted.')
      setJobId('')
      await refresh()
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <LoadingState label="Loading applications…" />
  if (error) return <ErrorState message={error} />

  return (
    <>
      <section className="panel">
        <div className="panel-head">
          <h2>Create application</h2>
        </div>
        <form className="form-grid" onSubmit={handleSubmit}>
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
          {formError ? <p className="form-error full">{formError}</p> : null}
          {success ? <p className="full" style={{ color: 'var(--ok)' }}>{success}</p> : null}
          <div className="full">
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Submitting…' : 'Submit application'}
            </button>
          </div>
        </form>
      </section>
      <section className="panel">
        <div className="panel-head">
          <h2>Applications</h2>
        </div>
        <DataTable
          emptyTitle="No applications yet"
          rows={rows}
          columns={[
            { key: 'studentName', header: 'Student' },
            { key: 'jobTitle', header: 'Job' },
            { key: 'status', header: 'Status' },
            {
              key: 'createdAt',
              header: 'Submitted',
              render: (row) => new Date(row.createdAt).toLocaleString(),
            },
          ]}
        />
      </section>
    </>
  )
}
