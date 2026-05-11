# MercaNova - Sistema de Microservicios

## Descripción
MercaNova es un sistema de comercio electrónico basado en arquitectura de microservicios desarrollado con Spring Boot.

## Integrantes
- Ismael Sánchez

## Microservicios
| Microservicio | Puerto | Descripción |
|---|---|---|
| Clientes | 8081 | Gestión de clientes |
| Productos | 8082 | Catálogo de productos |
| Categorías | 8083 | Categorías de productos |
| Proveedores | 8084 | Gestión de proveedores |
| Descuentos | 8085 | Códigos de descuento |
| Pedidos | 8086 | Gestión de pedidos |
| Envíos | 8087 | Seguimiento de envíos |
| Carrito | 8088 | Carrito de compras |
| Pago | 8089 | Procesamiento de pagos |
| Inventario | 8090 | Control de inventario |

## Tecnologías
- Java 21
- Spring Boot 4.0.6
- MySQL
- JPA/Hibernate
- WebClient
- Maven

## Requisitos previos
- Java 21
- MySQL (Laragon)
- IntelliJ IDEA

## Pasos para ejecutar
1. Clonar el repositorio
2. Crear las bases de datos en MySQL:
```sql
CREATE DATABASE db_clientes;
CREATE DATABASE db_productos;
CREATE DATABASE db_categorias;
CREATE DATABASE db_proveedores;
CREATE DATABASE db_descuentos;
CREATE DATABASE db_pedidos;
CREATE DATABASE db_envios;
CREATE DATABASE db_carrito;
CREATE DATABASE db_pago;
CREATE DATABASE db_inventario;
```
3. Abrir cada microservicio en IntelliJ IDEA
4. Ejecutar cada microservicio en el orden indicado:
   - Primero los independientes: Clientes, Productos, Categorías, Proveedores, Descuentos
   - Luego los dependientes: Pedidos, Envíos, Carrito, Pago, Inventario

## Comunicación entre microservicios
- Carrito consulta a Clientes y Productos
- Pedidos consulta a Clientes y Productos
- Envíos consulta a Pedidos
- Pago consulta a Pedidos y Descuentos
- Inventario consulta a Productos