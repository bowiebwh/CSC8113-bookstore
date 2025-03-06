# Bookstore Microservices

This repository contains a microservices-based bookstore application, including backend services for catalog and cart management, a frontend React application, and Kubernetes deployment configurations.

## Prerequisites

Ensure that you have the following installed on your local machine:

- Java 11 (Required for Spring Boot applications)
- Maven 3.5+ (For building backend services)
- Node.js & npm (For running the frontend application)
- PostgreSQL (Database setup)
- Docker & Kubernetes (For containerization and deployment)

## Database Setup

Before running the application, set up a PostgreSQL database:

1. Install PostgreSQL.
2. Create a new database named `bookstore`.
3. Use the following credentials:
    - **Username:** `postgres`
    - **Password:** `csc8113`

## Running the Application Locally

### Backend Services

Each backend service is a Spring Boot application. You need to build and run them separately.

1. **Catalog Service**
   ```sh
   cd backend/catalog-service
   mvn clean install
   mvn spring-boot:run
   ```

2. **Cart Service**
   ```sh
   cd backend/cart-service
   mvn clean install
   mvn spring-boot:run
   ```

### Frontend Application

1. Navigate to the frontend directory:
   ```sh
   cd frontend
   ```
2. Install dependencies:
   ```sh
   npm install
   ```
3. Start the development server:
   ```sh
   npm start
   ```

### Configuration for Production Deployment

To switch from development to production mode:

1. Update the `application.properties` file in each backend service:
    - Change `dev` to `prod`
    - Update database URLs and credentials to match AWS services

2. Update the frontend `.env` file to point to the production backend endpoints.

## Directory Structure

```plaintext
bookstore-microservices/
│── frontend/                        # React frontend application
│   ├── src/                         # Source code
│   ├── public/                      # Public assets
│   ├── package.json                 # npm dependencies
│   ├── Dockerfile                    # Docker configuration
│   ├── .env                          # Environment variables
│
│── backend/                         # Spring Boot microservices
│   ├── catalog-service/             # Catalog microservice
│   │   ├── src/                      # Java source code
│   │   ├── Dockerfile                # Docker configuration
│   │   ├── deployment/               # Kubernetes deployment files
│   │   ├── pom.xml                   # Maven configuration
│
│   ├── cart-service/                # Cart microservice
│   │   ├── src/                      # Java source code
│   │   ├── Dockerfile                # Docker configuration
│   │   ├── deployment/               # Kubernetes deployment files
│   │   ├── pom.xml                   # Maven configuration
│
│── database/                        # PostgreSQL setup & backups
│   ├── postgres-statefulset.yaml     # Kubernetes deployment
│   ├── backup/                       # Backup scripts
│
│── monitoring/                       # Monitoring & autoscaling
│   ├── hpa.yaml                      # Horizontal Pod Autoscaler
│   ├── k6-load-test.js                # Load testing script
│
│── ci-cd/                            # CI/CD automation
│   ├── github-actions/               # GitHub Actions workflows
│   ├── terraform/                     # Infrastructure as Code
│
│── k8s/                              # Kubernetes deployment files
│   ├── catalog-deployment.yaml       # Catalog microservice deployment
│   ├── cart-deployment.yaml          # Cart microservice deployment
│   ├── postgres-statefulset.yaml     # Database setup
│   ├── api-gateway-deployment.yaml   # API Gateway deployment
│   ├── hpa.yaml                      # Autoscaling setup
│   ├── ingress.yaml                   # API Gateway ingress
│
│── README.md                         # Project documentation
```

## Deployment on AWS

1. **Containerization**
    - Use Docker to build images for each service.
   ```sh
   docker build -t catalog-service backend/catalog-service
   docker build -t cart-service backend/cart-service
   docker build -t bookstore-frontend frontend
   ```

2. **Push Images to AWS ECR**
   ```sh
   aws ecr create-repository --repository-name catalog-service
   aws ecr create-repository --repository-name cart-service
   aws ecr create-repository --repository-name bookstore-frontend
   ```

    - Tag and push images:
   ```sh
   docker tag catalog-service:latest <AWS_ECR_URL>/catalog-service:latest
   docker tag cart-service:latest <AWS_ECR_URL>/cart-service:latest
   docker tag bookstore-frontend:latest <AWS_ECR_URL>/bookstore-frontend:latest

   docker push <AWS_ECR_URL>/catalog-service:latest
   docker push <AWS_ECR_URL>/cart-service:latest
   docker push <AWS_ECR_URL>/bookstore-frontend:latest
   ```

3. **Deploy to Kubernetes**
    - Apply deployment configurations:
   ```sh
   kubectl apply -f k8s/
   ```
    - Enable autoscaling:
   ```sh
   kubectl apply -f monitoring/hpa.yaml
   ```

4. **Verify Deployment**
   ```sh
   kubectl get pods
   kubectl get services
   ```

This completes the setup for running the bookstore microservices both locally and in AWS.

