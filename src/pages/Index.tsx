
import React from 'react';

const Index = () => {
  return (
    <div className="min-h-screen bg-gray-100 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto bg-white p-8 rounded-lg shadow-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-4">Spring Boot Microservices Architecture</h1>
          <p className="text-gray-600">
            This project requires backend setup. Check README.md and initial-setup.md for instructions.
          </p>
        </div>
        
        <div className="prose max-w-none">
          <h2 className="text-xl font-semibold mb-3">Architecture Overview</h2>
          <ul className="list-disc pl-5 mb-6 space-y-2">
            <li><strong>Gateway Service:</strong> Spring Cloud API Gateway with advanced routing</li>
            <li><strong>Auth Service:</strong> JWT-based authentication service</li>
            <li><strong>Customer Service:</strong> Customer data management</li>
            <li><strong>Eureka Server:</strong> Service discovery and registration</li>
          </ul>
          
          <h2 className="text-xl font-semibold mb-3">Gateway Features</h2>
          <ul className="list-disc pl-5 mb-6 space-y-2">
            <li><strong>Path Rewriting:</strong> Maps external URLs to internal service endpoints</li>
            <li><strong>Rate Limiting:</strong> Controls request rate with Redis RateLimiter</li>
            <li><strong>Circuit Breaker:</strong> Implemented with Resilience4j</li>
            <li><strong>JWT Authentication:</strong> Validates tokens and forwards user context</li>
            <li><strong>CORS Configuration:</strong> Cross-origin resource sharing management</li>
          </ul>
          
          <h2 className="text-xl font-semibold mb-3">Technologies</h2>
          <ul className="list-disc pl-5 mb-6 space-y-2">
            <li>Java 21 (LTS)</li>
            <li>Spring Boot 3.0.4</li>
            <li>Gradle 8.4</li>
            <li>MongoDB 6.0.x (LTS)</li>
            <li>Spring Cloud 2022.x</li>
          </ul>
          
          <h2 className="text-xl font-semibold mb-3">Implementation Notes</h2>
          <p className="mb-4">
            This is a backend-only implementation. For full functionality, please follow the setup
            instructions in the project files to run the microservices locally.
          </p>
          <p>
            The project includes complete implementations of all services with proper gradle configuration,
            service discovery, API gateway, and microservices communication.
          </p>
        </div>
      </div>
    </div>
  );
};

export default Index;
