# 🚀 ReportaTuCalle - Development Workflow

Este es el flujo de trabajo paso a paso para levantar todo el ecosistema de ReportaTuCalle en tu entorno local.

---

### 1. Iniciar la Base de Datos (Docker)
Primero, necesitamos levantar PostgreSQL + PostGIS usando Docker.
```bash
# Navega a la carpeta principal
cd /home/yuse/Desktop/ReportaTuCalle/backend

# Inicia la base de datos en segundo plano
docker compose up -d

# Para apagarla cuando termines: docker compose down
```

### 2. Iniciar el Backend (Spring Boot)
Una vez que la base de datos está corriendo, iniciamos el servidor Java. Él aplicará automáticamente las migraciones (Flyway) y creará los usuarios mock (`admin@test.com` y `supervisor@test.com`).
```bash
# Asegúrate de estar en la carpeta backend
cd /home/yuse/Desktop/ReportaTuCalle/backend

# Inicia el backend (correrá en http://localhost:8080)
mvn spring-boot:run
```

### 3. Iniciar el Frontend (React + Vite)
Con el backend listo, levantamos la interfaz visual en una **nueva terminal**.
```bash
# Abre una nueva terminal y navega al frontend
cd /home/yuse/Desktop/ReportaTuCalle/frontend

# Inicia el servidor de desarrollo (correrá en http://localhost:5173)
npm run dev
```

### 4. Simular Tráfico y Ataques (Locust Bot)
Si quieres ver cómo el servidor resiste múltiples ciudadanos enviando reportes a la vez, abre una **tercera terminal** y lanza los bots.
```bash
# Navega a la carpeta principal
cd /home/yuse/Desktop/ReportaTuCalle

# Activa el entorno virtual de Python
source venv/bin/activate

# Lanza la GUI de Locust (correrá en http://localhost:8089)
locust -f locustfile.py
```
*Abre tu navegador en `http://localhost:8089`, pon 100 usuarios, ¡y mira cómo explota de actividad la terminal de Spring Boot y cómo aparecen marcadores en el mapa del Frontend!*
