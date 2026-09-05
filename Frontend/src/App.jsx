import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext'
import { Layout } from './components/Layout'
import { ProtectedRoute } from './components/ProtectedRoute'
import { AddJobPage } from './pages/AddJobPage'
import { AddStudentPage } from './pages/AddStudentPage'
import { AnalyzerPage } from './pages/AnalyzerPage'
import { ApplicationsPage } from './pages/ApplicationsPage'
import { DashboardPage } from './pages/DashboardPage'
import { JobDetailsPage } from './pages/JobDetailsPage'
import { JobsPage } from './pages/JobsPage'
import { LoginPage } from './pages/LoginPage'
import { ManageSkillsPage } from './pages/ManageSkillsPage'
import { RecommendationsPage } from './pages/RecommendationsPage'
import { StudentProfilePage } from './pages/StudentProfilePage'
import { StudentsPage } from './pages/StudentsPage'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route element={<ProtectedRoute />}>
            <Route element={<Layout />}>
              <Route index element={<DashboardPage />} />
              <Route path="profile" element={<StudentProfilePage />} />
              <Route path="profile/skills" element={<ManageSkillsPage />} />
              <Route path="jobs" element={<JobsPage />} />
              <Route path="jobs/:id" element={<JobDetailsPage />} />
              <Route path="analyzer" element={<AnalyzerPage />} />
              <Route path="recommendations" element={<RecommendationsPage />} />
              <Route path="applications" element={<ApplicationsPage />} />
              <Route element={<ProtectedRoute roles={['admin']} />}>
                <Route path="students" element={<StudentsPage />} />
                <Route path="students/new" element={<AddStudentPage />} />
                <Route path="students/:id" element={<StudentProfilePage />} />
                <Route path="students/:id/skills" element={<ManageSkillsPage />} />
                <Route path="jobs/new" element={<AddJobPage />} />
              </Route>
            </Route>
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
