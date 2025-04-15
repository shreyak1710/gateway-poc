
# Gateway POC

A Spring Boot microservices application with API Gateway, Authentication, and Customer services.

## Overview

This project demonstrates a modern microservices architecture with:

- Spring Cloud Gateway with advanced routing features
- Service discovery with Netflix Eureka
- Authentication and authorization with JWT
- MongoDB persistence
- Circuit breaking with Resilience4j
- Rate limiting and request/response transformation

## Services

- **Eureka Server**: Service discovery (port 8761)
- **Gateway Service**: API Gateway with advanced features (port 8080)
- **Auth Service**: Authentication and user management (port 8081)
- **Customer Service**: Customer data management (port 8082)

## Getting Started

See [initial-setup.md](initial-setup.md) for detailed setup instructions and API documentation.

## Technologies Used

- Java 21 (LTS)
- Spring Boot 3.0.4
- Gradle 8.4
- MongoDB 6.0.x (LTS)
- Spring Cloud 2022.x
- Spring Cloud API Gateway
- JWT for authentication
- Resilience4j for circuit breaking

## Architecture

The Gateway Service implements:
- Path Rewriting
- Rate Limiting
- Circuit Breaker (with Resilience4j)
- Authentication & Authorization (JWT)
- CORS configuration
- Request/Response transformation
