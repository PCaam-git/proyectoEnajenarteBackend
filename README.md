Enajenarte API
Descripción general

Enajenarte API es una API REST académica desarrollada como parte de la asignatura Acceso a Datos, correspondiente al segundo curso del Grado Superior en Desarrollo de Aplicaciones Multiplataforma.

La API proporciona el backend de la empresa Enajenarte, que hasta el momento únicamente contaba con una página web estática. Enajenarte es una empresa centrada en la salud emocional, que trabaja a través de distintos enfoques como la expresión artística, la comunicación oral y el autoconocimiento. Para ello organiza eventos, talleres y formaciones, y necesita un sistema que facilite la gestión de estas actividades y la comunicación con sus clientes.

Esta API permite mostrar los eventos y talleres que la empresa llevará a cabo y gestionar la inscripción de los usuarios en dichas actividades, actuando como base para futuras aplicaciones cliente.

----------------------------
Modelo de dominio

La API se basa en cinco entidades principales, cada una con un propósito bien definido:

Event

Representa los eventos que organiza Enajenarte o en los que participa la empresa o alguno de sus ponentes. Incluye información como título, ubicación, fecha, precio y carácter público del evento.

Speaker

Recoge la información de los ponentes que participan en eventos y talleres, incluyendo su especialidad, experiencia y disponibilidad.

Workshop

Representa los talleres organizados por Enajenarte. Incluye datos como nombre, descripción, fecha, duración, precio, modalidad (online o presencial) y ponente asociado.

User

Almacena la información de los usuarios registrados en la plataforma, que pueden consultar eventos y talleres y realizar inscripciones.

Registration

Relaciona a los usuarios con los talleres a los que se han inscrito, almacenando información adicional como la fecha de inscripción, estado de pago y otros datos asociados al registro.

-----------------------------
Funcionalidad disponible

La API ofrece operaciones CRUD completas para todas las entidades del dominio:

- Crear

- Consultar listado

 - Consultar por identificador

 - Modificar

 - Eliminar

Además, cada entidad dispone de operaciones de filtrado en las peticiones GET, permitiendo aplicar hasta tres filtros diferentes según el recurso.

----------------------------
Gestión de errores

La API gestiona de forma explícita los siguientes tipos de error, devolviendo siempre respuestas estructuradas mediante ErrorResponse:

400 Bad Request

Errores de validación de datos enviados por el cliente.

404 Not Found

Recurso inexistente o relaciones no encontradas.

500 Internal Server Error

Errores internos del servidor.

--------------------------------
Arquitectura del proyecto

El proyecto sigue una arquitectura por capas estricta, lo que permite una separación clara de responsabilidades:

 - Domain: entidades del modelo de datos.

 - Repository: acceso a datos mediante JPA.

 - Service: lógica de negocio.

 - Controller: exposición de la API REST.

 - DTOs: objetos de entrada y salida para la comunicación con el cliente.

 - Exception: gestión centralizada de errores.

Esta estructura facilita el mantenimiento, la comprensión del proyecto y su evaluación académica.


----------------------------------
Tecnologías utilizadas

 - Java 21

 - Spring Boot

 - Maven

 - JPA / Hibernate

 - MariaDB

 - ModelMapper

 - OpenAPI 3.0

 - WireMock

 - Postman


----------------------------------------
Documentación de la API (OpenAPI)

La API está documentada mediante OpenAPI 3.0, que define de forma formal:

 - Endpoints disponibles

 - Métodos HTTP

 - Parámetros de entrada

 - Cuerpos de petición

 - Respuestas y códigos HTTP

 - Esquemas de datos

 - Errores posibles

El fichero OpenAPI refleja fielmente las operaciones implementadas en los controladoresA partir de esta especificación 
se han generado las colecciones Postman incluidas en el proyecto, garantizando la coherencia entre API real, WireMock y pruebas.


-----------------------------------------
API Mock (WireMock)

l proyecto incluye un API Mock implementado con WireMock, que permite simular el comportamiento de la API real sin necesidad de ejecutar el backend completo.

El proyecto WireMock se encuentra dentro del repositorio, en la carpeta:
enajenarte/wiremock/enajenarteWiremock

Puesta en marcha de WireMock

WireMock se ejecuta como un servicio independiente en el puerto 8089. No se trata de un proyecto Spring Boot ni Maven, por lo que no se ejecuta mediante mvn spring-boot:run.

Para arrancar WireMock es necesario ejecutar el JAR standalone incluido en el proyecto desde la carpeta correspondiente, indicando el puerto y la ubicación de los stubs.

java -jar .\wiremock-standalone-3.13.2.jar --port 8089 --root-dir .


Una vez iniciado, WireMock expone los endpoints simulados definidos en las carpetas mappings/ y __files/.

Estructura del mock

mappings/: define las rutas simuladas y las condiciones de cada endpoint.

__files/: contiene las respuestas JSON asociadas a cada mapping.

Las rutas están definidas utilizando identificadores fijos (por ejemplo 1, 400, 999, 500), lo que garantiza un comportamiento determinista y reproducible durante la ejecución de pruebas automáticas.


-----------------------------------------
Colección Postman

El proyecto incluye dos colecciones Postman, ambas generadas a partir del fichero OpenAPI:

Colección Postman – API real (8080): Enajenarte RUNNER (8080).postman_collection.json

Esta colección está orientada a la prueba manual de la API real, que se ejecuta mediante Spring Boot en el puerto 8080. Permite verificar el comportamiento real de los endpoints contra la base de datos.

Colección Postman – WireMock (8089): Enajenarte RUNNER (8089).postman_collection.json

Esta colección está orientada a la ejecución automática con Postman Runner, apuntando al mock WireMock en el puerto 8089. Está diseñada para validar de forma reproducible todas las operaciones y códigos de respuesta definidos.

Las colecciones se encuentran en la carpeta:

enajenarte/postman/
Ejecución con Postman Runner

Para ejecutar la colección de WireMock con Postman Runner:

Abrir Postman y seleccionar Runner.

Elegir la colección correspondiente a WireMock (8089).

Verificar el uso de variables de colección, como baseUrl e identificadores parametrizados.

Ejecutar una iteración del Runner.

El resultado esperado es la ejecución completa de la colección sin errores, validando los distintos casos de éxito y error definidos en el mock.


---------------------------------------------
Tests unitarios

Se han desarrollado tests unitarios para las capas Service y Controller, cubriendo los principales flujos de la aplicación.

El proyecto cuenta con aproximadamente 132 tests unitarios, que verifican:

 - Casos de éxito

 - Errores 400 y 404

 - Funcionamiento de filtros

 - Operaciones CRUD completas

 - Ejecución del proyecto

La API se ejecuta por defecto en el puerto 8080.


--------------------------------------------
**Ejecución del proyecto

La API real se ejecuta por defecto en el puerto 8080 mediante Spring Boot.

El API Mock (WireMock) se ejecuta de forma independiente en el puerto 8089. Ambos entornos son independientes y pueden utilizarse de forma separada según el tipo de prueba que se desee realizar.**
Comandos básicos:

mvn clean compile
mvn test
mvn spring-boot:run


---------------------------------------------
Conclusión

Enajenarte API constituye un backend completo para la gestión de eventos y talleres de una empresa dedicada a la salud emocional. El proyecto cumple los requisitos académicos establecidos, presenta una arquitectura clara y dispone de documentación, pruebas y herramientas de validación que permiten demostrar su correcto funcionamiento y defendibilidad ante evaluación docente.