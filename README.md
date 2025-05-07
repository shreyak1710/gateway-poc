# Gateway Service - AI Customer Support Agents

## 1. Project Overview

-**Project Name:** AI Customer Support Agents (ACSA) - gateway-service
-**Description:** Spring Cloud Gateway service that routes and manages requests to microservices in the AI Customer Support Agents architecture. Provides rate limiting, circuit breaking, security via API key authentication, and fallback capabilities.

The AI Customer Support Agents platform is a comprehensive microservices solution that enables businesses to deploy customized AI-powered customer support agents. The system processes detailed customer registration data, handles secure verification, and generates API keys for authenticated service access.

The Gateway Service acts as the entry point for all client requests, performing critical functions such as request routing, rate limiting, circuit breaking, and security enforcement through API key validation.



## 2. Prerequisites

- **Java Version:** Java 21
- **Spring Boot Version:** 3.0.4
- **Database:** Redis (for rate limiting)
- **Build Tool:** Gradle 8.4
- **Other Tools:** 
  - Spring Cloud Gateway
  - Spring Security
  - Resilience4j (Circuit Breaker)
  - API Key Authentication

## 3. Project Setup

### Clone the Repository:
```bash
git clone <repository-url>
```

### Build the Project:
```bash
./gradlew clean build
```

### Run the Application:
```bash
./gradlew bootRun
```

## 4. Configuration

### Environment Configurations
The application is configured via the `application.yml` file. Key configurations include:

- **Server Port**: `8080`
- **Active Spring Profile**: `default`
- **Application Name**: `gateway-service`
- **Route Configurations** for downstream services:
  - `AUTH_SERVICE_URL=http://localhost:8081`
  - `CUSTOMER_SERVICE_URL=http://localhost:8082`
- **Rate Limiting Settings**:
  - `Replenish Rate`: `10`
  - `Burst Capacity`: `20`
  - `Requested Tokens`: `1`
- **Circuit Breaker Configurations**
- **API Key Validation Settings**
- **CORS Settings**:
  - `Allowed Origins`: `*`
  - `Allowed Methods`: `GET, POST, PUT, DELETE, OPTIONS`
  - `Allowed Headers`: `*`
  - `Allow Credentials`: `true`
  - `Max Age`: `3600` seconds

### Redis Connection Settings
```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

## 5. Directory Structure

```
gateway-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── zapcom/
│   │   │           ├── configuration/       
│   │   │           │   ├── GatewayServiceConfiguration.java
│   │   │           │   └── RateLimiterConfiguration.java
│   │   │           ├── controller/        
│   │   │           │   └── FallbackController.java
│   │   │           ├── exception/       
│   │   │           │   ├── GatewayServiceException.java
│   │   │           │   └── GatewayServiceGlobalExceptionHandler.java
│   │   │           ├── filter/          
│   │   │           │   ├── GatewayServiceRequestLoggingFilter.java
│   │   │           │   ├── GatewayServiceResponseHeadersFilter.java
│   │   │           │   └── JwtAuthenticationFilter.java
│   │   │           ├── model/       
│   │   │           │   ├── request/
│   │   │           │   │   └── GatewayServiceRequest.java
│   │   │           │   └── response/
│   │   │           │       ├── GatewayServiceErrorResponse.java
│   │   │           │       └── GatewayServiceResponse.java
│   │   │           ├── utils/           
│   │   │           │   ├── GatewayServiceJwtUtils.java
│   │   │           │   ├── GatewayServicePathConstants.java
│   │   │           │   ├── GatewayServiceRequestConstants.java
│   │   │           │   └── GatewayServiceResponseConstants.java
│   │   │           └── GatewayServiceApplication.java
│   │   └── resources/
│   │       └── application.yml            
│   └── test/                               
├── build.gradle                         
├── gradlew
├── README.md                            
└── settings.gradle
```

## 6. API Documentation

Swagger documentation is not directly applicable to API Gateway services as they primarily route to other services. However, you can document the available routes and their purposes.

## 7. API Endpoints

The Gateway Service routes to the following services:

### Authentication Service Routes
- `POST /auth/api/auth/register` - Register new customer profiles
- `POST /auth/api/auth/verify-email` - Verify email for API key generation
- `GET /auth/api/auth/generate-api-key` - Generate API key after email verification
- `POST /auth/api/auth/validate-api-key` - Validate API key

### Customer Service Routes
- `GET /api/customers` - List all customers (Requires API key)
- `GET /api/customers/{id}` - Get customer by ID (Requires API key)
- `POST /api/customers` - Create new customer profile (Requires API key)
- `PUT /api/customers/{id}` - Update customer profile (Requires API key)
- `DELETE /api/customers/{id}` - Delete customer profile (Requires API key)

### Fallback Routes
- `GET /fallback/auth` - Fallback for authentication service
- `GET /fallback/customers` - Fallback for customer service

## 8. Error Handling

The Gateway Service implements centralized error handling through:

1. **Circuit Breakers**: When downstream services fail, the circuit breaker trips and redirects to fallback endpoints.
2. **Fallback Controllers**: Provides meaningful error responses when services are unavailable.
3. **API Key Validation**: Returns appropriate error messages for invalid or expired API keys.

### Common Error Responses:

- **503 Service Unavailable**: When a service is down or not responding
- **429 Too Many Requests**: When rate limits are exceeded
- **401 Unauthorized**: When API key authentication fails
- **403 Forbidden**: When permission is denied for the provided API key
- **400 Bad Request**: When the request format is invalid

## 9. Testing

### Run Tests:
```bash
./gradlew test
```

### Test Categories:
- Unit Tests: Test individual components, including API key validation
- Integration Tests: Test routing and filter behavior

## 10. Deployment Instructions

### Local Deployment
1. Ensure Redis is running locally
2. Run with development profile:
   ```
   ./gradlew bootRun --args='--spring.profiles.active=dev'
   ```

### QA Environment
1. Build the application:
   ```
   ./gradlew build
   ```
2. Deploy the JAR file to the QA server
3. Run with QA profile:
   ```
   java -jar OptimusAPI_Gateway_service-0.0.1-SNAPSHOT.jar --spring.profiles.active=qa
   ```

### Production Environment
1. Build the application:
   ```
   ./gradlew build
   ```
2. Deploy the JAR file to production servers
3. Run with production profile and appropriate memory settings:
   ```
   java -Xms1G -Xmx2G -jar OptimusAPI_Gateway_service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
   ```
   
## 11. API Key Authentication Flow

The system uses API key-based authentication that follows this flow:

1. Customer submits registration details via the Authentication Service
2. System validates customer information and sends a verification email
3. Customer verifies email through the verification link
4. Upon successful verification, an API key is generated and stored in both Authentication and Customer Service databases
5. All subsequent API requests must include this API key in the header
6. Gateway Service validates the API key before routing requests to microservices

### Customer Registration Request Format

The registration endpoint accepts a comprehensive JSON payload containing:

- Customer Profile (name, industry, business details)
- Legal and Tax Compliance information
- Banking Details
- Administrative Contact Information
- API Configuration preferences
- Branding elements
- Chatbot configuration
- Terms agreement

### API Key Header Format

After obtaining an API key, all requests must include:

```
X-API-Key: {generated-api-key}
```
