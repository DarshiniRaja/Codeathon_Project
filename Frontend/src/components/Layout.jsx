import { useState } from 'react'
import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { USE_MOCK } from '../api/config'
import { useAuth } from '../auth/AuthContext'

const ADMIN_LINKS = [
  { to: '/', label: 'Dashboard' },
  { to: '/students', label: 'Students' },
  { to: '/jobs', label: 'Jobs' },
  { to: '/analyzer', label: 'Skill Gap Analyzer' },
  { to: '/recommendations', label: 'Recommendations' },
  { to: '/applications', label: 'Applications' },
]

const STUDENT_LINKS = [
  { to: '/', label: 'Dashboard' },
  { to: '/profile', label: 'My Profile' },
  { to: '/jobs', label: 'Jobs' },
  { to: '/analyzer', label: 'Skill Gap Analyzer' },
  { to: '/recommendations', label: 'Recommendations' },
  { to: '/applications', label: 'Applications' },
]

export function Layout() {
  const { user, logout, isAdmin } = useAuth()
  const navigate = useNavigate()
  const [open, setOpen] = useState(false)
  const links = isAdmin ? ADMIN_LINKS : STUDENT_LINKS

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <div className="app-shell">
      <aside className={`sidebar${open ? ' is-open' : ''}`}>
        <div className="sidebar-brand">
          <span className="logo-mark">SG</span>
          <div>
            <strong>Skill Gap</strong>
            <span>Analyzer</span>
          </div>
        </div>
        <nav className="nav" onClick={() => setOpen(false)}>
          {links.map((link) => (
            <NavLink key={link.to} to={link.to} end={link.to === '/'}>
              {link.label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-user">
          <div>
            <strong>{user?.name}</strong>
            <span>{user?.role}</span>
          </div>
          <button type="button" className="btn btn-ghost btn-sm" onClick={handleLogout}>
            Log out
          </button>
        </div>
      </aside>
      <div className="app-main">
        <header className="topbar">
          <button type="button" className="icon-btn" onClick={() => setOpen((v) => !v)} aria-label="Menu">
            ☰
          </button>
          <h1>Skill Gap Analyzer</h1>
          <span className="source-pill">{USE_MOCK ? 'Mock API' : 'Live API'}</span>
        </header>
        <main className="content">
          <Outlet />
        </main>
      </div>
      {open ? <div className="nav-backdrop" onClick={() => setOpen(false)} /> : null}
    </div>
  )
}
