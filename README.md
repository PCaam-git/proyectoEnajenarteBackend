# enajenArte API

API REST desarrollada como backend del proyecto **enajenArte**, una plataforma orientada a la gestión de talleres, programas, eventos, inscripciones y comunicación con usuarios dentro del ámbito de la creatividad, el bienestar emocional y la expresión artística.

El proyecto forma parte del Trabajo Intermodular del ciclo de Desarrollo de Aplicaciones Multiplataforma. El objetivo del proyecto es transformar la presencia digital de enajenArte en una aplicación dinámica, con zona pública, área de usuario, panel de administración y persistencia de datos.

---

## Tecnologías utilizadas

| Tecnología      | Uso principal                                      |
|-----------------|----------------------------------------------------|
| Java 21         | Lenguaje principal del backend                     |
| Spring Boot     | Framework principal para construir la API REST     |
| Spring Web      | Exposición de endpoints HTTP                       |
| Spring Data JPA | Persistencia y acceso a base de datos              |
| Hibernate       | ORM para el mapeo entidad-tabla                    |
| MariaDB         | Base de datos relacional                           |
| Maven           | Gestión de dependencias y compilación              |
| Spring Security | Control de acceso por roles                        |
| JWT             | Autenticación mediante token                       |
| ModelMapper     | Conversión entre entidades y DTOs                  |
| Spring Mail     | Envío de correos electrónicos                      |
| Docker          | Contenerización del backend                        |
| Docker Compose  | Despliegue conjunto de MariaDB, backend y frontend |

---

## Arquitectura del proyecto

El backend sigue una arquitectura por capas:

```
domain        → Entidades JPA y enums
repository    → Interfaces de acceso a datos
service       → Lógica de negocio
controller    → Endpoints REST
dto           → Objetos de entrada y salida
exception     → Excepciones personalizadas y manejador global
config        → Configuración general, seguridad y JWT
security      → Filtros y utilidades de autenticación
```

Esta estructura separa la lógica del proyecto en partes claras y facilita el mantenimiento del código.

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

```
POST /auth/login
```

Body:

```
{
  "username": "admin",
  "password": "123456"
}
```

La respuesta devuelve un token JWT. Para acceder a rutas protegidas, debe enviarse en la cabecera:

```
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

El backend incluye filtros en varios endpoints GET mediante parámetros de consulta.

Estos filtros forman parte de la API y pueden probarse desde herramientas como Postman o realizando peticiones HTTP directamente. En la versión actual del frontend no se han añadido controles visuales específicos para todos estos filtros.

Ejemplos de uso en la API:

```
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

Los talleres y programas pueden tener tres estados:

```
PENDING
CONFIRMED
CANCELLED
```

Reglas principales:

- Una actividad confirmada permite inscripciones confirmadas si hay plazas disponibles.
- Una actividad pendiente permite registrar inscripciones pendientes hasta alcanzar el mínimo de participantes.
- Una actividad cancelada no debe admitir nuevas inscripciones.
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

El backend incluye integración con correo electrónico para notificar al usuario en algunos flujos, como:

- confirmación de inscripción;
- cambios de estado;
- cancelaciones.

La configuración SMTP se define en `application.properties` mediante variables de entorno para evitar guardar datos sensibles en el repositorio.

---

## Configuración local

El entorno local utiliza el archivo `application.properties`

En este entorno, el backend trabaja con la base de datos local: 

```
enajenarte_db
```
La conexión local está configurada para usar MariaDB en: 

```
localhost:3307
```

Este es el entorno utilizado durante el desarrollo

---

## Base de datos local con Docker

El archivo:

```
docker-compose.yml
```
levanta únicamente MariaDB para desarrollo local.

Ejemplo de variables del archivo .env local:

```
MARIADB_USER=enajenarte_user
MARIADB_PASSWORD=
MARIADB_ROOT_PASSWORD=
MARIADB_DATABASE=enajenarte_db
```
Para levantar solo la base de datos local:
````
docker compose up -d
````
La base local queda disponible en:
````
localhost:3307
````

---

## Compilación y ejecución local del backend

Compilar el proyecto:
````
mvn clean package
````

Ejecutar el backend:
````
mvn spring-boot:run
````
La api  queda disponible en:
````
http://localhost:8080
````

---

## Script de datos de prueba

El proyecto incluye un script de datos de prueba en:

```
src/main/resources/db/test-data.sql
```

Este script está pensado para facilitar la revisión del proyecto.

En el despliegue Docker, los datos se cargan automáticamente sobre una base de datos independiente llamada: 
````
enajenarte_test_db
````

Esta base de datos está separada de la base local de desarrollo:
````
enajenarte_db
````

El script incluye datos de prueba para revisar:
- Usuarios
- Ponentes
- Talleres
- Programas
- Eventos
- Inscripciones
- Calendario administrativo

Importante:
El script elimina e inserta datos de prueba. 
No debe ejecutarse sobre una base de datos con información real.
---

## Despliegue completo con Docker

El proyecto puede levantarse completo con Docker usando:
- MariaDB
- backend Spring Boot
- frontend React servidocon Nginx.

### Estructura esperada

Los proyectos del backend y del frontend deben descomprimirse en la misma carpeta.

El archivo docker-compose.tfg.yml está en el backend y construye también el frontend usando una ruta relativa a la carpeta del frontend.

### Preparar variables del entorno

Entrar en la carpeta del backend:

````
cd proyectoEnajenarteBackend-main
````

Crear el archivo .env a partir del ejemplo en .env.tfg.example

### Levantar el proyecto

Desde la carpeta del backend:
````
docker compose -f docker-compose.tfg.yml up --build
````

Cuando el arranque termine, la aplicación estará disponible en:
````
http://localhost:8000
````
La API queda disponible en:
````
http://localhost:8080
````

### Reiniciar el contenedor desde cero

Para borrar los contenedores y el volumen de datos de prueba:
````
docker compose -f docker-compose.tfg.yml down -v
````

Para volver a levantarlo:
````
docker compose -f docker-compose.tfg.yml up --build
````

Este proceso vuelve a crear la base de datos enajenarte_test_db y carga de nuevo los datos de prueba.

### Usuarios de prueba

El entorno de prueba incluye usuarios ya creados:

admin / 123456
usuario1 / 123456

El usuario **admin**  permite acceder al panel de administración.

---
## Colección Postman

El proyecto incluye una colección Postman para revisar manualmente los endpoints principales de la API.

La colección y el entorno se encuentran en la carpeta:

```
postman/
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

La API utiliza un manejador global de excepciones para devolver respuestas claras cuando se produce un error.

Ejemplo:

```
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

Para la entrega final se ha preparado un entorno Docker completo que permite levantar la aplicación con frontend, backend y base de datos desde Docker Compose.

El despliegue Docker utiliza el perfil de producción del backend y una base de datos de prueba independiente. Para evitar problemas con los tipos TEXT de MariaDB generados por Hibernate, en el perfil de producción se desactivó el entrecomillado global de identificadores:

spring.jpa.properties.hibernate.globally_quoted_identifiers=false

---

## Estado del proyecto

El backend incluye las funcionalidades principales necesarias para la gestión de la plataforma:

- autenticación;
- roles de usuario y administrador;
- CRUD administrativo;
- gestión de talleres;
- gestión de programas;
- gestión de eventos;
- gestión de ponentes;
- inscripciones a talleres;
- inscripciones a programas;
- mensajes de contacto;
- calendario administrativo;
- envío de emails;
- filtros en endpoints del backend;
- datos de prueba;
- despliegue Docker completo junto al frontend y MariaDB.

Quedan como posibles mejoras futuras:

- bloqueo de fechas para evitar asignar actividades a ponentes ocupados;
- despliegue público estable;
- mejora del sistema de documentación API;
- ampliación de métricas o paneles administrativos.