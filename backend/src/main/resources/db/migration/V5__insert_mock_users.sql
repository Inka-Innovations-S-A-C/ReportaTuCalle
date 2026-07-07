-- Contraseña para todos es: Password123!
-- Generado con BCrypt (costo 10)

-- Insertar cuentas en auth.auth_accounts
INSERT INTO auth.auth_accounts (id, email, password, role, created_at) VALUES 
(1001, 'admin@test.com', '$2b$12$rzRTZUoA3DDQGUenjXhl1.o64ZK2xWkjqhZgSLbSVYG4wTx9f2YrO', 'ADMIN', CURRENT_TIMESTAMP),
(1002, 'supervisor@test.com', '$2b$12$rzRTZUoA3DDQGUenjXhl1.o64ZK2xWkjqhZgSLbSVYG4wTx9f2YrO', 'SUPERVISOR', CURRENT_TIMESTAMP),
(1003, 'ciudadano@test.com', '$2b$12$rzRTZUoA3DDQGUenjXhl1.o64ZK2xWkjqhZgSLbSVYG4wTx9f2YrO', 'CITIZEN', CURRENT_TIMESTAMP),

(1004, 'admin2@test.com', '$2b$12$rzRTZUoA3DDQGUenjXhl1.o64ZK2xWkjqhZgSLbSVYG4wTx9f2YrO', 'ADMIN', CURRENT_TIMESTAMP),
(1005, 'admin3@test.com', '$2b$12$rzRTZUoA3DDQGUenjXhl1.o64ZK2xWkjqhZgSLbSVYG4wTx9f2YrO', 'ADMIN', CURRENT_TIMESTAMP),

(1006, 'supervisor2@test.com', '$2b$12$rzRTZUoA3DDQGUenjXhl1.o64ZK2xWkjqhZgSLbSVYG4wTx9f2YrO', 'SUPERVISOR', CURRENT_TIMESTAMP),
(1007, 'supervisor3@test.com', '$2b$12$rzRTZUoA3DDQGUenjXhl1.o64ZK2xWkjqhZgSLbSVYG4wTx9f2YrO', 'SUPERVISOR', CURRENT_TIMESTAMP),

(1008, 'ciudadano2@test.com', '$2b$12$rzRTZUoA3DDQGUenjXhl1.o64ZK2xWkjqhZgSLbSVYG4wTx9f2YrO', 'CITIZEN', CURRENT_TIMESTAMP),
(1009, 'ciudadano3@test.com', '$2b$12$rzRTZUoA3DDQGUenjXhl1.o64ZK2xWkjqhZgSLbSVYG4wTx9f2YrO', 'CITIZEN', CURRENT_TIMESTAMP)

ON CONFLICT (email) DO UPDATE SET role = EXCLUDED.role;

-- Insertar perfiles en "user".user_profiles
INSERT INTO "user".user_profiles (id, account_id, first_name, last_name, phone, civic_score, created_at) VALUES 
(1001, 1001, 'Admin', 'Principal', '+51999999999', 0, CURRENT_TIMESTAMP),
(1002, 1002, 'Supervisor', 'Norte', '+51999999998', 0, CURRENT_TIMESTAMP),
(1003, 1003, 'Ciudadano', 'Mock', '+51999999997', 50, CURRENT_TIMESTAMP),

(1004, 1004, 'Admin', 'Secundario', '+51999999996', 0, CURRENT_TIMESTAMP),
(1005, 1005, 'Admin', 'Tercero', '+51999999995', 0, CURRENT_TIMESTAMP),

(1006, 1006, 'Supervisor', 'Sur', '+51999999994', 0, CURRENT_TIMESTAMP),
(1007, 1007, 'Supervisor', 'Este', '+51999999993', 0, CURRENT_TIMESTAMP),

(1008, 1008, 'Ciudadano', 'Dos', '+51999999992', 50, CURRENT_TIMESTAMP),
(1009, 1009, 'Ciudadano', 'Tres', '+51999999991', 50, CURRENT_TIMESTAMP)

ON CONFLICT (account_id) DO NOTHING;
