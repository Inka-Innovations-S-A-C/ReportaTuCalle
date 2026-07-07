describe('Panel de Administrador', () => {
  beforeEach(() => {
    cy.window().then((win) => {
      win.localStorage.setItem('rtk_token', 'fake.eyJyb2xlIjoiQURNSU4iLCJleHAiOjk5OTk5OTk5OTksInByb2ZpbGVJZCI6MX0=.signature')
      win.localStorage.setItem('rtk_usuario', JSON.stringify({ email: 'admin@test.com', role: 'ADMIN', profileId: 1 }))
    })
    
    // Interceptar métricas generales
    cy.intercept('GET', '**/api/v1/admin/metrics*', {
      statusCode: 200,
      body: {
        totalReports: 100,
        resolvedReports: 45
      }
    }).as('getMetrics')

    cy.intercept('GET', '**/api/v1/reports*', { statusCode: 200, body: { data: [] } }).as('getReports')
    cy.intercept('GET', '**/api/v1/users*', { statusCode: 200, body: { data: [] } }).as('getUsers')
    cy.intercept('GET', '**/api/v1/auth/accounts*', { statusCode: 200, body: { data: [] } }).as('getAccounts')
  })

  it('Debe cargar las métricas y la navegación del administrador', () => {
    cy.visit('/admin')
    
    cy.contains('Panel de Administración').should('exist')
    
    // Validar pestañas
    cy.contains('button', 'Usuarios').should('be.visible')
    cy.contains('button', 'Categorías').should('be.visible')
    cy.contains('button', 'Reportes').should('be.visible')
  })
})
