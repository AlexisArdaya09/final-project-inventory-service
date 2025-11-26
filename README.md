# Inventory Service - Microservicio de Gestión de Inventario

Microservicio de gestión de inventario para el sistema de e-commerce. Este servicio administra el stock de productos, consume eventos de órdenes desde Kafka, valida disponibilidad y publica eventos de confirmación o cancelación.

## 📋 Propósito

El **Inventory Service** es uno de los tres microservicios que componen el sistema de e-commerce. Su responsabilidad principal es:

- Gestionar el inventario de productos (CRUD completo)
- Consumir eventos `ecommerce.orders.placed` desde Kafka
- Validar disponibilidad de stock para órdenes
- Reservar stock cuando hay disponibilidad
- Publicar eventos `ecommerce.orders.confirmed` o `ecommerce.orders.cancelled` según el resultado
- Publicar eventos `ecommerce.inventory.updated` cuando cambia el stock

## 🏗️ Arquitectura del Sistema

```
┌──────────────────┐
│ Inventory Service│
│   (Puerto 8083)  │
└────────┬─────────┘
         │
         ├───> PostgreSQL (ecommerce_inventory)
         │
         ├───> Kafka Consumer (ecommerce.orders.placed)
         │
         └───> Kafka Producer
                ├───> ecommerce.orders.confirmed
                ├───> ecommerce.orders.cancelled
                └───> ecommerce.inventory.updated
```

### Relación con otros Microservicios

- **Product Service**: Proporciona información de productos (referencia lógica mediante `productId`)
- **Order Service**: Publica `ecommerce.orders.placed`, consume `ecommerce.orders.confirmed` y `ecommerce.orders.cancelled`
- **Inventory Service** (este): Consume órdenes, gestiona stock, publica confirmaciones/cancelaciones

## 🛠️ Stack Tecnológico

- **Java**: 17.0.9
- **Spring Boot**: 3.5.7
- **Spring Data JPA**: Para persistencia
- **PostgreSQL**: Base de datos relacional
- **Apache Kafka**: Sistema de mensajería
- **Spring Kafka**: Integración con Kafka
- **Bean Validation**: Validación de datos
- **Maven**: Gestión de dependencias

## 📦 Estructura del Proyecto

```
src/main/java/com/alexisardaya/inventoryservice/
├── controller/          # Controladores REST
│   └── InventoryController.java
├── service/            # Lógica de negocio
│   └── InventoryService.java
├── repository/         # Acceso a datos
│   └── InventoryRepository.java
├── model/              # Entidades JPA
│   └── InventoryItem.java
├── dto/                # Objetos de transferencia
│   ├── InventoryItemRequest.java
│   ├── InventoryItemResponse.java
│   └── ErrorResponse.java
├── exception/          # Manejo de excepciones
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── InventoryItemAlreadyExistsException.java
└── kafka/              # Integración con Kafka
    ├── event/
    │   ├── OrderPlacedEvent.java
    │   ├── OrderConfirmedEvent.java
    │   ├── OrderCancelledEvent.java
    │   └── InventoryUpdatedEvent.java
    ├── consumer/
    │   └── OrderEventConsumer.java
    └── producer/
        └── InventoryEventProducer.java
```

## ✅ Requisitos Previos

- **Java 17.0.9** o superior
- **Maven 3.9.11+**
- **PostgreSQL 15+** (o usar Docker Compose)
- **Apache Kafka** (o usar Docker Compose)
- **Docker** y **Docker Compose** (opcional, recomendado)

## 🚀 Pasos para Compilar y Ejecutar

### 1. Clonar el Repositorio

```bash
git clone <url-del-repositorio>
cd final-project-inventory-service
```

### 2. Verificar que los servicios de Base de Datos y Kafka estén funcionando

```bash
# Verificar que los contenedores estén corriendo
docker ps
```

#### Opción B: Instalación Manual

- **PostgreSQL**: Crear base de datos `ecommerce_inventory`
- **Kafka**: Configurar y ejecutar Kafka en `localhost:9092`

### 3. Crear Base de Datos

Si usas Docker Compose, la base de datos se crea automáticamente. Si no:

#### Opción A: Usar el script SQL proporcionado

```bash
# Ejecutar el script SQL
psql -U postgres -f scripts/setup-database.sql
```

#### Opción B: Crear manualmente

```bash
# Conectar a PostgreSQL
psql -U postgres

# Crear base de datos
CREATE DATABASE ecommerce_inventory;

# Crear usuario (opcional)
CREATE USER ecommerce_user WITH PASSWORD 'ecommerce_password';
GRANT ALL PRIVILEGES ON DATABASE ecommerce_inventory TO ecommerce_user;
```

### 4. Crear Topics de Kafka

#### Opción A: Usar el script proporcionado (Recomendado)

```bash
# Copiar el script al contenedor y ejecutarlo
docker cp scripts/create-kafka-topics.sh kafka:/tmp/
docker exec -it kafka bash /tmp/create-kafka-topics.sh
```

#### Opción B: Crear manualmente

```bash
# Entrar al contenedor de Kafka
docker exec -it kafka bash

# Crear los topics necesarios
kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic ecommerce.orders.placed \
  --partitions 5 \
  --replication-factor 1

kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic ecommerce.orders.confirmed \
  --partitions 5 \
  --replication-factor 1

kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic ecommerce.orders.cancelled \
  --partitions 5 \
  --replication-factor 1

# Verificar que se crearon
kafka-topics --list --bootstrap-server localhost:9092
```

**Nota**: Si `KAFKA_AUTO_CREATE_TOPICS_ENABLE` está en `true` (como en nuestro docker-compose.yml), los topics se crean automáticamente al publicar el primer mensaje. Sin embargo, es recomendable crearlos explícitamente para tener control sobre las particiones y replicación.

### 5. Configurar Variables de Entorno

En la carpeta `src/main/resources` encontrarás los archivos de configuración `.yml` que corresponden a los perfiles de Spring Boot:

- **`application.yml`**: Configuración base (perfil por defecto)
- **`application-dev.yml`**: Perfil de desarrollo
- **`application-prod.yml`**: Perfil de producción

Estos archivos contienen las variables de ambiente configuradas para cada perfil. Puedes modificar los valores directamente en estos archivos o usar variables de entorno del sistema, que tienen prioridad sobre los valores definidos en los `.yml`.

### 6. Configurar Variables de Entorno en IntelliJ IDEA (Opcional)

Si utilizas IntelliJ IDEA para ejecutar el proyecto, puedes configurar las variables de entorno directamente en la configuración de ejecución:

1. Ve a **Run** → **Edit Configurations...**
2. Selecciona tu configuración de Spring Boot o crea una nueva
3. En la sección **Environment variables**, haz clic en el ícono de carpeta
4. Agrega las siguientes variables de entorno:

| Name | Value |
|------|-------|
| `DB_HOST` | `localhost` |
| `DB_PORT` | `5432` |
| `DB_NAME` | `ecommerce_inventory` |
| `DB_USER` | `ecommerce_user` |
| `DB_PASSWORD` | `ecommerce_password` |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` |
| `SERVER_PORT` | `8083` |

**Nota**: Estos valores son ejemplos. Ajusta los valores según tu configuración local.

### 7. Compilar el Proyecto

```bash
mvn clean install
```

### 8. Ejecutar el Servicio

#### Perfil por defecto (desarrollo con create-drop):

```bash
mvn spring-boot:run
```

#### Perfil de desarrollo (update):

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Perfil de producción (validate):

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

El servicio estará disponible en: `http://localhost:8083`

### 9. Verificar que el Servicio Está Corriendo

```bash
# Health check
curl http://localhost:8083/actuator/health

# Info del servicio
curl http://localhost:8083/actuator/info
```

## 📡 Endpoints de la API

### Inventario

| Método | Endpoint | Descripción | Body |
|--------|----------|-------------|------|
| `GET` | `/api/inventory` | Listar todos los items de inventario | - |
| `GET` | `/api/inventory/{id}` | Obtener un item de inventario por ID | - |
| `GET` | `/api/inventory/product/{productId}` | Obtener un item de inventario por productId | - |
| `POST` | `/api/inventory` | Crear un nuevo item de inventario | `InventoryItemRequest` |
| `DELETE` | `/api/inventory/{id}` | Eliminar un item de inventario | - |

### Ejemplos de Request

#### Crear Item de Inventario

```json
POST /api/inventory
{
  "productId": 1,
  "productName": "Laptop Dell XPS 15",
  "initialStock": 50
}
```

### Respuestas de Error

El servicio utiliza un `GlobalExceptionHandler` que devuelve respuestas estructuradas y consistentes para todos los errores. Todas las respuestas de error siguen el formato:

```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 404,
  "code": "RESOURCE_NOT_FOUND",
  "message": "Item de inventario 1 no encontrado",
  "path": "/api/inventory/1"
}
```

#### Códigos de Error Disponibles

| Código | Status | Descripción | Ejemplo |
|--------|--------|-------------|---------|
| `RESOURCE_NOT_FOUND` | 404 | Recurso no encontrado | Item de inventario no existe |
| `VALIDATION_ERROR` | 400 | Error de validación | Campos inválidos o faltantes |
| `INVENTORY_ITEM_EXISTS` | 409 | Item duplicado | Intento de crear item existente |
| `DATA_INTEGRITY_ERROR` | 400 | Error de integridad de datos | Violación de constraints de BD |
| `MISSING_PARAMETER` | 400 | Parámetro requerido faltante | Parámetro de query/path faltante |
| `TYPE_MISMATCH` | 400 | Tipo de parámetro incorrecto | ID debe ser numérico |
| `INVALID_REQUEST_BODY` | 400 | Cuerpo de petición inválido | JSON mal formado |
| `INTERNAL_SERVER_ERROR` | 500 | Error interno del servidor | Error no manejado |

#### Ejemplos de Respuestas de Error

**Recurso no encontrado (404):**
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 404,
  "code": "RESOURCE_NOT_FOUND",
  "message": "Item de inventario 999 no encontrado",
  "path": "/api/inventory/999"
}
```

**Error de validación (400):**
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "productId: El ID del producto es obligatorio; initialStock: El stock inicial no puede ser negativo",
  "path": "/api/inventory"
}
```

**Item duplicado (409):**
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 409,
  "code": "INVENTORY_ITEM_EXISTS",
  "message": "Ya existe un item de inventario para el productId: 1",
  "path": "/api/inventory"
}
```

## 🔄 Flujo de Kafka

### Eventos Consumidos

El servicio consume eventos del topic `ecommerce.orders.placed` cuando se crea una nueva orden.

#### Topic: `ecommerce.orders.placed`

- **Particiones**: 5
- **Replicación**: 1
- **Formato**: JSON
- **Consumer Group**: `inventory-service`

#### Estructura del Evento Consumido

```json
{
  "orderId": 1,
  "productId": 1,
  "quantity": 2,
  "customerName": "Juan Pérez",
  "customerEmail": "juan@example.com",
  "totalAmount": 2599.98,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Eventos Publicados

El servicio publica eventos a tres topics diferentes según el resultado del procesamiento:

#### 1. Topic: `ecommerce.orders.confirmed`

Publicado cuando hay stock disponible y se reserva exitosamente.

```json
{
  "orderId": 1,
  "productId": 1,
  "quantity": 2,
  "availableStock": 48,
  "reservedStock": 2,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### 2. Topic: `ecommerce.orders.cancelled`

Publicado cuando no hay stock disponible o ocurre un error.

```json
{
  "orderId": 1,
  "productId": 1,
  "quantity": 2,
  "availableStock": 0,
  "reason": "Insufficient stock",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### 3. Topic: `ecommerce.inventory.updated`

Publicado cuando el stock cambia (después de reservar o liberar).

```json
{
  "productId": 1,
  "availableStock": 48,
  "reservedStock": 2,
  "totalStock": 50,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Flujo Completo

```
1. Order Service → Publica ecommerce.orders.placed
2. Inventory Service → Consume el evento
3. Inventory Service → Valida stock disponible
4a. SI HAY STOCK:
    - Reserva stock en BD
    - Publica ecommerce.orders.confirmed
    - Publica ecommerce.inventory.updated
4b. SI NO HAY STOCK:
    - Publica ecommerce.orders.cancelled
5. Order Service → Consume confirmación/cancelación
```

### Verificar Eventos en Kafka

```bash
# Consumir mensajes del topic de órdenes colocadas
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic ecommerce.orders.placed \
  --from-beginning

# Consumir mensajes del topic de órdenes confirmadas
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic ecommerce.orders.confirmed \
  --from-beginning

# Consumir mensajes del topic de órdenes canceladas
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic ecommerce.orders.cancelled \
  --from-beginning

# Consumir mensajes del topic de inventario actualizado
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic ecommerce.inventory.updated \
  --from-beginning
```

## 🗄️ Modelo de Datos

### Entidad: InventoryItem

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| `id` | Long | Identificador único | PK, Auto-increment |
| `product_id` | Long | ID del producto (FK lógico) | NOT NULL, UNIQUE |
| `product_name` | String(255) | Nombre del producto | NOT NULL |
| `available_stock` | Integer | Stock disponible | NOT NULL, >= 0 |
| `reserved_stock` | Integer | Stock reservado | NOT NULL, >= 0 |
| `created_at` | Instant | Fecha de creación | NOT NULL |
| `updated_at` | Instant | Fecha de actualización | NOT NULL |

### Métodos de Negocio

La entidad `InventoryItem` incluye métodos de negocio:

- `hasAvailableStock(Integer quantity)`: Verifica si hay stock suficiente
- `reserveStock(Integer quantity)`: Reserva stock (mueve de disponible a reservado)
- `releaseStock(Integer quantity)`: Libera stock (mueve de reservado a disponible)
- `getTotalStock()`: Calcula el stock total (disponible + reservado)

### Diagrama de Relaciones

```
┌──────────────────┐
│  InventoryItem   │
├──────────────────┤
│ id (PK)          │
│ product_id (UK)  │ (FK lógico a product-service)
│ product_name     │
│ available_stock  │
│ reserved_stock   │
│ created_at       │
│ updated_at       │
└──────────────────┘
```

**Nota**: La relación con `Product` es lógica (mediante `productId`), no física, ya que cada microservicio tiene su propia base de datos.

## ⚙️ Configuración

### Variables de Entorno

El servicio utiliza variables de entorno con valores por defecto:

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `DB_HOST` | Host de PostgreSQL | `localhost` |
| `DB_PORT` | Puerto de PostgreSQL | `5432` |
| `DB_NAME` | Nombre de la base de datos | `ecommerce_inventory` |
| `DB_USER` | Usuario de PostgreSQL | `ecommerce_user` |
| `DB_PASSWORD` | Contraseña de PostgreSQL | `ecommerce_password` |
| `KAFKA_BOOTSTRAP_SERVERS` | Servidores de Kafka | `localhost:9092` |
| `SERVER_PORT` | Puerto del servicio | `8083` |

### Perfiles de Spring

- **default**: `ddl-auto=create-drop`, logging DEBUG, SQL visible
- **dev**: `ddl-auto=update`, logging DEBUG, SQL visible
- **prod**: `ddl-auto=validate`, logging INFO, SQL oculto

### Validaciones

Las validaciones se configuran en `ValidationMessages.properties`:

- `inventory.productId.notnull`: El ID del producto es obligatorio
- `inventory.productName.notblank`: El nombre del producto es obligatorio
- `inventory.initialStock.notnull`: El stock inicial es obligatorio
- `inventory.initialStock.min`: El stock inicial no puede ser negativo

## 📚 Colección de Postman

La colección de Postman está disponible en la carpeta `postman/` del repositorio.

**Importar en Postman:**
1. Abre Postman
2. Click en "Import"
3. Selecciona el archivo `postman/InventoryService.postman_collection.json`
4. Configura la variable de entorno `baseUrl` con `http://localhost:8083`

**Endpoints incluidos:**
- Crear item de inventario
- Listar todos los items
- Obtener item por ID
- Obtener item por productId
- Eliminar item
- Casos de prueba con validaciones y errores

## 📊 Actuator Endpoints

El servicio expone endpoints de Spring Boot Actuator:

- `/actuator/health`: Estado de salud del servicio
- `/actuator/info`: Información del servicio
- `/actuator/metrics`: Métricas del servicio
- `/actuator/env`: Variables de entorno

## 🔍 Troubleshooting

### Error: "Cannot connect to database"

- Verifica que PostgreSQL esté corriendo: `docker ps`
- Verifica las credenciales en `application.yml`
- Verifica que la base de datos exista

### Error: "Kafka bootstrap servers not available"

- Verifica que Kafka esté corriendo: `docker ps`
- Verifica la configuración en `application.yml`
- Verifica que el topic exista o que `auto.create.topics.enable=true`

### Error: "Topic not found"

- Crea el topic manualmente (ver sección "Crear Topics de Kafka")
- O verifica que `KAFKA_AUTO_CREATE_TOPICS_ENABLE=true` en docker-compose.yml

### Error: "No se procesan eventos de Kafka"

- Verifica que el consumer group esté configurado correctamente
- Verifica que el topic `ecommerce.orders.placed` exista
- Revisa los logs del servicio para ver errores de deserialización
- Verifica que el type mapping esté configurado correctamente en `application.yml`

## 📝 Notas Adicionales

- El servicio utiliza JPA con Hibernate para el mapeo objeto-relacional
- Los eventos de Kafka se consumen y publican de forma asíncrona
- Las validaciones se aplican tanto a nivel de DTO como de base de datos
- El `GlobalExceptionHandler` maneja todas las excepciones y devuelve respuestas estructuradas
- El servicio mantiene un stock reservado separado del disponible para manejar órdenes en proceso

## 🔗 Enlaces Útiles

- [Postman Collection](./postman/InventoryService.postman_collection.json)

## 👤 Autor

Alexis Ardaya

## 📄 Licencia

Este proyecto es parte del curso de Spring Boot y Kafka.
