# enajenArte API

API REST desarrollada como backend del proyecto **enajenArte**, una plataforma orientada a la gestión de talleres, programas, eventos, inscripciones y comunicación con usuarios dentro del ámbito de la creatividad, el bienestar emocional y la expresión artística.

El proyecto forma parte del Trabajo Fin de Grado del ciclo de Desarrollo de Aplicaciones Multiplataforma. Su objetivo es transformar la presencia digital de enajenArte en una aplicación dinámica, con gestión administrativa, inscripción de usuarios y persistencia de datos.

---

## Tecnologías utilizadas

| Tecnología | Uso principal |
|---|---|
| Java 21 | Lenguaje principal del backend |
| Spring Boot | Framework principal para construir la API REST |
| Spring Web | Exposición de endpoints HTTP |
| Spring Data JPA | Persistencia y acceso a base de datos |
| Hibernate | ORM para el mapeo entidad-tabla |
| MariaDB | Base de datos relacional |
| Maven | Gestión de dependencias y compilación |
| Spring Security | Control de acceso por roles |
| JWT | Autenticación mediante token |
| ModelMapper | Conversión entre entidades y DTOs |
| Spring Mail | Envío de correos electrónicos |

---

## Arquitectura del proyecto

El backend sigue una arquitectura por capas:

```text
domain        → Entidades JPA y enums
repository    → Interfaces de acceso a datos
service       → Lógica de negocio
controller    → Endpoints REST
dto           → Objetos de entrada y salida
exception     → Excepciones personalizadas y manejador global
config        → Configuración general, seguridad y JWT
security      → Filtros y utilidades de autenticación
```

Esta separación permite mantener una estructura clara, facilitar las pruebas y aislar la lógica de negocio de la capa de exposición REST.

---

## Modelo de dominio

Las principales entidades del proyecto son:

| Entidad | Descripción                                                   |
|---|---------------------------------------------------------------|
| `User` | Usuario registrado en la plataforma                           |
| `Speaker` | Ponente asociado a talleres, programas o eventos              |
| `Workshop` | Taller puntual con fecha, capacidad, modalidad y estado       |
| `Program` | Programa de larga duración con fecha de inicio y finalización |
| `Event` | Evento gestionado desde el panel de administración            |
| `Registration` | Inscripción de un usuario a un taller                         |
| `ProgramRegistration` | Inscripción de un usuario a un programa                       |
| `ContactMessage` | Mensaje enviado desde el formulario de contacto               |
| `AdminCalendar` | Entrada de calendario administrativo                          |

---

## Seguridad

La API utiliza autenticación mediante JWT y autorización basada en roles.

### Roles

| Rol | Descripción                                                                                                                                                                                                        |
|---|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `USER` | Usuario registrado. Puede gestionar su perfil y sus propias inscripciones.                                                                                                                                         |
| `ADMIN` | Usuario administrador. Puede gestionar usuarios, ponentes, talleres, programas, eventos, inscripciones y calendario. A nivel de API, también existen endpoints administrativos para mensajes de contacto vía email |

### Login

Endpoint:

```http
POST /auth/login
```

Body:

```json
{
  "username": "admin",
  "password": "123456"
}
```

La respuesta incluye un token JWT que debe enviarse en las peticiones protegidas:

```http
Authorization: Bearer <token>
```

---

## Endpoints principales

### Autenticación

| Método | Endpoint | Acceso |
|---|---|---|
| POST | `/auth/login` | Público |

---

### Usuarios

| Método | Endpoint | Acceso |
|---|---|---|
| POST | `/users` | Público |
| GET | `/users` | ADMIN |
| GET | `/users/{id}` | ADMIN |
| GET | `/users/me` | Usuario autenticado |
| PUT | `/users/{id}` | Usuario autenticado |
| DELETE | `/users/{id}` | ADMIN |
| GET | `/users/{id}/registrations` | Usuario propietario o ADMIN |
| GET | `/users/{id}/program-registrations` | Usuario propietario o ADMIN |

---

### Ponentes

| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/speakers` | Público |
| GET | `/speakers/{id}` | Público |
| POST | `/speakers` | ADMIN |
| PUT | `/speakers/{id}` | ADMIN |
| DELETE | `/speakers/{id}` | ADMIN |

---

### Eventos

| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/events` | Público |
| GET | `/events/{id}` | Público |
| POST | `/events` | ADMIN |
| PUT | `/events/{id}` | ADMIN |
| DELETE | `/events/{id}` | ADMIN |

---

### Talleres

| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/workshops` | Público |
| GET | `/workshops/{id}` | Público |
| POST | `/workshops` | ADMIN |
| PUT | `/workshops/{id}` | ADMIN |
| DELETE | `/workshops/{id}` | ADMIN |

---

### Programas

| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/programs` | Público |
| GET | `/programs/{id}` | Público |
| POST | `/programs` | ADMIN |
| PUT | `/programs/{id}` | ADMIN |
| DELETE | `/programs/{id}` | ADMIN |

---

### Inscripciones a talleres

| Método | Endpoint | Acceso |
|---|---|---|
| POST | `/registrations` | Usuario autenticado |
| GET | `/registrations` | ADMIN |
| GET | `/registrations/{id}` | ADMIN |
| PUT | `/registrations/{id}` | ADMIN |
| DELETE | `/registrations/{id}` | ADMIN |

---

### Inscripciones a programas

| Método | Endpoint | Acceso |
|---|---|---|
| POST | `/program-registrations` | Usuario autenticado |
| GET | `/program-registrations` | ADMIN |
| GET | `/program-registrations/{id}` | ADMIN |
| PUT | `/program-registrations/{id}` | ADMIN |
| DELETE | `/program-registrations/{id}` | ADMIN |

---

### Mensajes de contacto

| Método | Endpoint | Acceso |
|---|---|---|
| POST | `/contact-messages` | Público |
| GET | `/contact-messages` | ADMIN |
| GET | `/contact-messages/{id}` | ADMIN |
| DELETE | `/contact-messages/{id}` | ADMIN |

---

### Calendario administrativo

| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/admin-calendar` | ADMIN |
| GET | `/admin-calendar/{id}` | ADMIN |
| POST | `/admin-calendar` | ADMIN |
| PUT | `/admin-calendar/{id}` | ADMIN |
| DELETE | `/admin-calendar/{id}` | ADMIN |

---

## Filtros

Varios endpoints GET permiten aplicar filtros mediante parámetros de consulta. Algunos ejemplos:

```http
GET /users?username=ana&email=test&active=true
GET /events?title=charla&location=zaragoza&isPublic=true
GET /workshops?name=escritura&isOnline=false&speakerId=1
GET /programs?name=emo&isOnline=false&speakerId=2
GET /speakers?firstName=cristina&lastName=garcia&available=true
GET /registrations?userId=2&workshopId=1&isPaid=true
```

---

## Lógica de negocio destacada

### Talleres y programas

Los talleres y programas pueden tener los siguientes estados:

```text
PENDING
CONFIRMED
CANCELLED
```

Reglas principales:

- Una actividad confirmada permite inscripciones confirmadas si hay plazas disponibles.
- Una actividad pendiente permite registrar inscripciones pendientes hasta alcanzar el mínimo de participantes.
- Una actividad cancelada no debería admitir nuevas inscripciones.
- La fecha límite de confirmación se utiliza cuando la actividad está pendiente de alcanzar el número mínimo de participantes.
- La capacidad máxima controla el número total de plazas disponibles.

---

### Inscripciones

Las inscripciones incluyen:

- usuario;
- taller o programa asociado;
- fecha de inscripción;
- código de confirmación;
- número de plazas;
- estado de inscripción;
- estado de pago;
- importe abonado;
- valoración opcional.

Reglas principales:

- Un usuario no puede inscribirse dos veces a la misma actividad.
- Se controla la capacidad máxima antes de confirmar una inscripción.
- El número de plazas por inscripción está limitado.
- El sistema genera automáticamente códigos de confirmación.
- El estado de pago puede ser `PENDING` o `PAID`.

---

### Envío de emails

El backend incluye integración con correo electrónico para notificar al usuario en determinados flujos:

- confirmación de inscripción;
- cambios de estado de talleres;
- cambios de estado de programas;
- cancelaciones.

La configuración SMTP se define en `application.properties` mediante variables de entorno para evitar guardar credenciales sensibles en el repositorio.

---

## Configuración local

El archivo `application.properties` utiliza valores por defecto para entorno local y variables de entorno para entornos externos:

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mariadb://localhost:3307/enajenarte_db}
spring.datasource.username=${MARIADB_USER:enajenarte_user}
spring.datasource.password=${MARIADB_PASSWORD:}

spring.mail.username=${MAIL_USERNAME:}
spring.mail.password=${MAIL_PASSWORD:}
app.mail.from=${MAIL_FROM:}
app.contact.to=${CONTACT_TO:}

app.jwt.secret=${JWT_SECRET:}
```

---

## Base de datos local

El proyecto utiliza MariaDB. Si se ejecuta mediante Docker Compose, se levanta un contenedor de MariaDB en el puerto local `3307`.

Ejemplo de variables necesarias:

```env
MARIADB_USER=enajenarte_user
MARIADB_PASSWORD=
MARIADB_ROOT_PASSWORD=root
MARIADB_DATABASE=enajenarte_db
```

---

## Script de datos de prueba

El proyecto incluye un script de datos de prueba en:

```text
src/main/resources/db/test-data.sql
```

Este script está pensado para facilitar la revisión del proyecto por parte del evaluador.

Importante:

```text
El script elimina e inserta datos de prueba.
No debe ejecutarse sobre una base de datos con información real de enajenArte.
Uso recomendado: crear una base separada llamada enajenarte_test_db.
```

Orden recomendado:

```sql
CREATE DATABASE IF NOT EXISTS enajenarte_test_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

Después se debe arrancar el backend apuntando temporalmente a `enajenarte_test_db` para que Hibernate cree las tablas y, finalmente, ejecutar el script SQL.

---

## Ejecución del backend

Instalar dependencias y compilar:

```bash
mvn clean package
```

Ejecutar la aplicación:

```bash
mvn spring-boot:run
```

La API queda disponible en:

```text
http://localhost:8080
```

---

## Compilación y validación

El proyecto se valida principalmente mediante compilación del backend y pruebas manuales de endpoints con Postman.

Compilar el proyecto:

```bash
mvn clean package
```

---

## Pruebas rápidas

Endpoints públicos:

```http
GET http://localhost:8080/events
GET http://localhost:8080/workshops
GET http://localhost:8080/programs
GET http://localhost:8080/speakers
```

Login:

```http
POST http://localhost:8080/auth/login
```

Body:

```json
{
  "username": "admin",
  "password": "123456"
}
```

---

## Gestión de errores

La API utiliza un manejador global de excepciones para devolver respuestas estructuradas ante errores de validación, recursos no encontrados, conflictos de negocio o errores internos.

Ejemplo:

```json
{
  "code": 404,
  "title": "not-found",
  "message": "The workshop does not exist",
  "errors": {}
}
```

Códigos habituales:

| Código | Uso |
|---|---|
| 400 | Petición incorrecta o validación fallida |
| 401 | Usuario no autenticado |
| 403 | Usuario autenticado sin permisos suficientes |
| 404 | Recurso no encontrado |
| 409 | Conflicto de negocio, si se aplica |
| 500 | Error interno del servidor |

---

## Despliegue

Durante el proyecto se realizaron pruebas de despliegue en AWS utilizando:

- Amazon RDS MariaDB;
- Elastic Beanstalk;
- variables de entorno;
- empaquetado del backend mediante `.jar`.

El despliegue permitió validar la configuración general del entorno, aunque quedó documentada una incidencia de comportamiento en algunas rutas públicas del backend desplegado respecto al comportamiento local del mismo `.jar`.

Como línea futura, se plantea completar un despliegue estable mediante:

- EC2 con Docker Compose;
- backend Spring Boot;
- frontend React servido por Nginx;
- MariaDB en contenedor.

---

## Estado del proyecto

El backend incluye las funcionalidades principales necesarias para la gestión de la plataforma:

- autenticación;
- roles;
- CRUD administrativo;
- talleres;
- programas;
- eventos;
- inscripciones;
- mensajes de contacto;
- calendario administrativo;
- emails;
- filtros;
- script de datos de prueba.

Quedan como posibles mejoras futuras:

- bloqueo de fechas para evitar asignar actividades a ponentes ocupados;
- despliegue público estable;
- mejora del sistema de documentación API;
- ampliación de métricas o paneles administrativos.