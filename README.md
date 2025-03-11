# EzraLendingApp

EzraLendingApp is a simplified Java-based lending application built with Spring Boot. It automates loan management processes such as product creation, loan disbursement, repayment handling, and notifications. The application demonstrates a robust domain model, RESTful API design, scheduled tasks, and automated database migrations using Liquibase. It's designed with flexibility, efficiency, and user engagement in mind.

## Features

### Product Module:
- Create, update, list, and delete loan products with detailed fee configurations (service fees, daily fees, late fees, and configurable days after due).

### Loan Management:
- Supports both single lump sum and installment-based loans.
- Calculates individual due dates based on origination date and tenure.
- Manages loan states (OPEN, CLOSED, CANCELLED, OVERDUE, WRITTEN_OFF).
- Processes repayments with dynamic allocation to fees, interest, and principal.

### Customer Profile Module:
- Manage comprehensive customer details and configure loan limits based on creditworthiness.

### Notification Module:
- Event-driven notifications for events like loan creation, due reminders, repayment acknowledgments, and overdue notices.
- Customizable communication channels (Email, SMS, Push).
- Configurable notification rules with template management and variable substitution.

### Scheduled Tasks:
- Sweep job for overdue loans: Automatically marks loans overdue, applies late fees, and sends notifications.
- Due date reminder job: Sends reminders for loans due the next day.

### Database Migrations:
- Manage all schema changes with Liquibase YAML changelogs.

## Architecture Overview

The application is organized as a microservice-style monolith that exposes RESTful endpoints and uses Spring Boot's built-in scheduling, validation, and exception handling. Key layers include:

- **Controller Layer**: REST API endpoints for products, loans, customers, and notifications.
- **Service Layer**: Business logic including loan processing, notification triggers, and scheduled tasks.
- **Repository Layer**: Persistence using Spring Data JPA.
- **Notification Module**: Handles sending notifications via multiple channels.
- **Liquibase Integration**: Automates database schema creation and migrations.

## Technology Stack

- Java 17
- Spring Boot
- Spring Data JPA
- Liquibase
- PostgreSQL
- Docker & Docker Compose
- JUnit 5, Mockito, Spring Boot Test

## Prerequisites

- Java 17 or later installed
- Maven (or Gradle) installed
- Docker and Docker Compose (if using containerized deployment)
- PostgreSQL (if not using Docker)

## Setup and Running

### Local (Maven)
1. Clone the Repository:
```bash
git clone https://github.com/yourusername/EzraLendingApp.git
cd EzraLendingApp
```

2. Build the Application:
```bash
mvn clean package
```

3. Run the Application:
```bash
mvn spring-boot:run
```
The application will start on port 8080 by default.

### Using Docker Compose
1. Ensure Docker and Docker Compose are installed.
2. Build and Run Containers:
```bash
docker-compose up --build
```
- The Spring Boot application container exposes port 8080.
- PostgreSQL container is set up with the environment variable `POSTGRES_DB=lending` and maps container port 5432 to host port 5433.


## API Endpoints

### Product Endpoints
- `POST /api/products`: Create a new product.
- `GET /api/products/{id}`: Retrieve product details.
- `GET /api/products`: List all products.
- `PUT /api/products/{id}`: Update product details.
- `DELETE /api/products/{id}`: Delete a product.

### Loan Endpoints
- `POST /api/loans`: Create a new loan.
- `GET /api/loans/{id}`: Retrieve loan details.
- `POST /api/loans/{loanId}/repayments`: Process a loan repayment.

### Customer Endpoints
- `POST /api/customers`: Create a new customer.
- `GET /api/customers/{id}`: Retrieve customer details.
- `PUT /api/customers/{id}`: Update customer profile.
- `POST /api/customers/{customerId}/loan-limits`: Set a loan limit for a customer.

### Notification Template Endpoints
- `POST /api/notification-templates`: Create a new notification template.
- `GET /api/notification-templates`: List all notification templates.

## Database Migrations

Database Migrations

EzraLendingApp uses Liquibase to manage database schema changes.
All migrations are defined in YAML changelogs located in the `src/main/resources/db/changelog` directory. 
To run migrations, simply start the application, and Liquibase will automatically apply any pending changes.
