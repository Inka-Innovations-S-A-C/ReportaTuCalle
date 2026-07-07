describe('Citizen UI Tests', () => {
  beforeEach(() => {
    // Bloquear llamadas SSE o WebSockets para evitar errores rojos en consola
    cy.intercept('**/api/v1/sse/reports*', { statusCode: 200, body: '' }).as('sseReports')
    cy.intercept('**/ws-tracking/**', { statusCode: 200, body: {} }).as('wsTracking')
  })

  it('Should load the dashboard successfully', () => {
    cy.visit('/dashboard')
    cy.contains('ReportaTuCalle').should('be.visible')
  })
})
