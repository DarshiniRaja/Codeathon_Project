import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { createJob } from '../api/services'

export function AddJobPage() {
  const navigate = useNavigate()
  const [title, setTitle] = useState('')
  const [department, setDepartment] = useState('')
  const [description, setDescription] = useState('')
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    if (!title.trim() || !department.trim()) {
      setError('Title and department are required.')
      return
    }
    setSaving(true)
    try {
      const job = await createJob({
        title: title.trim(),
        department: department.trim(),
        description: description.trim(),
      })
      navigate(`/jobs/${job.id}`)
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <section className="panel">
      <div className="panel-head">
        <h2>Add job</h2>
        <Link to="/jobs">Back to jobs</Link>
      </div>
      <form className="form-grid" onSubmit={handleSubmit}>
        <label>
          Title
          <input value={title} onChange={(e) => setTitle(e.target.value)} required />
        </label>
        <label>
          Department
          <input value={department} onChange={(e) => setDepartment(e.target.value)} required />
        </label>
        <label className="full">
          Description
          <textarea rows={4} value={description} onChange={(e) => setDescription(e.target.value)} />
        </label>
        {error ? <p className="form-error full">{error}</p> : null}
        <div className="full">
          <button type="submit" className="btn btn-primary" disabled={saving}>
            {saving ? 'Saving…' : 'Create job'}
          </button>
        </div>
      </form>
    </section>
  )
}
