# ReportaTuCalle - Development Workflow

Este es el flujo de trabajo paso a paso para levantar todo el ecosistema de ReportaTuCalle en su entorno local.

---

### 1. Iniciar la Base de Datos (Docker)
Primero, es necesario levantar PostgreSQL + PostGIS usando Docker.
```bash
# Navegar a la carpeta principal
cd /home/yuse/Desktop/ReportaTuCalle/backend

# Iniciar la base de datos en segundo plano
docker compose up -d

# Para detener los contenedores cuando se termine: docker compose down
```

### 2. Iniciar el Backend (Spring Boot)
Una vez que la base de datos está ejecutándose, se inicia el servidor Java. Este aplicará automáticamente las migraciones (Flyway) y creará los usuarios predeterminados (`admin@test.com` y `supervisor@test.com`).
```bash
# Asegurarse de estar en la carpeta backend
cd /home/yuse/Desktop/ReportaTuCalle/backend

# Iniciar el backend (se ejecutará en http://localhost:8080)
mvn spring-boot:run
```

### 3. Iniciar el Frontend (React + Vite)
Con el backend listo, se levanta la interfaz visual en una **nueva terminal**.
```bash
# Abrir una nueva terminal y navegar al frontend
cd /home/yuse/Desktop/ReportaTuCalle/frontend

# Iniciar el servidor de desarrollo (se ejecutará en http://localhost:5173)
npm run dev
```

### 4. Simular Tráfico y Carga (Locust)
Para evaluar la resistencia del servidor ante múltiples solicitudes concurrentes, se puede iniciar una **tercera terminal** y lanzar el script de pruebas de carga.
```bash
# Navegar a la carpeta principal
cd /home/yuse/Desktop/ReportaTuCalle

# Activar el entorno virtual de Python
source venv/bin/activate

# Lanzar la GUI de Locust (se ejecutará en http://localhost:8089)
locust -f locustfile.py
```
*Abrir el navegador en `http://localhost:8089`, configurar el número de usuarios concurrentes y observar el tráfico en el servidor Spring Boot y el panel del Frontend.*
