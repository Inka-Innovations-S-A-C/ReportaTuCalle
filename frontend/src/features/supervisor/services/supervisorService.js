import apiClient from '../../../shared/lib/apiClient'

export async function getAssignedReports() {
  const { data: envelope } = await apiClient.get('/reports/assigned')
  return envelope.data
}

export async function getAllReports() {
  const { data: envelope } = await apiClient.get('/reports')
  return envelope.data
}

export async function selfAssignReport(reportId) {
  const { data: envelope } = await apiClient.put(`/reports/${reportId}/self-assign`)
  return envelope.data
}

export async function optimizeRoute(categoryId, startLatitude, startLongitude, reportIds) {
  const { data: envelope } = await apiClient.post(
    `/reports/optimize-route`,
    {
      categoryId,
      startLatitude,
      startLongitude,
      reportIds
    }
  )
  return envelope.data
}

export async function updateReportStatus(reportId, newStatus, resolutionImageUrl = null) {
  const { data } = await apiClient.put(`/reports/${reportId}/status`, { 
    status: newStatus,
    resolutionImageUrl
  })
  return data.data
}

export async function bulkAssignReports(reportIds) {
  const { data } = await apiClient.put('/reports/bulk-assign', reportIds)
  return data.data
}
