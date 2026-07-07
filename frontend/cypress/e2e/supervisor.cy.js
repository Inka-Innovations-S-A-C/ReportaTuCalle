describe('Panel de Supervisor', () => {
  beforeEach(() => {
    // Simular que el usuario ya está autenticado
    cy.window().then((win) => {
      win.localStorage.setItem('rtk_token', 'fake.eyJyb2xlIjoiU1VQRVJWSVNPUiIsImV4cCI6OTk5OTk5OTk5OSwicHJvZmlsZUlkIjoxfQ==.signature')
      win.localStorage.setItem('rtk_usuario', JSON.stringify({ email: 'sup@test.com', role: 'SUPERVISOR', profileId: 1 }))
    })
    
    // Interceptar lista de reportes para el mapa
    cy.intercept('GET', '**/api/v1/reports/assigned*', {
      statusCode: 200,
      body: {
        data: [
          {
            id: 1,
            title: 'Bache peligroso',
            status: 'PENDING',
            latitude: -12.0464,
            longitude: -77.0428
          }
        ]
      }
    }).as('getAssignedReports')

    cy.intercept('GET', '**/api/v1/reports*', {
      statusCode: 200,
      body: { data: [] }
    }).as('getAllReports')

    cy.intercept('GET', '**/api/v1/categories*', {
      statusCode: 200,
      body: { data: [] }
    }).as('getCategories')

    // Bloquear todas las llamadas de WebSocket/SockJS (info, xhr_streaming, etc)
    cy.intercept('**/ws-tracking/**', {
      statusCode: 200,
      body: {}
    }).as('wsTracking')
  })

  it('Debe cargar el mapa y mostrar los reportes asignados', () => {
    cy.visit('/supervisor')
    cy.wait(['@getAssignedReports', '@getAllReports'])

    // Verificar controles y texto
    cy.contains('Gestión centralizada de reportes').should('be.visible')
    cy.contains('Categoría a Optimizar').should('be.visible')
    
    // Verificar que el mapa se haya renderizado
    cy.get('.leaflet-container').should('exist')
  })
})
