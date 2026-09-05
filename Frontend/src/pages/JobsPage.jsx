import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getJobs } from '../api/services'
import { DataTable } from '../components/DataTable'
import { ErrorState, LoadingState } from '../components/Status'
import { useAuth } from '../auth/AuthContext'

export function JobsPage() {
  const { isAdmin } = useAuth()
  const [rows, setRows] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancelled = false
    getJobs()
      .then((data) => {
        if (!cancelled) setRows(data)
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
  }, [])

  if (loading) return <LoadingState label="Loading jobs…" />
  if (error) return <ErrorState message={error} />

  return (
    <section className="panel">
      <div className="panel-head">
        <h2>Jobs</h2>
        {isAdmin ? (
          <Link className="btn btn-primary" to="/jobs/new">
            Add job
          </Link>
        ) : null}
      </div>
      <DataTable
        emptyTitle="No jobs yet"
        rows={rows}
        columns={[
          { key: 'title', header: 'Title' },
          { key: 'department', header: 'Department' },
          { key: 'requiredSkillCount', header: 'Required skills' },
          {
            key: 'actions',
            header: 'Actions',
            render: (row) => (
              <Link className="btn btn-sm" to={`/jobs/${row.id}`}>
                Details
              </Link>
            ),
          },
        ]}
      />
    </section>
  )
}
