# MAMA MPOKI CAR HIRE — REST API Specifications

## Base URL
```
http://localhost:8080/api/v1
```

## Authentication
All endpoints (except `/auth/login` and `/auth/refresh`) require a valid JWT token in the header:
```
Authorization: Bearer <jwt_token>
```

## Rate Limiting
Login endpoint (`POST /auth/login`) is rate-limited to **5 attempts per minute** per IP. Exceeding this returns HTTP 429.

## Standard Response Format

### Success
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... }
}
```

### Error
```json
{
  "success": false,
  "message": "Error description",
  "errors": [
    { "field": "fieldName", "message": "Validation error detail" }
  ]
}
```

### Paginated
```json
{
  "success": true,
  "data": {
    "content": [ ... ],
    "totalElements": 100,
    "totalPages": 10,
    "currentPage": 0,
    "size": 10
  }
}
```

### Rate Limited
```json
{
  "success": false,
  "message": "Too many login attempts. Please try again in 60 seconds.",
  "retryAfter": 60
}
```

---

## 1. Authentication (`/auth`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/auth/login` | Owner login, returns JWT + refresh token | ❌ |
| POST | `/auth/refresh` | Refresh expired access token | ❌ (requires refresh token) |
| POST | `/auth/change-password` | Change password | ✅ |
| GET | `/auth/me` | Get current owner info | ✅ |

### POST `/auth/login`
**Request:**
```json
{
  "username": "owner_username",
  "password": "secure_password"
}
```
**Response:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "owner": {
      "id": 1,
      "username": "mamampoki",
      "fullName": "Mama Mpoki",
      "phone": "+255XXXXXXXXX"
    }
  }
}
```

### POST `/auth/refresh`
**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```
**Response:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 86400000
  }
}
```

---

## 2. Vehicles (`/vehicles`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/vehicles` | List all vehicles (filterable by moduleType, vehicleType, status) |
| GET | `/vehicles/{id}` | Get vehicle details |
| POST | `/vehicles` | Create new vehicle |
| PUT | `/vehicles/{id}` | Update vehicle |
| DELETE | `/vehicles/{id}` | Soft-delete (set deleted=true) |
| GET | `/vehicles/{id}/fuel` | Get fuel records for vehicle |
| GET | `/vehicles/{id}/maintenance` | Get maintenance records |
| GET | `/vehicles/fleet-summary` | Get fleet summary stats |
| GET | `/vehicles/available` | Check vehicle availability for date range |

> **Note**: `/vehicles/summary` was renamed to `/vehicles/fleet-summary` to avoid conflict with `/vehicles/{id}`.

### GET `/vehicles/available?moduleType=SPECIAL_HIRE&startDate=2026-09-01&endDate=2026-09-03`
**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "vehicleType": "COASTER",
      "regNumber": "T 123 ABC",
      "capacity": 30,
      "status": "ACTIVE",
      "availableFrom": "2026-09-04",
      "availableUntil": "2026-09-15"
    },
    {
      "id": 2,
      "vehicleType": "MINIBUS",
      "regNumber": "T 234 DEF",
      "capacity": 16,
      "status": "ACTIVE",
      "availableFrom": null,
      "availableUntil": null
    }
  ]
}
```

### GET `/vehicles/fleet-summary`
**Response:**
```json
{
  "success": true,
  "data": {
    "total": 8,
    "byModule": {
      "SPECIAL_HIRE": 4,
      "DALADALA": 3,
      "PRIVATE": 1
    },
    "byStatus": {
      "ACTIVE": 6,
      "MAINTENANCE": 1,
      "INACTIVE": 1
    },
    "totalCapacity": 142
  }
}
```

### GET `/vehicles?moduleType=SPECIAL_HIRE&status=ACTIVE&page=0&size=20`
**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "vehicleType": "COASTER",
        "moduleType": "SPECIAL_HIRE",
        "make": "Toyota",
        "model": "HiAce",
        "year": 2022,
        "regNumber": "T 123 ABC",
        "color": "White",
        "capacity": 30,
        "fuelType": "DIESEL",
        "status": "ACTIVE",
        "photoUrl": "/images/vehicles/1.jpg"
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "currentPage": 0,
    "size": 20
  }
}
```

### POST `/vehicles`
**Request:**
```json
{
  "vehicleType": "COASTER",
  "moduleType": "SPECIAL_HIRE",
  "make": "Toyota",
  "model": "HiAce",
  "year": 2022,
  "regNumber": "T 123 ABC",
  "color": "White",
  "capacity": 30,
  "fuelType": "DIESEL",
  "notes": "Main charter vehicle"
}
```

---

## 3. Drivers (`/drivers`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/drivers` | List all drivers (filterable by status) |
| GET | `/drivers/{id}` | Get driver details |
| POST | `/drivers` | Add new driver |
| PUT | `/drivers/{id}` | Update driver |
| DELETE | `/drivers/{id}` | Soft-delete (set deleted=true) |
| GET | `/drivers/{id}/trips` | Get driver's trip history |
| GET | `/drivers/stats` | Get driver performance summary |

### POST `/drivers`
**Request:**
```json
{
  "fullName": "John Mwakasege",
  "phone": "+255712345678",
  "licenseNumber": "TZ-LIC-2024-001",
  "licenseExpiry": "2026-12-31",
  "nationalId": "TZ-NID-12345678",
  "address": "Dodoma, Chang'ombe",
  "dailyRate": 30000.00
}
```

---

## 4. Conductors (`/conductors`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/conductors` | List all conductors (filterable by status) |
| GET | `/conductors/{id}` | Get conductor details |
| POST | `/conductors` | Add new conductor |
| PUT | `/conductors/{id}` | Update conductor |
| DELETE | `/conductors/{id}` | Soft-delete (set deleted=true) |
| GET | `/conductors/{id}/operations` | Get conductor's operation history |
| GET | `/conductors/stats` | Get conductor performance summary |

### POST `/conductors`
**Request:**
```json
{
  "fullName": "Hamisi Juma",
  "phone": "+255756789012",
  "nationalId": "TZ-NID-87654321",
  "address": "Dodoma, Nala",
  "dailyRate": 15000.00
}
```

### GET `/conductors/stats`
**Response:**
```json
{
  "success": true,
  "data": [
    {
      "conductorId": 1,
      "fullName": "Hamisi Juma",
      "totalOperations": 45,
      "totalFareCollected": 675000.00,
      "averageDailyFare": 15000.00,
      "status": "ACTIVE"
    }
  ]
}
```

---

## 5. Customers (`/customers`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/customers` | List all customers (paginated) |
| GET | `/customers/{id}` | Get customer details |
| POST | `/customers` | Add new customer |
| PUT | `/customers/{id}` | Update customer |
| DELETE | `/customers/{id}` | Soft-delete |
| GET | `/customers/{id}/bookings` | Get customer's booking history |
| GET | `/customers/search?query=John` | Search customers by name/phone |

---

## 6. Special Hire (`/special-hire`)

### Bookings

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/special-hire/bookings` | List all bookings (filterable by status, date range) |
| GET | `/special-hire/bookings/{id}` | Get booking details with payments |
| POST | `/special-hire/bookings` | Create new booking |
| PUT | `/special-hire/bookings/{id}` | Update booking |
| PUT | `/special-hire/bookings/{id}/status` | Update booking status |
| GET | `/special-hire/bookings/{id}/financials` | Get booking financial summary |

### POST `/special-hire/bookings`
**Request:**
```json
{
  "vehicleId": 1,
  "customerId": 1,
  "hireDate": "2026-09-01",
  "endDate": "2026-09-03",
  "destination": "Dar es Salaam",
  "tripPurpose": "Corporate event",
  "agreedPrice": 850000.00,
  "depositPaid": 200000.00,
  "notes": "Airport pickup included"
}
```

### GET `/special-hire/bookings/{id}/financials`
**Response:**
```json
{
  "success": true,
  "data": {
    "bookingId": 12,
    "agreedPrice": 850000.00,
    "totalPaid": 650000.00,
    "outstandingBalance": 200000.00,
    "totalTripExpenses": 320000.00,
    "totalProfit": 530000.00,
    "profitMargin": 62.35,
    "paymentStatus": "PARTIALLY_PAID",
    "payments": [
      {
        "id": 1,
        "amount": 200000.00,
        "method": "CASH",
        "date": "2026-08-25",
        "reference": null
      },
      {
        "id": 2,
        "amount": 450000.00,
        "method": "MOBILE_MONEY",
        "date": "2026-09-01",
        "reference": "MPESA-12345"
      }
    ]
  }
}
```

### Trips

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/special-hire/trips` | List all trips (filterable) |
| GET | `/special-hire/trips/{id}` | Get trip details with expenses |
| POST | `/special-hire/trips` | Create new trip (assign driver) |
| PUT | `/special-hire/trips/{id}` | Update trip |
| PUT | `/special-hire/trips/{id}/complete` | Complete a trip (enter odometer, finalize) |
| GET | `/special-hire/trips/{id}/expenses` | List trip expenses |
| POST | `/special-hire/trips/{id}/expenses` | Add trip expense |
| DELETE | `/special-hire/trips/{id}/expenses/{expenseId}` | Remove expense |
| GET | `/special-hire/trips/{id}/profit` | Calculate trip profit/loss |

### Trip Profit Calculation
```json
GET /special-hire/trips/5/profit
Response:
{
  "success": true,
  "data": {
    "tripId": 5,
    "revenue": 850000.00,
    "totalExpenses": 320000.00,
    "breakdown": {
      "fuel": 180000.00,
      "driverAllowance": 90000.00,
      "tolls": 25000.00,
      "food": 25000.00
    },
    "profit": 530000.00,
    "profitMargin": 62.35,
    "currency": "TZS"
  }
}
```

### Payments

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/special-hire/bookings/{id}/payments` | List payments for booking |
| POST | `/special-hire/bookings/{id}/payments` | Record a payment |
| DELETE | `/special-hire/bookings/{id}/payments/{paymentId}` | Remove payment |

### POST `/special-hire/bookings/{id}/payments`
**Request:**
```json
{
  "amount": 650000.00,
  "paymentMethod": "MOBILE_MONEY",
  "paymentDate": "2026-09-01",
  "referenceNumber": "MPESA-12345",
  "notes": "Final payment"
}
```

---

## 7. Daladala (`/daladala`)

### Routes

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/daladala/routes` | List all routes |
| GET | `/daladala/routes/{id}` | Get route details with stats |
| POST | `/daladala/routes` | Create new route |
| PUT | `/daladala/routes/{id}` | Update route |
| DELETE | `/daladala/routes/{id}` | Deactivate route (soft-delete) |

### POST `/daladala/routes`
**Request:**
```json
{
  "name": "Dodoma Town - Ihumwa",
  "startPoint": "Dodoma Town Centre",
  "endPoint": "Ihumwa",
  "distanceKm": 25.5,
  "fareAmount": 1500.00
}
```

### Daily Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/daladala/operations` | List operations (filterable by date, route, vehicle) |
| GET | `/daladala/operations/{id}` | Get operation details with revenues/expenses |
| POST | `/daladala/operations` | Record daily operation |
| PUT | `/daladala/operations/{id}` | Update operation |
| PUT | `/daladala/operations/{id}/complete` | Complete operation (enter final figures) |
| GET | `/daladala/operations/{id}/summary` | Get operation profit/loss summary |

### POST `/daladala/operations`
**Request:**
```json
{
  "vehicleId": 3,
  "routeId": 1,
  "driverId": 2,
  "conductorId": 1,
  "operationDate": "2026-08-24",
  "departureTime": "06:30",
  "totalPassengers": 45
}
```

> **Note**: `conductorId` now references the conductor entity instead of a plain text field.

### Daily Revenue & Expenses

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/daladala/operations/{id}/revenues` | List revenues for operation |
| POST | `/daladala/operations/{id}/revenues` | Add revenue entry |
| DELETE | `/daladala/operations/{id}/revenues/{revenueId}` | Remove revenue entry |
| GET | `/daladala/operations/{id}/expenses` | List expenses for operation |
| POST | `/daladala/operations/{id}/expenses` | Add expense entry |
| DELETE | `/daladala/operations/{id}/expenses/{expenseId}` | Remove expense entry |
| GET | `/daladala/operations/{id}/summary` | Get operation profit/loss summary |

### POST `/daladala/operations/{id}/revenues`
**Request:**
```json
{
  "source": "FARE",
  "amount": 67500.00,
  "description": "45 passengers × 1,500 TZS",
  "revenueDate": "2026-08-24"
}
```

### POST `/daladala/operations/{id}/expenses`
**Request:**
```json
{
  "expenseType": "FUEL",
  "amount": 35000.00,
  "description": "Full tank - Total Dodoma",
  "expenseDate": "2026-08-24"
}
```

### GET `/daladala/operations/{id}/summary`
**Response:**
```json
{
  "success": true,
  "data": {
    "operationId": 15,
    "date": "2026-08-24",
    "route": "Dodoma Town - Ihumwa",
    "vehicle": "T 456 DEF",
    "driver": "John Mwakasege",
    "conductor": "Hamisi Juma",
    "totalPassengers": 45,
    "totalRevenue": 67500.00,
    "totalExpenses": 42000.00,
    "profit": 25500.00,
    "profitMargin": 37.78,
    "currency": "TZS"
  }
}
```

---

## 8. Private Cars (`/private-cars`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/private-cars` | List all private cars |
| GET | `/private-cars/{id}` | Get private car details |
| POST | `/private-cars` | Register private car (creates vehicle + private_car) |
| PUT | `/private-cars/{id}` | Update private car info |
| DELETE | `/private-cars/{id}` | Soft-delete |
| GET | `/private-cars/{id}/fuel` | Get fuel records |
| POST | `/private-cars/{id}/fuel` | Add fuel record |
| GET | `/private-cars/{id}/maintenance` | Get maintenance records |
| POST | `/private-cars/{id}/maintenance` | Add maintenance record |
| GET | `/private-cars/{id}/insurance` | Get insurance details |
| PUT | `/private-cars/{id}/insurance` | Update insurance info |
| GET | `/private-cars/expiring-docs` | Get vehicles with expiring documents (next 30 days) |

---

## 9. Fuel Records (`/fuel`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/fuel` | List all fuel records (filterable by vehicle, date range, paginated) |
| POST | `/fuel` | Add fuel record (validates liters × cost_per_liter = total_cost) |
| PUT | `/fuel/{id}` | Update fuel record |
| DELETE | `/fuel/{id}` | Soft-delete fuel record |
| GET | `/fuel/stats` | Get fuel consumption summary |
| GET | `/fuel/cost-trend` | Get fuel cost trend over time |

### POST `/fuel`
**Request:**
```json
{
  "vehicleId": 1,
  "fuelDate": "2026-08-24",
  "liters": 100.00,
  "costPerLiter": 3500.00,
  "totalCost": 350000.00,
  "odometer": 125000,
  "station": "Total Dodoma",
  "notes": "Full tank"
}
```

> **Validation**: Backend enforces `totalCost == liters × costPerLiter`. If mismatch, returns 400 with error.

---

## 10. Maintenance (`/maintenance`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/maintenance` | List all maintenance records (paginated) |
| GET | `/maintenance/{id}` | Get maintenance record details |
| POST | `/maintenance` | Add maintenance record |
| PUT | `/maintenance/{id}` | Update maintenance record |
| DELETE | `/maintenance/{id}` | Soft-delete |
| GET | `/maintenance/upcoming` | Get upcoming maintenance (next 30 days) |
| GET | `/maintenance/stats` | Get maintenance cost summary |

---

## 11. Reports (`/reports`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/reports/special-hire` | Special hire summary report |
| GET | `/reports/daladala` | Daladala performance report |
| GET | `/reports/vehicle-profitability` | Vehicle profitability comparison |
| GET | `/reports/expenses` | Expense breakdown report |
| GET | `/reports/revenue` | Revenue breakdown report |
| GET | `/reports/monthly-summary` | Monthly P&L summary |
| GET | `/reports/quarterly-summary` | Quarterly P&L summary |

> **All report endpoints require `from` and `to` date parameters.**

### GET `/reports/special-hire?from=2026-08-01&to=2026-08-31`
```json
{
  "success": true,
  "data": {
    "period": { "from": "2026-08-01", "to": "2026-08-31" },
    "totalBookings": 12,
    "completedTrips": 10,
    "totalRevenue": 4500000.00,
    "totalExpenses": 1800000.00,
    "totalProfit": 2700000.00,
    "profitMargin": 60.0,
    "currency": "TZS",
    "topVehicle": {
      "id": 1,
      "regNumber": "T 123 ABC",
      "trips": 6,
      "revenue": 2400000.00
    },
    "topDestination": "Dar es Salaam",
    "averageBookingValue": 375000.00
  }
}
```

### GET `/reports/daladala?from=2026-08-01&to=2026-08-31`
```json
{
  "success": true,
  "data": {
    "period": { "from": "2026-08-01", "to": "2026-08-31" },
    "totalOperations": 90,
    "totalRevenue": 6075000.00,
    "totalExpenses": 3150000.00,
    "totalProfit": 2925000.00,
    "averageDailyRevenue": 202500.00,
    "averageDailyExpense": 105000.00,
    "currency": "TZS",
    "routePerformance": [
      {
        "routeId": 1,
        "routeName": "Dodoma Town - Ihumwa",
        "operations": 30,
        "revenue": 2025000.00,
        "profit": 975000.00
      }
    ],
    "vehiclePerformance": [
      {
        "vehicleId": 3,
        "regNumber": "T 456 DEF",
        "operations": 28,
        "revenue": 1890000.00
      }
    ]
  }
}
```

### GET `/reports/monthly-summary?year=2026&month=8`
```json
{
  "success": true,
  "data": {
    "year": 2026,
    "month": 8,
    "specialHire": {
      "revenue": 4500000.00,
      "expenses": 1800000.00,
      "profit": 2700000.00
    },
    "daladala": {
      "revenue": 6075000.00,
      "expenses": 3150000.00,
      "profit": 2925000.00
    },
    "privateCars": {
      "expenses": 450000.00
    },
    "totalRevenue": 10575000.00,
    "totalExpenses": 5400000.00,
    "netProfit": 5175000.00,
    "currency": "TZS"
  }
}
```

### GET `/reports/quarterly-summary?year=2026&quarter=3`
```json
{
  "success": true,
  "data": {
    "year": 2026,
    "quarter": 3,
    "months": [7, 8, 9],
    "totalRevenue": 31725000.00,
    "totalExpenses": 16200000.00,
    "netProfit": 15525000.00,
    "byModule": {
      "specialHire": { "revenue": 13500000.00, "profit": 8100000.00 },
      "daladala": { "revenue": 18225000.00, "profit": 8775000.00 }
    },
    "currency": "TZS"
  }
}
```

---

## 12. Dashboard (`/dashboard`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/dashboard/summary` | Get overall dashboard summary |
| GET | `/dashboard/fleet-status` | Get fleet status overview |
| GET | `/dashboard/revenue-chart` | Get revenue trend data for charts |
| GET | `/dashboard/recent-activity` | Get recent activities |
| GET | `/dashboard/alerts` | Get alerts (expiring docs, overdue maintenance, etc.) |

### GET `/dashboard/summary`
```json
{
  "success": true,
  "data": {
    "fleet": {
      "totalVehicles": 8,
      "activeVehicles": 6,
      "inMaintenance": 1,
      "inactive": 1
    },
    "specialHire": {
      "pendingBookings": 3,
      "activeTrips": 1,
      "monthlyRevenue": 2700000.00,
      "monthlyProfit": 1620000.00
    },
    "daladala": {
      "totalRoutes": 4,
      "activeRoutes": 3,
      "todayOperations": 3,
      "monthlyRevenue": 4050000.00,
      "monthlyProfit": 1950000.00
    },
    "alerts": [
      {
        "type": "INSURANCE_EXPIRY",
        "severity": "HIGH",
        "message": "T 789 GHI insurance expires in 5 days",
        "vehicleId": 5
      },
      {
        "type": "MAINTENANCE_DUE",
        "severity": "MEDIUM",
        "message": "T 123 ABC service overdue by 500km",
        "vehicleId": 1
      }
    ],
    "currency": "TZS"
  }
}
```

---

## 13. Common Query Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `page` | int | Page number (0-based) |
| `size` | int | Page size (default 20) |
| `sort` | string | Sort field (e.g., `created_at,desc`) |
| `status` | string | Filter by status |
| `from` | date | Start date (YYYY-MM-DD) |
| `to` | date | End date (YYYY-MM-DD) |
| `vehicleId` | long | Filter by vehicle |
| `search` | string | General search term |
| `moduleType` | string | Filter by module (SPECIAL_HIRE, DALADALA, PRIVATE) |

---

## 14. HTTP Status Codes

| Code | Usage |
|------|-------|
| 200 | Success |
| 201 | Created |
| 204 | No Content (successful delete) |
| 400 | Bad Request (validation error) |
| 401 | Unauthorized (no token / invalid token) |
| 403 | Forbidden (not owner) |
| 404 | Resource Not Found |
| 429 | Too Many Requests (rate limited) |
| 500 | Internal Server Error |
