import { API_BASE_URL, USE_MOCK } from './config'
import { mockHandle } from './mockStore'

export { USE_MOCK }

export async function apiRequest(method, path, body) {
  if (USE_MOCK) {
    return mockHandle(method, path, body)
  }

  const res = await fetch(`${API_BASE_URL}${path}`, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: body != null ? JSON.stringify(body) : undefined,
  })

  if (!res.ok) {
    let message = res.statusText || 'Request failed'
    try {
      const data = await res.json()
      message = data.message || data.error || message
    } catch {
      /* ignore */
    }
    const err = new Error(message)
    err.status = res.status
    throw err
  }

  if (res.status === 204) return null
  return res.json()
}

export const api = {
  get: (path) => apiRequest('GET', path),
  post: (path, body) => apiRequest('POST', path, body),
}
