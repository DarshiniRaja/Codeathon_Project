import { useEffect, useState } from 'react'
import { getDashboard } from '../api/services'
import { DataTable } from '../components/DataTable'
import { MetricCard } from '../components/MetricCard'
import { ErrorState, LoadingState } from '../components/Status'
import { useAuth } from '../auth/AuthContext'

export function DashboardPage() {
  const { isAdmin, user } = useAuth()
  const [data, setData] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    getDashboard()
      .then((result) => {
        if (!cancelled) setData(result)
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

  if (loading) return <LoadingState label="Loading dashboard…" />
  if (error) return <ErrorState message={error} />
  if (!data) return <ErrorState message="No dashboard data returned." />

  const matchLabel = data.averageSkillMatch == null ? '—' : `${data.averageSkillMatch}%`

  return (
    <>
      <p className="muted" style={{ marginBottom: 16 }}>
        {isAdmin ? 'Workspace overview from live records.' : `Welcome back, ${user.name}. Metrics below are workspace-wide.`}
      </p>
      <section className="grid-metrics">
        <MetricCard label="Total students" value={data.totalStudents} />
        <MetricCard label="Total jobs" value={data.totalJobs} />
        <MetricCard label="Applications" value={data.totalApplications} />
        <MetricCard label="Average skill match" value={matchLabel} />
      </section>
      <section className="panel">
        <div className="panel-head">
          <h2>Top skill gaps</h2>
        </div>
        <DataTable
          emptyTitle="No skill gaps yet"
          columns={[
            { key: 'skill', header: 'Skill' },
            { key: 'gapCount', header: 'Students with a gap' },
          ]}
          rows={data.topSkillGaps}
          rowKey="skill"
        />
      </section>
    </>
  )
}
