# 🚐 Mama Mpoki Car Hire - Fleet Management System

A comprehensive fleet management system for **Mama Mpoki Car Hire**, a private Tanzanian transport company operating in the **Dodoma region**.

## 📋 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Deployment](#deployment)

---

## Overview

Mama Mpoki Car Hire is an **owner-controlled** fleet management system with three main modules:

| Module | Description | Workflow |
|--------|-------------|----------|
| **Special Hire** | Coasters & minibuses for charter | Trip-based (booking → trip → payment) |
| **Daladala** | Route-based public transport | Daily operations (route → revenue/expenses) |
| **Private Cars** | Owner's personal vehicles | Record-keeping (maintenance, insurance, fuel) |

### Key Features

- ✅ **JWT Authentication** with refresh tokens
- ✅ **Rate Limiting** on login (5 attempts/min)
- ✅ **Soft Delete** for all entities
- ✅ **Real-time Dashboard** with alerts
- ✅ **Financial Tracking** in TZS
- ✅ **Document Expiry Alerts**
- ✅ **Swagger/OpenAPI** documentation

---

## Tech Stack

| Component | Technology |
|-----------|------------|
| **Backend** | Java 17, Spring Boot 3.2.5 |
| **Database** | MySQL 8.0 (Production), H2 (Development) |
| **Security** | Spring Security + JWT |
| **ORM** | Spring Data JPA + Hibernate |
| **Migrations** | Flyway |
| **API Docs** | Swagger/OpenAPI 3 |
| **Build** | Maven |
| **Container** | Docker + Docker Compose |

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose (optional)

### Quick Start (Development)

```bash
# Clone the repository
git clone https://github.com/your-repo/mama-mpoki-car-hire.git
cd mama-mpoki-car-hire

# Run with H2 database (no setup needed)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will start on `http://localhost:8080`

### Default Credentials

| Username | Password | Role |
|----------|----------|------|
| `mamampoki` | `MamaMpoki2026!` | Owner |

### Useful Links

| URL | Description |
|-----|-------------|
| http://localhost:8080/swagger-ui.html | Swagger API Docs |
| http://localhost:8080/h2-console | H2 Database Console |
| http://localhost:8080/actuator/health | Health Check |

---

## API Documentation

### Base URL
```
http://localhost:8080/api/v1
```

### Authentication Header
```
Authorization: Bearer <jwt_token>
```

### Standard Response Format

**Success:**
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... }
}
```

**Error:**
```json
{
  "success": false,
  "message": "Error description",
  "errors": [
    { "field": "fieldName", "message": "Validation error" }
  ]
}
```

---

## 📚 API Endpoints

### 1. Authentication (`/auth`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/auth/login` | Login and get tokens | ❌ |
| POST | `/auth/refresh` | Refresh access token | ❌ |
| POST | `/auth/change-password` | Change password | ✅ |
| GET | `/auth/me` | Get current user info | ✅ |

#### POST `/auth/login`
```json
// Request
{
  "username": "mamampoki",
  "password": "MamaMpoki2026!"
}

// Response
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGci...",
    "refreshToken": "eyJhbGci...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "owner": {
      "id": 1,
      "username": "mamampoki",
      "fullName": "Mama Mpoki",
      "phone": "+255712345678"
    }
  }
}
```

---

### 2. Vehicles (`/vehicles`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/vehicles` | List vehicles (filter by moduleType, status) |
| GET | `/vehicles/{id}` | Get vehicle details |
| POST | `/vehicles` | Create vehicle |
| PUT | `/vehicles/{id}` | Update vehicle |
| DELETE | `/vehicles/{id}` | Soft-delete vehicle |
| PUT | `/vehicles/{id}/status` | Update vehicle status |
| GET | `/vehicles/available` | Check availability |
| GET | `/vehicles/fleet-summary` | Get fleet stats |

#### POST `/vehicles`
```json
// Request
{
  "vehicleType": "COASTER",
  "moduleType": "SPECIAL_HIRE",
  "make": "Toyota",
  "model": "HiAce",
  "year": 2022,
  "regNumber": "T 123 ABC",
  "color": "White",
  "capacity": 30,
  "fuelType": "DIESEL"
}

// Response
{
  "success": true,
  "data": {
    "id": 1,
    "vehicleType": "COASTER",
    "moduleType": "SPECIAL_HIRE",
    "regNumber": "T 123 ABC",
    "status": "ACTIVE",
    ...
  }
}
```

---

### 3. Drivers (`/drivers`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/drivers` | List drivers (filter by status) |
| GET | `/drivers/{id}` | Get driver details |
| POST | `/drivers` | Create driver |
| PUT | `/drivers/{id}` | Update driver |
| DELETE | `/drivers/{id}` | Soft-delete driver |
| PUT | `/drivers/{id}/status` | Update status |

#### POST `/drivers`
```json
{
  "fullName": "John Mwakasege",
  "phone": "+255712345678",
  "licenseNumber": "TZ-LIC-001",
  "licenseExpiry": "2027-12-31",
  "dailyRate": 30000
}
```

---

### 4. Conductors (`/conductors`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/conductors` | List conductors |
| GET | `/conductors/{id}` | Get conductor details |
| POST | `/conductors` | Create conductor |
| PUT | `/conductors/{id}` | Update conductor |
| DELETE | `/conductors/{id}` | Soft-delete conductor |

---

### 5. Customers (`/customers`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/customers` | List customers |
| GET | `/customers/{id}` | Get customer details |
| POST | `/customers` | Create customer |
| PUT | `/customers/{id}` | Update customer |
| DELETE | `/customers/{id}` | Soft-delete customer |
| GET | `/customers/search?query=` | Search by name/phone |

---

### 6. Special Hire (`/special-hire`)

#### Bookings

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/special-hire/bookings` | List bookings |
| GET | `/special-hire/bookings/{id}` | Get booking details |
| POST | `/special-hire/bookings` | Create booking |
| PUT | `/special-hire/bookings/{id}` | Update booking |
| PUT | `/special-hire/bookings/{id}/status` | Update status |
| GET | `/special-hire/bookings/{id}/financials` | Get financial summary |

#### POST `/special-hire/bookings`
```json
{
  "vehicleId": 1,
  "customerId": 1,
  "hireDate": "2026-09-01",
  "endDate": "2026-09-03",
  "destination": "Dar es Salaam",
  "tripPurpose": "Corporate event",
  "agreedPrice": 850000,
  "depositPaid": 200000
}
```

#### Trips

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/special-hire/trips` | List trips |
| GET | `/special-hire/trips/{id}` | Get trip details |
| POST | `/special-hire/trips` | Create trip |
| PUT | `/special-hire/trips/{id}/complete` | Complete trip |

#### Trip Expenses

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/special-hire/trips/{tripId}/expenses` | List expenses |
| POST | `/special-hire/trips/{tripId}/expenses` | Add expense |
| DELETE | `/special-hire/trips/{tripId}/expenses/{id}` | Delete expense |

#### Payments

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/special-hire/bookings/{id}/payments` | List payments |
| POST | `/special-hire/bookings/{id}/payments` | Record payment |
| DELETE | `/special-hire/bookings/{id}/payments/{id}` | Delete payment |

#### POST `/special-hire/bookings/{id}/payments`
```json
{
  "amount": 650000,
  "paymentMethod": "MOBILE_MONEY",
  "paymentDate": "2026-09-01",
  "referenceNumber": "MPESA-12345"
}
```

---

### 7. Daladala (`/daladala`)

#### Routes

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/daladala/routes` | List routes |
| GET | `/daladala/routes/{id}` | Get route details |
| POST | `/daladala/routes` | Create route |
| PUT | `/daladala/routes/{id}` | Update route |
| DELETE | `/daladala/routes/{id}` | Delete route |

#### POST `/daladala/routes`
```json
{
  "name": "Dodoma Town - Ihumwa",
  "startPoint": "Dodoma Town Centre",
  "endPoint": "Ihumwa",
  "distanceKm": 25.5,
  "fareAmount": 1500
}
```

#### Daily Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/daladala/operations` | List operations |
| GET | `/daladala/operations/{id}` | Get operation details |
| POST | `/daladala/operations` | Create operation |
| PUT | `/daladala/operations/{id}/complete` | Complete operation |

#### Revenue & Expenses

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/daladala/operations/{id}/revenues` | List revenues |
| POST | `/daladala/operations/{id}/revenues` | Add revenue |
| GET | `/daladala/operations/{id}/expenses` | List expenses |
| POST | `/daladala/operations/{id}/expenses` | Add expense |

---

### 8. Private Cars (`/private-cars`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/private-cars` | List private cars |
| GET | `/private-cars/{id}` | Get car details |
| POST | `/private-cars` | Register car |
| PUT | `/private-cars/{id}` | Update car |
| DELETE | `/private-cars/{id}` | Delete car |
| GET | `/private-cars/{id}/fuel` | Get fuel records |
| POST | `/private-cars/{id}/fuel` | Add fuel record |
| GET | `/private-cars/{id}/maintenance` | Get maintenance records |
| POST | `/private-cars/{id}/maintenance` | Add maintenance record |
| GET | `/private-cars/expiring-docs` | Get expiring documents |

---

### 9. Fuel Records (`/fuel`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/fuel` | List fuel records |
| POST | `/fuel` | Add fuel record |

---

### 10. Maintenance (`/maintenance`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/maintenance` | List maintenance records |
| POST | `/maintenance` | Add maintenance record |

---

### 11. Dashboard (`/dashboard`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/dashboard/summary` | Get full dashboard summary |

#### GET `/dashboard/summary`
```json
{
  "success": true,
  "data": {
    "fleet": {
      "totalVehicles": 8,
      "activeVehicles": 6,
      "inMaintenance": 1,
      "inactive": 1,
      "specialHire": 4,
      "daladala": 3,
      "privateCars": 1
    },
    "specialHire": {
      "pendingBookings": 3,
      "activeTrips": 1,
      "monthlyRevenue": 4500000,
      "monthlyProfit": 2700000
    },
    "daladala": {
      "totalRoutes": 4,
      "activeRoutes": 3,
      "todayOperations": 3,
      "monthlyRevenue": 6075000
    },
    "alerts": [
      {
        "type": "INSURANCE_EXPIRY",
        "severity": "HIGH",
        "message": "Insurance expires in 5 days",
        "vehicleRegNumber": "T 789 STU"
      }
    ],
    "currency": "TZS"
  }
}
```

---

## Project Structure

```
mama-mpoki-car-hire/
├── src/main/java/com/mamampoki/carhire/
│   ├── MamaMpokiApplication.java
│   ├── auth/                    # Authentication
│   ├── owner/                   # Owner entity
│   ├── vehicle/                 # Vehicle management
│   ├── driver/                  # Driver management
│   ├── conductor/               # Conductor management
│   ├── customer/                # Customer management
│   ├── specialhire/             # Special Hire module
│   ├── daladala/                # Daladala module
│   ├── privatecar/              # Private Cars module
│   ├── fuel/                    # Fuel records
│   ├── maintenance/             # Maintenance records
│   ├── dashboard/               # Dashboard
│   ├── security/                # JWT & Security
│   ├── config/                  # Configuration
│   ├── common/                  # Shared classes
│   └── exception/               # Exception handling
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   └── db/migration/            # Flyway migrations
├── docs/                        # Design documents
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

## Database Schema

### Tables

| Table | Description |
|-------|-------------|
| `owner` | System owner (authentication) |
| `vehicle` | All vehicles (shared) |
| `driver` | Driver records |
| `conductor` | Conductor records |
| `customer` | Customer records |
| `hire_booking` | Special hire bookings |
| `trip` | Trip records |
| `trip_expense` | Trip expenses |
| `payment` | Payment records |
| `route` | Daladala routes |
| `daily_operation` | Daily operations |
| `daily_revenue` | Daily revenue |
| `daily_expense` | Daily expenses |
| `fuel_record` | Fuel records |
| `maintenance_record` | Maintenance records |
| `private_car` | Private car details |

### Entity Relationships

```
Owner ──┬── Vehicle ──┬── FuelRecord
        │             ├── MaintenanceRecord
        │             └── PrivateCar
        ├── Driver
        ├── Conductor
        ├── Customer
        ├── HireBooking ──┬── Trip ── TripExpense
        │                 └── Payment
        └── Route ── DailyOperation ──┬── DailyRevenue
                                      └── DailyExpense
```

---

## Deployment

### Docker (Recommended)

```bash
# 1. Clone the repository
git clone https://github.com/your-repo/mama-mpoki-car-hire.git
cd mama-mpoki-car-hire

# 2. Create environment file
cp .env.example .env

# 3. Edit .env with your values
# - JWT_SECRET: Use a strong, unique secret
# - DB_PASSWORD: Use a strong password

# 4. Start services
docker-compose up -d

# 5. Check logs
docker-compose logs -f app

# 6. Access the application
# - API: http://localhost:8080
# - Swagger: http://localhost:8080/swagger-ui.html
```

### Manual Deployment

```bash
# 1. Build the application
mvn clean package -DskipTests

# 2. Run with MySQL
java -jar target/mama-mpoki-car-hire-1.0.0-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --DB_HOST=your-mysql-host \
  --DB_USERNAME=your-db-user \
  --DB_PASSWORD=your-db-password \
  --JWT_SECRET=your-jwt-secret
```

---

## License

This project is proprietary software for Mama Mpoki Car Hire.

---

## Support

For support, contact: info@mamampoki.co.tz
