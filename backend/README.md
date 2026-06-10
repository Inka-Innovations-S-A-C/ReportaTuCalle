# Reporta Tu Calle API - Simple

API REST simple en Spring Boot **sin base de datos**, datos en memoria. Para testear con Postman.

## Requisitos

```
Java 25
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
POST /api/v1/usuarios
{
  "email": "juan@test.com",
  "password": "Password123!",
  "nombre": "Juan Pérez",
  "tipo": "ciudadano"
}

# Registrar supervisor (con teléfono obligatorio)
POST /api/v1/usuarios
{
  "email": "supervisor@test.com",
  "password": "Password123!",
  "nombre": "García López",
  "tipo": "supervisor",
  "telefono": "+573001234567"
}

# Obtener usuario
GET /api/v1/usuarios/1

# Listar todos
GET /api/v1/usuarios
```

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

##  Categorías por Defecto

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

Los datos se guardan **EN MEMORIA** mientras el servidor está activo. Al reiniciar se limpian.

## Próximos Pasos

- Agregar JWT para autenticación
- Conectar con MongoDB
- Agregar validaciones más robustas
- Agregar error handling global

---

**Versión:** 1.0  
**Estado:** API Simple & Funcional 
