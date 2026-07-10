# ReportaTuCalle

Plataforma de gestión cívica para reportes de incidentes viales. Construido con arquitectura de grado empresarial para asegurar mantenibilidad y escalabilidad.

## Arquitectura y Tecnologías
- **Backend:** Java 21, Spring Boot 3, Arquitectura Hexagonal (Puertos y Adaptadores), PostgreSQL + PostGIS (Datos Geoespaciales).
- **Frontend:** React 18, Vite, Tailwind CSS, Leaflet (Mapas Interactivos), SSE (Server-Sent Events) para tiempo real.
- **QA:** JUnit 5 + Mockito (Pruebas Unitarias Backend), Cypress (Pruebas E2E Frontend).

---

## Flujo de Trabajo de Desarrollo (Cómo ejecutar el proyecto)

### 1. Iniciar Base de Datos (Docker)
Requisito previo: Tener Docker y Docker Compose instalados.
```bash
cd backend
docker-compose up -d
```
Esto levanta un contenedor con PostgreSQL y la extensión PostGIS habilitada, expuesto en el puerto `5432`.

### 2. Levantar el Backend (Spring Boot)
Requisito previo: Java 21 instalado.
```bash
cd backend
./mvnw clean spring-boot:run
```
El servidor backend se ejecutará en `http://localhost:8080`.

### 3. Levantar el Frontend (React)
Requisito previo: Node.js (v18+) instalado.
```bash
cd frontend
npm install
npm run dev
```
La aplicación web estará disponible en `http://localhost:5173`.

---

## Ejecución de Pruebas (QA)

### Pruebas Unitarias (Backend)
Verifican la lógica de negocio aislada usando Mockito.
```bash
cd backend
./mvnw clean test
```

### Pruebas E2E / Integración (Frontend con Cypress)
Aseguran que la interfaz gráfica y los flujos de usuario (Login, Dashboard) funcionen correctamente en un navegador real.

**Modo Interactivo (Recomendado para desarrollo):**
Abre una interfaz gráfica donde puedes ver al "robot" hacer clics y navegar paso a paso.
```bash
cd frontend
npm run cypress:open
```
*Nota: Asegúrate de tener el Frontend corriendo (`npm run dev`) antes de abrir Cypress.*

**Modo Consola (Para CI/CD):**
Ejecuta las pruebas de forma silenciosa en la terminal.
```bash
cd frontend
npm run cypress:run
```

---

## Estructura del Repositorio
- `/backend/src/main/java/com/reportatucalle/modules/` -> Lógica separada por módulos (Auth, Report, User, Optimization) respetando Arquitectura Hexagonal.
- `/frontend/src/` -> Componentes React modulares.
- `/frontend/cypress/e2e/` -> Archivos de prueba End-to-End.
