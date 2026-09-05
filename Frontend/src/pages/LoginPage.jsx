import { useState } from 'react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

export function LoginPage() {
  const { user, login, loading, error } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [role, setRole] = useState('admin')
  const [email, setEmail] = useState('admin@skillgap.local')
  const [password, setPassword] = useState('admin123')

  if (user) {
    return <Navigate to={location.state?.from || '/'} replace />
  }

  function switchRole(next) {
    setRole(next)
    if (next === 'admin') {
      setEmail('admin@skillgap.local')
      setPassword('admin123')
    } else {
      setEmail('aisha@student.local')
      setPassword('student123')
    }
  }

  async function handleSubmit(e) {
    e.preventDefault()
    try {
      await login(role, email, password)
      navigate('/', { replace: true })
    } catch {
      /* error shown from context */
    }
  }

  return (
    <div className="login-screen">
      <div className="login-brand">
        <div>
          <p className="eyebrow">Employee / Student</p>
          <h1>Skill Gap Analyzer</h1>
          <p className="login-lead">
            Measure skill match against job requirements, close gaps, and track applications from one workspace.
          </p>
        </div>
      </div>
      <div className="login-panel">
        <form className="login-card" onSubmit={handleSubmit} noValidate>
          <h2>Sign in</h2>
          <p className="muted">Choose a workspace role to continue.</p>
          <div className="role-toggle">
            <button type="button" className={`role-btn${role === 'admin' ? ' is-active' : ''}`} onClick={() => switchRole('admin')}>
              Admin
            </button>
            <button type="button" className={`role-btn${role === 'student' ? ' is-active' : ''}`} onClick={() => switchRole('student')}>
              Student
            </button>
          </div>
          <label>
            Email
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
          </label>
          <label>
            Password
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
          </label>
          {error ? <p className="form-error">{error}</p> : null}
          <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
            {loading ? 'Signing in…' : `Sign in as ${role === 'admin' ? 'Admin' : 'Student'}`}
          </button>
          <p className="demo-hint">
            Demo admin: admin@skillgap.local / admin123
            <br />
            Demo student: aisha@student.local / student123
          </p>
        </form>
      </div>
    </div>
  )
}
