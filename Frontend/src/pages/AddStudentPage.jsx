import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { createStudent } from '../api/services'

export function AddStudentPage() {
  const navigate = useNavigate()
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    if (!name.trim()) {
      setError('Name is required.')
      return
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) {
      setError('Enter a valid email address.')
      return
    }
    setSaving(true)
    try {
      const student = await createStudent({ name: name.trim(), email: email.trim() })
      navigate(`/students/${student.id}`)
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <section className="panel">
      <div className="panel-head">
        <h2>Add student</h2>
        <Link to="/students">Back to list</Link>
      </div>
      <form className="form-grid" onSubmit={handleSubmit}>
        <label>
          Name
          <input value={name} onChange={(e) => setName(e.target.value)} required />
        </label>
        <label>
          Email
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        {error ? <p className="form-error full">{error}</p> : null}
        <div className="full">
          <button type="submit" className="btn btn-primary" disabled={saving}>
            {saving ? 'Saving…' : 'Create student'}
          </button>
        </div>
      </form>
    </section>
  )
}
