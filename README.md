# Enajenarte API

> API REST académica desarrollada para la asignatura **Proyecto Intermodular** del Grado Superior en Desarrollo de Aplicaciones Multiplataforma.

Enajenarte es una plataforma centrada en la salud emocional a través de la expresión artística, la comunicación y el autoconocimiento. Esta API proporciona el backend completo para gestionar eventos, talleres, usuarios e inscripciones.

---

## 📦 Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje principal |
| Spring Boot | Framework |
| Maven | Gestión de dependencias |
| JPA / Hibernate | Persistencia |
| MariaDB | Base de datos |
| ModelMapper | Mapeo de DTOs |
| JWT | Autenticación |

---

## 🗂️ Arquitectura

```
domain        →  Entidades y enums
repository    →  Acceso a datos
service       →  Lógica de negocio
controller    →  Endpoints REST
dto           →  Objetos de transferencia
exception     →  Manejo de errores
config        →  Seguridad y configuración
```

---

## 📐 Modelo de dominio

| Entidad | Descripción |
|---|---|
| **Event** | Eventos organizados o en los que participa Enajenarte |
| **Speaker** | Ponentes con especialidad y experiencia |
| **Workshop** | Talleres con modalidad, fechas, capacidad y estado |
| **User** | Usuarios registrados en la plataforma |
| **Registration** | Relación entre usuarios y talleres |

---

## 🔐 Seguridad

La API implementa autenticación mediante **JWT** con control de acceso por roles.

### Roles

| Rol | Acceso |
|---|---|
| Público | Eventos, workshops, registro de usuario |
| `USER` | Operaciones personales, historial de inscripciones |
| `ADMIN` | Gestión completa de todas las entidades |

### Login

```
POST /auth/login
```

```json
{
  "username": "tu_usuario",
  "password": "tu_contraseña"
}
```

Devuelve un token JWT que debe incluirse en el header de las peticiones protegidas:

```
Authorization: Bearer <token>
```

---

## 📋 Endpoints

### Users
| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/users` | ADMIN |
| GET | `/users/{id}` | Autenticado |
| GET | `/users/{id}/registrations` | Autenticado |
| POST | `/users` | Público |
| PUT | `/users/{id}` | Autenticado |
| DELETE | `/users/{id}` | ADMIN |

### Events
| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/events` | Público |
| GET | `/events/{id}` | Público |
| POST | `/events` | ADMIN |
| PUT | `/events/{id}` | ADMIN |
| DELETE | `/events/{id}` | ADMIN |

### Workshops
| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/workshops` | Público |
| GET | `/workshops/{id}` | Público |
| POST | `/workshops` | ADMIN |
| PUT | `/workshops/{id}` | ADMIN |
| DELETE | `/workshops/{id}` | ADMIN |

### Speakers
| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/speakers` | Público |
| GET | `/speakers/{id}` | Público |
| POST | `/speakers` | ADMIN |
| PUT | `/speakers/{id}` | ADMIN |
| DELETE | `/speakers/{id}` | ADMIN |

### Registrations
| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/registrations` | Autenticado |
| GET | `/registrations/{id}` | Autenticado |
| POST | `/registrations` | Autenticado |
| PUT | `/registrations/{id}` | ADMIN |
| DELETE | `/registrations/{id}` | ADMIN |

Todos los endpoints GET admiten hasta **tres filtros simultáneos** mediante parámetros de consulta.

---

## ⚙️ Lógica de negocio

### Workshops

Los talleres tienen tres estados posibles: `PENDING`, `CONFIRMED` y `CANCELLED`.

- Los talleres **online** se confirman automáticamente al crearse.
- Los talleres **presenciales** quedan en `PENDING` hasta alcanzar el mínimo de participantes.
- Un **scheduler** comprueba periódicamente si la fecha límite ha pasado sin alcanzar el mínimo y cancela el taller automáticamente.

### Registrations

- Control de **duplicados**: un usuario no puede inscribirse dos veces al mismo taller.
- Control de **capacidad**: se verifica el aforo disponible antes de confirmar.
- Soporte de **tickets múltiples**: entre 1 y 5 personas por inscripción.
- Generación automática de **código de confirmación** y **fecha de inscripción**.

### Pagos

```java
enum PaymentStatus {
    PENDING,
    PAID
}
```

El estado de pago es modificable por ADMIN y se valida contra los valores del enum (`400 Bad Request` si el valor no es válido).

---

## 🧪 Tests

El proyecto incluye tests unitarios con **JUnit 5** y **Mockito** para las capas de servicio y controlador.

```bash
mvn test
```

Para compilar sin ejecutar los tests:

```bash
mvn clean compile -DskipTests
```

---

## 🚀 Ejecución

```bash
mvn clean compile
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

---

## ❌ Gestión de errores

Todos los errores se devuelven con el siguiente formato:

```json
{
  "code": 404,
  "title": "not-found",
  "message": "The workshop does not exist",
  "errors": {}
}
```

| Código | Descripción |
|---|---|
| `400` | Bad Request — validación fallida o valor inválido |
| `404` | Not Found — recurso no encontrado |
| `500` | Internal Server Error |
