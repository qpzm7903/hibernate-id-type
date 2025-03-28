# Hibernate Custom ID Type Demo

This project demonstrates how to implement a custom ID type called "Identifier" in Hibernate with Spring Boot 3. 
The Identifier type adapts between different underlying types (Long or String) at runtime, with proper serialization/deserialization for different database systems and JSON conversion.

## Project Structure

The project follows the standard Spring Boot application structure:

```
src/
├── main/
│   ├── java/
│   │   └── com/example/idtypedemo/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── domain/
│   │       │   ├── entities/
│   │       │   └── Identifier.java (coming in Story 2)
│   │       ├── repository/
│   │       ├── service/
│   │       ├── type/ (coming in Story 3)
│   │       └── IdTypeDemoApplication.java
│   └── resources/
│       ├── application.properties
│       ├── application-h2.properties
│       ├── application-mysql.properties
│       └── application-postgresql.properties
└── test/
    └── java/
        └── com/example/idtypedemo/
```

## Features

- Custom `Identifier` type that can be either a Long or String based on runtime conditions
- Integration with Hibernate's type system
- Support for MySQL, PostgreSQL, and H2 databases
- JSON serialization/deserialization using Jackson
- Spring Data JPA repositories with the custom ID type

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose (for running MySQL and PostgreSQL)

## Running the Application

### Using H2 (In-Memory Database)

```
mvn spring-boot:run
```

### Using MySQL

```
# Start MySQL container
docker-compose up -d mysql

# Run the application with MySQL profile
mvn spring-boot:run -Dspring.profiles.active=mysql
```

### Using PostgreSQL

```
# Start PostgreSQL container
docker-compose up -d postgres

# Run the application with PostgreSQL profile
mvn spring-boot:run -Dspring.profiles.active=postgresql
```

## Running Tests

```
mvn test
```

## License

This project is licensed under the MIT License. 