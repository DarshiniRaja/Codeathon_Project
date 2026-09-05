import { PROFICIENCY } from './config'

const STORAGE_KEY = 'sga.mock.db.v1'

function nowIso() {
  return new Date().toISOString()
}

function seed() {
  return {
    nextId: 8,
    users: [
      { id: 1, name: 'Admin User', email: 'admin@skillgap.local', password: 'admin123', role: 'admin' },
      { id: 2, name: 'Aisha Khan', email: 'aisha@student.local', password: 'student123', role: 'student', studentId: 1 },
      { id: 3, name: 'Ben Ortiz', email: 'ben@student.local', password: 'student123', role: 'student', studentId: 2 },
    ],
    students: [
      { id: 1, name: 'Aisha Khan', email: 'aisha@student.local', createdAt: nowIso() },
      { id: 2, name: 'Ben Ortiz', email: 'ben@student.local', createdAt: nowIso() },
      { id: 3, name: 'Chris Patel', email: 'chris@student.local', createdAt: nowIso() },
    ],
    studentSkills: [
      { studentId: 1, skill: 'Python', proficiency: 4 },
      { studentId: 1, skill: 'SQL', proficiency: 3 },
      { studentId: 1, skill: 'Communication', proficiency: 5 },
      { studentId: 2, skill: 'Python', proficiency: 2 },
      { studentId: 2, skill: 'JavaScript', proficiency: 3 },
      { studentId: 3, skill: 'Excel', proficiency: 4 },
    ],
    jobs: [
      {
        id: 1,
        title: 'Data Analyst Intern',
        department: 'Analytics',
        description: 'Support reporting and data cleaning.',
        createdAt: nowIso(),
      },
      {
        id: 2,
        title: 'Junior Frontend Developer',
        department: 'Engineering',
        description: 'Build UI for internal tools.',
        createdAt: nowIso(),
      },
    ],
    jobSkills: [
      { jobId: 1, skill: 'Python', requiredLevel: 4, mandatory: true },
      { jobId: 1, skill: 'SQL', requiredLevel: 4, mandatory: true },
      { jobId: 1, skill: 'Communication', requiredLevel: 3, mandatory: false },
      { jobId: 2, skill: 'JavaScript', requiredLevel: 4, mandatory: true },
      { jobId: 2, skill: 'Python', requiredLevel: 2, mandatory: false },
    ],
    applications: [{ id: 1, studentId: 1, jobId: 1, status: 'submitted', createdAt: nowIso() }],
  }
}

function load() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (raw) return JSON.parse(raw)
  } catch {
    localStorage.removeItem(STORAGE_KEY)
  }
  const db = seed()
  save(db)
  return db
}

function save(db) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(db))
}

function nextId(db) {
  db.nextId += 1
  return db.nextId
}

function delay(result, ms = 180) {
  return new Promise((resolve) => setTimeout(() => resolve(structuredClone(result)), ms))
}

function fail(status, message) {
  const err = new Error(message)
  err.status = status
  return Promise.reject(err)
}

function studentSkills(db, studentId) {
  return db.studentSkills
    .filter((s) => s.studentId === studentId)
    .map((s) => ({
      skill: s.skill,
      proficiency: s.proficiency,
      proficiencyLabel: PROFICIENCY[s.proficiency] || String(s.proficiency),
    }))
}

function jobWithSkills(db, job) {
  return {
    ...job,
    requiredSkills: db.jobSkills
      .filter((s) => s.jobId === job.id)
      .map((s) => ({
        skill: s.skill,
        requiredLevel: s.requiredLevel,
        requiredLevelLabel: PROFICIENCY[s.requiredLevel] || String(s.requiredLevel),
        mandatory: Boolean(s.mandatory),
      })),
  }
}

/**
 * Mock backend skill-gap engine. UI must render these fields as-is.
 */
function computeSkillGap(db, studentId, jobId) {
  const student = db.students.find((s) => s.id === studentId)
  const job = db.jobs.find((j) => j.id === jobId)
  if (!student || !job) return null

  const current = studentSkills(db, studentId)
  const required = jobWithSkills(db, job).requiredSkills
  const currentMap = Object.fromEntries(current.map((s) => [s.skill.toLowerCase(), s]))

  const skills = required.map((req) => {
    const have = currentMap[req.skill.toLowerCase()]
    const currentLevel = have ? have.proficiency : 0
    const gap = Math.max(0, req.requiredLevel - currentLevel)
    let status = 'matched'
    if (!have) status = 'missing'
    else if (gap > 0) status = 'gap'
    return {
      skill: req.skill,
      currentLevel,
      currentLevelLabel: have ? have.proficiencyLabel : 'None',
      requiredLevel: req.requiredLevel,
      requiredLevelLabel: req.requiredLevelLabel,
      gap,
      status,
      mandatory: req.mandatory,
    }
  })

  const overallMatchPercent = skills.length
    ? Math.round(
        (skills.reduce((sum, s) => sum + Math.min(s.currentLevel, s.requiredLevel) / s.requiredLevel, 0) /
          skills.length) *
          100,
      )
    : 0

  return {
    studentId,
    studentName: student.name,
    jobId,
    jobTitle: job.title,
    overallMatchPercent,
    skills,
  }
}

function computeRecommendations(gap) {
  if (!gap) return null
  const items = gap.skills
    .filter((s) => s.status !== 'matched')
    .map((s) => {
      const priority = s.mandatory && s.gap >= 2 ? 'High' : s.gap >= 1 ? 'Medium' : 'Low'
      const reason =
        s.status === 'missing'
          ? `${s.skill} is required at ${s.requiredLevelLabel} (${s.requiredLevel}) but is not on the student profile.`
          : `${s.skill} is at ${s.currentLevelLabel} (${s.currentLevel}); the job asks for ${s.requiredLevelLabel} (${s.requiredLevel}).`
      return {
        priority,
        skill: s.skill,
        currentLevel: s.currentLevel,
        currentLevelLabel: s.currentLevelLabel,
        targetLevel: s.requiredLevel,
        targetLevelLabel: s.requiredLevelLabel,
        reason,
      }
    })
  const order = { High: 0, Medium: 1, Low: 2 }
  items.sort((a, b) => order[a.priority] - order[b.priority])
  return {
    studentId: gap.studentId,
    jobId: gap.jobId,
    studentName: gap.studentName,
    jobTitle: gap.jobTitle,
    items,
  }
}

function dashboard(db) {
  const gaps = []
  db.students.forEach((st) => {
    db.jobs.forEach((job) => {
      const result = computeSkillGap(db, st.id, job.id)
      if (result) gaps.push(result)
    })
  })
  const averageSkillMatch =
    gaps.length === 0
      ? null
      : Math.round(gaps.reduce((sum, g) => sum + g.overallMatchPercent, 0) / gaps.length)

  const skillGapCounts = {}
  gaps.forEach((g) => {
    g.skills.forEach((s) => {
      if (s.status !== 'matched') {
        skillGapCounts[s.skill] = (skillGapCounts[s.skill] || 0) + 1
      }
    })
  })
  const topSkillGaps = Object.entries(skillGapCounts)
    .map(([skill, gapCount]) => ({ skill, gapCount }))
    .sort((a, b) => b.gapCount - a.gapCount)
    .slice(0, 5)

  return {
    totalStudents: db.students.length,
    totalJobs: db.jobs.length,
    totalApplications: db.applications.length,
    averageSkillMatch,
    topSkillGaps,
  }
}

const handlers = {
  'POST /api/auth/login': (db, _p, body) => {
    const email = String(body.email || '').trim().toLowerCase()
    const password = String(body.password || '')
    const role = body.role === 'student' ? 'student' : 'admin'
    const user = db.users.find(
      (u) => u.email.toLowerCase() === email && u.password === password && u.role === role,
    )
    if (!user) return fail(401, 'Invalid email, password, or role.')
    return delay({
      token: `mock-token-${user.id}`,
      user: {
        id: user.id,
        name: user.name,
        email: user.email,
        role: user.role,
        studentId: user.studentId || null,
      },
    })
  },
  'GET /api/dashboard': (db) => delay(dashboard(db)),
  'GET /api/students': (db) =>
    delay(
      db.students.map((s) => ({
        ...s,
        skillCount: db.studentSkills.filter((x) => x.studentId === s.id).length,
      })),
    ),
  'POST /api/students': (db, _p, body) => {
    const name = String(body.name || '').trim()
    const email = String(body.email || '').trim().toLowerCase()
    if (!name) return fail(400, 'Name is required.')
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) return fail(400, 'A valid email is required.')
    if (db.students.some((s) => s.email.toLowerCase() === email)) {
      return fail(409, 'A student with this email already exists.')
    }
    const student = { id: nextId(db), name, email, createdAt: nowIso() }
    db.students.push(student)
    db.users.push({
      id: nextId(db),
      name,
      email,
      password: 'student123',
      role: 'student',
      studentId: student.id,
    })
    save(db)
    return delay(student, 220)
  },
  'GET /api/students/:id': (db, params) => {
    const student = db.students.find((s) => s.id === Number(params.id))
    if (!student) return fail(404, 'Student not found.')
    return delay({ ...student, skills: studentSkills(db, student.id) })
  },
  'GET /api/students/:id/skills': (db, params) => {
    const student = db.students.find((s) => s.id === Number(params.id))
    if (!student) return fail(404, 'Student not found.')
    return delay(studentSkills(db, student.id))
  },
  'POST /api/students/:id/skills': (db, params, body) => {
    const studentId = Number(params.id)
    const student = db.students.find((s) => s.id === studentId)
    if (!student) return fail(404, 'Student not found.')
    const skill = String(body.skill || '').trim()
    const proficiency = Number(body.proficiency)
    if (!skill) return fail(400, 'Skill name is required.')
    if (!Number.isInteger(proficiency) || proficiency < 1 || proficiency > 5) {
      return fail(400, 'Proficiency must be an integer from 1 to 5.')
    }
    const existing = db.studentSkills.find(
      (s) => s.studentId === studentId && s.skill.toLowerCase() === skill.toLowerCase(),
    )
    if (existing) existing.proficiency = proficiency
    else db.studentSkills.push({ studentId, skill, proficiency })
    save(db)
    return delay(studentSkills(db, studentId), 200)
  },
  'GET /api/jobs': (db) =>
    delay(
      db.jobs.map((j) => ({
        ...j,
        requiredSkillCount: db.jobSkills.filter((s) => s.jobId === j.id).length,
      })),
    ),
  'POST /api/jobs': (db, _p, body) => {
    const title = String(body.title || '').trim()
    const department = String(body.department || '').trim()
    const description = String(body.description || '').trim()
    if (!title) return fail(400, 'Job title is required.')
    if (!department) return fail(400, 'Department is required.')
    const job = { id: nextId(db), title, department, description, createdAt: nowIso() }
    db.jobs.push(job)
    save(db)
    return delay(job, 220)
  },
  'GET /api/jobs/:id': (db, params) => {
    const job = db.jobs.find((j) => j.id === Number(params.id))
    if (!job) return fail(404, 'Job not found.')
    return delay(jobWithSkills(db, job))
  },
  'POST /api/jobs/:id/skills': (db, params, body) => {
    const jobId = Number(params.id)
    const job = db.jobs.find((j) => j.id === jobId)
    if (!job) return fail(404, 'Job not found.')
    const skill = String(body.skill || '').trim()
    const requiredLevel = Number(body.requiredLevel)
    const mandatory = Boolean(body.mandatory)
    if (!skill) return fail(400, 'Skill name is required.')
    if (!Number.isInteger(requiredLevel) || requiredLevel < 1 || requiredLevel > 5) {
      return fail(400, 'Required level must be an integer from 1 to 5.')
    }
    const existing = db.jobSkills.find(
      (s) => s.jobId === jobId && s.skill.toLowerCase() === skill.toLowerCase(),
    )
    if (existing) {
      existing.requiredLevel = requiredLevel
      existing.mandatory = mandatory
    } else {
      db.jobSkills.push({ jobId, skill, requiredLevel, mandatory })
    }
    save(db)
    return delay(jobWithSkills(db, job), 200)
  },
  'GET /api/students/:studentId/jobs/:jobId/skill-gap': (db, params) => {
    const gap = computeSkillGap(db, Number(params.studentId), Number(params.jobId))
    if (!gap) return fail(404, 'Student or job not found.')
    return delay(gap, 260)
  },
  'GET /api/students/:studentId/jobs/:jobId/recommendations': (db, params) => {
    const gap = computeSkillGap(db, Number(params.studentId), Number(params.jobId))
    if (!gap) return fail(404, 'Student or job not found.')
    return delay(computeRecommendations(gap), 260)
  },
  'GET /api/applications': (db) =>
    delay(
      db.applications.map((a) => ({
        ...a,
        studentName: db.students.find((s) => s.id === a.studentId)?.name || 'Unknown',
        jobTitle: db.jobs.find((j) => j.id === a.jobId)?.title || 'Unknown',
      })),
    ),
  'POST /api/applications': (db, _p, body) => {
    const studentId = Number(body.studentId)
    const jobId = Number(body.jobId)
    if (!db.students.some((s) => s.id === studentId)) return fail(400, 'Valid studentId is required.')
    if (!db.jobs.some((j) => j.id === jobId)) return fail(400, 'Valid jobId is required.')
    if (db.applications.some((a) => a.studentId === studentId && a.jobId === jobId)) {
      return fail(409, 'This student has already applied to that job.')
    }
    const application = { id: nextId(db), studentId, jobId, status: 'submitted', createdAt: nowIso() }
    db.applications.push(application)
    save(db)
    return delay(
      {
        ...application,
        studentName: db.students.find((s) => s.id === studentId).name,
        jobTitle: db.jobs.find((j) => j.id === jobId).title,
      },
      240,
    )
  },
}

const ROUTES = Object.keys(handlers).map((key) => {
  const [method, path] = key.split(' ')
  const names = []
  const regex = new RegExp(
    `^${path.replace(/:[a-zA-Z]+/g, (seg) => {
      names.push(seg.slice(1))
      return '([^/]+)'
    })}$`,
  )
  return { method, regex, names, handler: handlers[key] }
})

export function mockHandle(method, path, body) {
  const db = load()
  const match = ROUTES.find((r) => r.method === method && r.regex.test(path))
  if (!match) return fail(404, `Mock endpoint not found: ${method} ${path}`)
  const values = path.match(match.regex).slice(1)
  const params = Object.fromEntries(match.names.map((name, i) => [name, values[i]]))
  return match.handler(db, params, body || {})
}
