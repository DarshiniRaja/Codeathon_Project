import { createContext, useContext, useMemo, useState } from 'react'
import { login as loginRequest } from '../api/services'
import { SESSION_KEY } from '../api/config'

const AuthContext = createContext(null)

function readSession() {
  try {
    const raw = localStorage.getItem(SESSION_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [session, setSession] = useState(readSession)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const value = useMemo(() => {
    async function login(role, email, password) {
      setLoading(true)
      setError('')
      try {
        const data = await loginRequest({ role, email, password })
        localStorage.setItem(SESSION_KEY, JSON.stringify(data))
        setSession(data)
        return data
      } catch (err) {
        setError(err.message || 'Login failed.')
        throw err
      } finally {
        setLoading(false)
      }
    }

    function logout() {
      localStorage.removeItem(SESSION_KEY)
      setSession(null)
    }

    return {
      session,
      user: session?.user || null,
      isAdmin: session?.user?.role === 'admin',
      login,
      logout,
      error,
      loading,
    }
  }, [session, error, loading])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
