import { api } from './client'

/** Shared API functions for dashboard, students, jobs, analysis, and applications. */

export function login(payload) {
  return api.post('/api/auth/login', payload)
}

export function getDashboard() {
  return api.get('/api/dashboard')
}

export function getStudents() {
  return api.get('/api/students')
}

export function createStudent(payload) {
  return api.post('/api/students', payload)
}

export function getStudent(id) {
  return api.get(`/api/students/${id}`)
}

export function getStudentSkills(id) {
  return api.get(`/api/students/${id}/skills`)
}

export async function addStudentSkill(id, payload) {
  const res = await api.post(`/api/students/${id}/skills`, payload)
  if (Array.isArray(res)) return res
  return getStudentSkills(id)
}

export function getJobs() {
  return api.get('/api/jobs')
}

export function createJob(payload) {
  return api.post('/api/jobs', payload)
}

export function getJob(id) {
  return api.get(`/api/jobs/${id}`)
}

export async function addJobSkill(id, payload) {
  const res = await api.post(`/api/jobs/${id}/skills`, payload)
  if (res && res.requiredSkills) return res
  return getJob(id)
}

export function getSkillGap(studentId, jobId) {
  return api.get(`/api/students/${studentId}/jobs/${jobId}/skill-gap`)
}

export async function getRecommendations(studentId, jobId) {
  const [res, student, job] = await Promise.all([
    api.get(`/api/students/${studentId}/jobs/${jobId}/recommendations`),
    getStudent(studentId).catch(() => null),
    getJob(jobId).catch(() => null),
  ])
  if (Array.isArray(res)) {
    return {
      studentId: Number(studentId),
      jobId: Number(jobId),
      studentName: student?.name || 'Student',
      jobTitle: job?.title || 'Job',
      items: res.map((r) => ({
        ...r,
        targetLevel: r.targetLevel ?? r.requiredLevel,
      })),
    }
  }
  return res
}

export function getApplications() {
  return api.get('/api/applications')
}

export function createApplication(payload) {
  return api.post('/api/applications', payload)
}
