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

export function addStudentSkill(id, payload) {
  return api.post(`/api/students/${id}/skills`, payload)
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

export function addJobSkill(id, payload) {
  return api.post(`/api/jobs/${id}/skills`, payload)
}

export function getSkillGap(studentId, jobId) {
  return api.get(`/api/students/${studentId}/jobs/${jobId}/skill-gap`)
}

export function getRecommendations(studentId, jobId) {
  return api.get(`/api/students/${studentId}/jobs/${jobId}/recommendations`)
}

export function getApplications() {
  return api.get('/api/applications')
}

export function createApplication(payload) {
  return api.post('/api/applications', payload)
}
