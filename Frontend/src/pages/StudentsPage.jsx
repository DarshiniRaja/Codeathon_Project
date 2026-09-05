import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getStudents } from '../api/services'
import { DataTable } from '../components/DataTable'
import { ErrorState, LoadingState } from '../components/Status'

export function StudentsPage() {
  const [rows, setRows] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancelled = false
    getStudents()
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

  if (loading) return <LoadingState label="Loading students…" />
  if (error) return <ErrorState message={error} />

  return (
    <section className="panel">
      <div className="panel-head">
        <h2>Students / Employees</h2>
        <Link className="btn btn-primary" to="/students/new">
          Add student
        </Link>
      </div>
      <DataTable
        emptyTitle="No students yet"
        rows={rows}
        columns={[
          { key: 'name', header: 'Name' },
          { key: 'email', header: 'Email' },
          { key: 'skillCount', header: 'Skills' },
          {
            key: 'actions',
            header: 'Actions',
            render: (row) => (
              <div className="row-actions">
                <Link className="btn btn-sm" to={`/students/${row.id}`}>
                  Profile
                </Link>
                <Link className="btn btn-sm" to={`/students/${row.id}/skills`}>
                  Manage skills
                </Link>
              </div>
            ),
          },
        ]}
      />
    </section>
  )
}
