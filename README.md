# User Tickets Services

API REST para la gestión de usuarios y tickets desarrollada con Spring Boot 3.5.7 y Java 21.

## 📋 Requisitos

- Java 21
- Maven 3.9+
- IntelliJ IDEA (recomendado)

## 🚀 Ejecución Local

### 1. Clonar el repositorio
```bash
git clone https://github.com/sneicast/user-tickets-services.git
cd user-tickets-services
```

### 2. Compilar el proyecto
```bash
mvn clean install
```

### 3. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

O desde IntelliJ IDEA:
- Abrir el proyecto
- Ejecutar la clase `UserTicketsApplication`

La aplicación se levantará en: `http://localhost:8080`

## 🐳 Ejecución con Docker

### Construir la imagen
```bash
docker build -t user-tickets-api:latest .
```

### Ejecutar el contenedor
```bash
docker run -d -p 8080:8080 --name user-tickets-api user-tickets-api:latest
```

### Detener el contenedor
```bash
docker stop user-tickets-api
```

### Eliminar el contenedor
```bash
docker rm user-tickets-api
```

## 📚 Documentación API

### Swagger UI
Una vez levantado el proyecto, acceder a:
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html

### Postman Collection
Importar la colección de Postman: [User-Tickets.postman_collection.json](./User-Tickets.postman_collection.json)

## 🛠️ Tecnologías y Componentes

### Framework
- Spring Boot 3.5.7
- Java 21

### Base de Datos
- **H2 Database** (en memoria)
- **H2 Console**: http://localhost:8080/h2-console
  - URL: `jdbc:h2:mem:testdb`
  - Usuario: `sa`
  - Password: _(vacío)_

### Cache
- **Caffeine** (cache en memoria)

### Otros
- Spring Data JPA
- MapStruct (mapeo de DTOs)
- Lombok
- Spring Validation
- SpringDoc OpenAPI

## 🧪 Ejecutar Tests

```bash
mvn clean test
```

## 📄 Arquitectura

El proyecto sigue una arquitectura hexagonal con las siguientes capas:
- **Domain**: Entidades y lógica de negocio
- **Application**: Servicios y casos de uso
- **Infrastructure**: Repositorios y acceso a datos
- **Adapter**: Controladores REST y DTOs

