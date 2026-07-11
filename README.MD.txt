# MercanovaDuoc — Proyecto Semestral de Arquitectura de Microservicios

Sistema de e-commerce basado en microservicios para la gestión de clientes, catálogo de productos, pedidos, pagos, envíos e inventario.

## Estudiante

- Ismael Sánchez

## Descripción del dominio

Mercanova es una plataforma de comercio electrónico compuesta por 10 microservicios independientes y un API Gateway como punto de entrada único. Cada microservicio administra su propio dominio de negocio (clientes, productos, categorías, proveedores, descuentos, pedidos, envíos, carrito, pagos e inventario), comunicándose entre sí vía REST cuando es necesario validar información de otro dominio (por ejemplo, `carrito` verifica que el cliente y el producto existan antes de agregar un item).

## Microservicios implementados

| Microservicio | Puerto local | Descripción |
|---|---|---|
| `gateway` | 8080 | API Gateway (Spring Cloud Gateway Server WebMVC) — punto de entrada único |
| `clientes` | 8081 | Gestión de clientes |
| `productos` | 8082 | Catálogo de productos |
| `categorias` | 8083 | Categorías de productos |
| `proveedores` | 8084 | Proveedores |
| `descuentos` | 8085 | Códigos de descuento |
| `pedidos` | 8086 | Pedidos (verifica cliente y producto) |
| `envios` | 8087 | Envíos (verifica pedido) |
| `carrito` | 8088 | Carrito de compras (verifica cliente y producto) |
| `pago` | 8089 | Pagos (verifica pedido, aplica descuentos) |
| `inventario` | 8090 | Control de stock (verifica producto) |

## Arquitectura

- **Patrón CSR** (Controller–Service–Repository/Model) en los 10 microservicios.
- **Persistencia:** Spring Data JPA + Hibernate, con base de datos MySQL alojada en **Aiven** (online, no local ni H2).
- **Comunicación entre microservicios:** WebClient (Spring WebFlux), con manejo de errores remotos y validación de existencia antes de operar.
- **API Gateway:** Spring Cloud Gateway Server WebMVC, con rutas declaradas mediante la Java Routes API (`GatewayRoutesConfig.java`), enrutando cada prefijo `/api/{recurso}/**` al microservicio correspondiente usando resolución de nombres de Docker Compose.
- **Documentación:** Swagger/OpenAPI (springdoc) en los 10 microservicios, con anotaciones `@Operation`, `@ApiResponses` y ejemplos JSON.
- **Pruebas unitarias:** JUnit 5 + Mockito en los 10 microservicios, cubriendo lógica de negocio, casos de éxito/error y comunicación WebClient mockeada.
- **Contenerización:** Docker (Dockerfile multi-stage por microservicio) + Docker Compose para orquestar el ecosistema completo.

## Rutas principales del Gateway

Todas las peticiones pasan por `http://localhost:8080`:

```
/api/clientes/**      → clientes:8081
/api/productos/**     → productos:8082
/api/categorias/**    → categorias:8083
/api/proveedores/**   → proveedores:8084
/api/descuentos/**    → descuentos:8085
/api/pedidos/**       → pedidos:8086
/api/envios/**        → envios:8087
/api/carrito/**       → carrito:8088
/api/pagos/**         → pago:8089
/api/inventario/**    → inventario:8090
```

## Base de datos

Todas las bases de datos están alojadas en un servicio MySQL de **Aiven** (online), una base de datos por microservicio:
`db_clientes`, `db_productos`, `db_categorias`, `db_proveedores`, `db_descuentos`, `db_pedidos`, `db_envios`, `db_carrito`, `db_pago`, `db_inventario`.

La conexión requiere SSL (`?ssl-mode=REQUIRED` en la URL JDBC).

## Documentación Swagger / OpenAPI

Con el proyecto corriendo (local o Docker), la documentación interactiva de cada microservicio está disponible en:

```
http://localhost:8081/swagger-ui.html   (clientes)
http://localhost:8082/swagger-ui.html   (productos)
http://localhost:8083/swagger-ui.html   (categorias)
http://localhost:8084/swagger-ui.html   (proveedores)
http://localhost:8085/swagger-ui.html   (descuentos)
http://localhost:8086/swagger-ui.html   (pedidos)
http://localhost:8087/swagger-ui.html   (envios)
http://localhost:8088/swagger-ui.html   (carrito)
http://localhost:8089/swagger-ui.html   (pago)
http://localhost:8090/swagger-ui.html   (inventario)
```

## Instrucciones de ejecución

### Requisitos previos
- Java 21
- Maven (o el wrapper `mvnw` incluido en cada microservicio)
- Docker Desktop
- Credenciales de la base de datos Aiven (host, puerto, usuario, password)

### Ejecución local (Docker Compose — recomendado)

1. Clonar el repositorio.
2. En la raíz del proyecto, completar el archivo `docker-compose.yml` con las credenciales reales de Aiven (reemplazar `TU_PASSWORD` en cada servicio).
3. Levantar todo el ecosistema:
   ```bash
   docker-compose up --build
   ```
4. Verificar que los 11 contenedores estén corriendo:
   ```bash
   docker-compose ps
   ```
5. Probar el Gateway:
   ```
   GET http://localhost:8080/api/clientes
   ```

### Ejecución local sin Docker (desde IntelliJ)

1. Configurar cada `application.yml` con las credenciales de Aiven (o de un MySQL local).
2. Levantar los microservicios en este orden: primero los independientes (`clientes`, `productos`, `categorias`, `proveedores`, `descuentos`), luego los dependientes (`pedidos`, `envios`, `carrito`, `pago`, `inventario`), y al final el `gateway`.
3. Cada microservicio corre en su puerto individual (ver tabla arriba).

### Pruebas unitarias

Cada microservicio incluye pruebas con JUnit 5 y Mockito. Para ejecutarlas:
```bash
cd <microservicio>
mvnw test
```

## Despliegue remoto

El API Gateway está desplegado en **Render** mediante Docker:
- **API Gateway (Render):** https://mercanoval-gateway.onrender.com

La base de datos está alojada de forma remota en **Aiven** (MySQL), tanto para el entorno local como para el desplegado.

## Notas técnicas relevantes

- El Gateway utiliza la **Java Routes API** de Spring Cloud Gateway Server WebMVC en vez de configuración YAML, debido a un problema de binding de propiedades detectado en la versión 5.0.2 del starter.
- Las URLs de comunicación entre microservicios se inyectan vía `@Value` con valores por defecto a `localhost` (para desarrollo sin Docker) y se sobreescriben con variables de entorno en `docker-compose.yml` para producción.
