# Reporta Tu Calle API - Simple

API REST simple en Spring Boot **sin base de datos**, datos en memoria. Para testear con Postman.

## Requisitos

```
Java 21
Maven 3.8+
```

## Ejecutar

```bash
cd backend
mvn spring-boot:run
```

Servidor estará en: `http://localhost:8080/api/v1`

## Endpoints

### Usuarios

```bash
# Registrar ciudadano (sin teléfono)
POST /api/v1/auth
{
  "email": "juan@test.com",
  "password": "Password123!",
  "nombre": "Juan Pérez",
  "tipo": "ciudadano"
}

# Registrar supervisor (con teléfono obligatorio)
POST /api/v1/auth
{
  "email": "supervisor@test.com",
  "password": "Password123!",
  "nombre": "García López",
  "tipo": "Supervisor",
  "telefono": "+573001234567"
}


### Reportes

```bash
# Crear reporte
POST /api/v1/reportes
{
  "titulo": "Bache en calle 5ta",
  "descripcion": "Bache grande frente a iglesia",
  "latitud": 10.4008,
  "longitud": -75.4822,
  "categoriaId": 1
}

# Obtener reporte
GET /api/v1/reportes/1

# Listar reportes
GET /api/v1/reportes

# Listar por estado
GET /api/v1/reportes?estado=nuevo

# Cambiar estado
PATCH /api/v1/reportes/1/estado?estado=revisado

# Eliminar
DELETE /api/v1/reportes/1
```

### Categorías

```bash
# Listar categorías
GET /api/v1/categorias

# Obtener una categoría
GET /api/v1/categorias/1
```

##  Categorías por Defecto en sprint 2
##  (Realmente el ADMIN es el que las crea)
```
1 - Baches
2 - Fuga de agua
3 - Falta de iluminación
4 - Alcantarilla tapada
5 - Árbol caído
6 - Basura acumulada
7 - Señal dañada
8 - Otro
```

## Datos

Los datos se guardan en una base de datos Postgres Local

## Próximos Pasos

- Mejorar aún más el frontend
- Buscar datasets reales para el análisis con el supervisor
- Subir la base de datos a la nube
---

**Versión:** 2.0 
**Estado:** Monolito modular con backend y frontend
