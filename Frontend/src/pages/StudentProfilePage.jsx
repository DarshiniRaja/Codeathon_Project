import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getStudent } from '../api/services'
import { DataTable } from '../components/DataTable'
import { SkillBar } from '../components/SkillBar'
import { ErrorState, LoadingState } from '../components/Status'
import { useAuth } from '../auth/AuthContext'

export function StudentProfilePage() {
  const { id } = useParams()
  const { user, isAdmin } = useAuth()
  const studentId = id || String(user?.studentId || '')
  const [student, setStudent] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!studentId) {
      setError('No student profile is linked to this account.')
      setLoading(false)
      return
    }
    let cancelled = false
    setLoading(true)
    getStudent(studentId)
      .then((data) => {
        if (!cancelled) setStudent(data)
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

  if (loading) return <LoadingState label="Loading profile…" />
  if (error) return <ErrorState message={error} />
  if (!student) return <ErrorState message="Student not found." />

  return (
    <>
      <section className="panel">
        <div className="panel-head">
          <div>
            <h2>{student.name}</h2>
            <p className="muted">{student.email}</p>
          </div>
          <Link className="btn btn-primary" to={isAdmin ? `/students/${student.id}/skills` : '/profile/skills'}>
            Manage skills
          </Link>
        </div>
      </section>
      <section className="panel">
        <div className="panel-head">
          <h2>Skills</h2>
        </div>
        <DataTable
          emptyTitle="No skills on this profile yet"
          rows={student.skills}
          rowKey="skill"
          columns={[
            { key: 'skill', header: 'Skill' },
            {
              key: 'proficiency',
              header: 'Proficiency (1–5)',
              render: (row) => <SkillBar level={row.proficiency} />,
            },
          ]}
        />
      </section>
    </>
  )
}
