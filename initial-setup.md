
# Agile Cloud Router - Microservices Setup Guide

This document provides instructions for setting up and running the microservices-based application with Spring Cloud Gateway and Eureka service discovery.

## System Requirements

- Java 21 (LTS)
- Gradle 8.4
- MongoDB 6.0.x (LTS)
- IntelliJ IDEA 2023.3.1
- Postman (for API testing)

## Technologies Used

- Spring Boot 3.0.4
- Spring Cloud 2022.x
- Spring Cloud API Gateway
- Spring Cloud Netflix Eureka
- Spring Security with JWT
- MongoDB for data storage
- Resilience4j for circuit breaking

## Project Structure

The project consists of four microservices:

1. **Eureka Server (Port: 8761)** - Service Discovery
2. **API Gateway (Port: 8080)** - Gateway with routing, authentication, etc.
3. **Auth Service (Port: 8081)** - Authentication and user management
4. **Customer Service (Port: 8082)** - Customer data management

## Setting Up the Project in IntelliJ IDEA

1. **Clone/Download the Repository**

2. **Import the Project**
   - Open IntelliJ IDEA
   - Select "File" > "Open"
   - Navigate to the project directory and select it
   - Choose "Open as Project"
   - Make sure to select the option to use the Gradle wrapper

3. **Configure Gradle**
   - IntelliJ should automatically detect the Gradle configuration
   - Wait for the Gradle sync to complete
   - If prompted, select Java 21 as the JDK

4. **Set up MongoDB**
   - Install MongoDB (version 6.0.x) if not already installed
   - Start the MongoDB service
   - The application is configured to connect to MongoDB at localhost:27017
   - The following databases will be created automatically:
     - `authdb` for Auth Service
     - `customerdb` for Customer Service

## Running the Application

The services should be started in the following order:

1. **Start Eureka Server**
   - Run the `EurekaServerApplication` class in the `eureka-server` module
   - Wait until it's fully started (check for "Started EurekaServerApplication" message)
   - Verify Eureka is running by visiting http://localhost:8761

2. **Start Auth Service**
   - Run the `AuthServiceApplication` class in the `auth-service` module
   - Wait until it's registered with Eureka

3. **Start Customer Service**
   - Run the `CustomerServiceApplication` class in the `customer-service` module
   - Wait until it's registered with Eureka

4. **Start Gateway Service**
   - Run the `ApiGatewayServiceApplication` class in the `gateway-service` module
   - Wait until it's registered with Eureka

All services should now be running and registered with Eureka. You can check the Eureka dashboard at http://localhost:8761 to confirm.

## API Endpoints and Request Flow

### Authentication Flow

1. **Register a New User**
   - **Endpoint**: `POST /api/auth/register`
   - **URL**: http://localhost:8080/api/auth/register
   - **Request Body**:
     ```json
     {
       "username": "testuser",
       "password": "password123",
       "email": "testuser@example.com",
       "fullName": "Test User"
     }
     ```
   - **Response**: JWT tokens and user information

2. **Login**
   - **Endpoint**: `POST /api/auth/login`
   - **URL**: http://localhost:8080/api/auth/login
   - **Request Body**:
     ```json
     {
       "username": "testuser",
       "password": "password123"
     }
     ```
   - **Response**: JWT tokens and user information
   - **Example Response**:
     ```json
     {
       "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
       "refreshToken": "a41e8339-4e19-4e90-8cdd-6dc52e33811b",
       "type": "Bearer",
       "userId": "60f1a9b3c1e57e001c3a7d82",
       "username": "testuser",
       "roles": ["USER"]
     }
     ```

3. **Refresh Token**
   - **Endpoint**: `POST /api/auth/token/refresh`
   - **URL**: http://localhost:8080/api/auth/token/refresh
   - **Request Body**:
     ```json
     {
       "refreshToken": "a41e8339-4e19-4e90-8cdd-6dc52e33811b"
     }
     ```
   - **Response**: New JWT tokens

### Customer Service Flow (Protected Endpoints)

For all Customer Service endpoints, you need to include the Authorization header with the JWT token:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

1. **Get All Customers**
   - **Endpoint**: `GET /api/customers`
   - **URL**: http://localhost:8080/api/customers
   - **Headers**: Authorization Bearer token

2. **Get Customer by ID**
   - **Endpoint**: `GET /api/customers/{id}`
   - **URL**: http://localhost:8080/api/customers/{id}
   - **Headers**: Authorization Bearer token

3. **Create Customer**
   - **Endpoint**: `POST /api/customers`
   - **URL**: http://localhost:8080/api/customers
   - **Headers**: Authorization Bearer token
   - **Request Body**:
     ```json
     {
       "name": "John Doe",
       "email": "john.doe@example.com",
       "phone": "123-456-7890",
       "address": "123 Main St, City, Country"
     }
     ```

4. **Update Customer**
   - **Endpoint**: `PUT /api/customers/{id}`
   - **URL**: http://localhost:8080/api/customers/{id}
   - **Headers**: Authorization Bearer token
   - **Request Body**:
     ```json
     {
       "name": "John Doe Updated",
       "email": "john.updated@example.com",
       "phone": "123-456-7890",
       "address": "123 Main St, City, Country"
     }
     ```

5. **Delete Customer**
   - **Endpoint**: `DELETE /api/customers/{id}`
   - **URL**: http://localhost:8080/api/customers/{id}
   - **Headers**: Authorization Bearer token

## Gateway Features

The Gateway Service implements several advanced features:

1. **Path Rewriting**
   - Rewrites `/api/auth/**` to `/auth/**` for Auth Service
   - Rewrites `/api/customers/**` to `/customers/**` for Customer Service

2. **JWT Authentication**
   - Validates JWT tokens for protected endpoints
   - Passes user ID and roles to downstream services in headers

3. **Rate Limiting**
   - Limits API requests based on client IP
   - Configured for 10 requests per second with burst capacity of 20

4. **Circuit Breaker (Resilience4j)**
   - Prevents cascading failures
   - Falls back to a default response when services are unavailable
   - Configured with a 5-second timeout

5. **CORS Configuration**
   - Allows cross-origin requests
   - Configurable for specific origins in production

6. **Request Logging**
   - Logs incoming requests with tracking IDs
   - Measures and logs response times

7. **Response Transformation**
   - Adds security headers to responses
   - Ensures consistent response format

## Troubleshooting

1. **Service Not Registering with Eureka**
   - Check if Eureka Server is running
   - Verify the `defaultZone` URL in application.yml files
   - Ensure network connectivity between services

2. **Authentication Failures**
   - Check JWT token expiration (default 1 hour)
   - Verify the token format in the Authorization header
   - Ensure the secret key is consistent

3. **MongoDB Connection Issues**
   - Verify MongoDB is running on localhost:27017
   - Check MongoDB logs for connection errors
   - Ensure no authentication is required (or configure if needed)

4. **Circuit Breaker Triggering**
   - Check if downstream services are available
   - Review Resilience4j configuration for appropriate thresholds
   - Check fallback responses

## Advanced Configuration

For production deployment, consider:
1. Configuring environment-specific properties
2. Securing MongoDB with authentication
3. Adjusting rate limiting and circuit breaker parameters
4. Implementing distributed tracing
5. Setting up centralized logging
