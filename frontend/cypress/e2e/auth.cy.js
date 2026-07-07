describe('Autenticación y Sesión', () => {
  beforeEach(() => {
    // Interceptar la llamada de login para no depender del backend real
    cy.intercept('POST', '**/api/v1/auth/login', {
      statusCode: 200,
      body: { data: { token: 'fake.eyJyb2xlIjoiU1VQRVJWSVNPUiIsImV4cCI6OTk5OTk5OTk5OSwicHJvZmlsZUlkIjoxfQ==.signature' } }
    }).as('loginRequest')
  })

  it('Debe mostrar error con credenciales inválidas', () => {
    cy.intercept('POST', '**/api/v1/auth/login', {
      statusCode: 401,
      body: { message: 'Credenciales inválidas' }
    }).as('loginError')

    cy.visit('/login')
    cy.get('input[type="email"]').type('test@fake.com')
    cy.get('input[type="password"]').type('wrongpass')
    cy.get('button[type="submit"]').click()

    cy.wait('@loginError')
    cy.contains('Correo o contraseña incorrectos').should('be.visible')
  })

  it('Debe permitir iniciar sesión correctamente', () => {
    cy.visit('/login')
    cy.get('input[type="email"]').type('supervisor@test.com')
    cy.get('input[type="password"]').type('123456')
    cy.get('button[type="submit"]').click()

    cy.wait('@loginRequest')
    // Verificar que redirige o guarda el token (simulado)
    cy.url().should('not.include', '/login')
  })
})
