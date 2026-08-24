# MAMA MPOKI CAR HIRE — System Overview & Architecture

## 1. Project Summary

**Mama Mpoki Car Hire** is an owner-controlled fleet management system for a private Tanzanian transport company operating in the **Dodoma region**. The system manages three distinct vehicle categories:

| Module | Description | Workflow Style |
|--------|-------------|----------------|
| **Special Hire** | Coasters & minibuses for hire/charter | Trip-based (booking → trip → payment) |
| **Daladala** | Route-based public transport in Dodoma | Daily operations (route → daily revenue/expenses) |
| **Private Cars** | Owner's personal vehicles | Record-keeping (maintenance, insurance, fuel) |

## 2. Key Design Principles

- **Owner-only access** — No multi-user roles in v1. The owner logs in and manages everything.
- **Drivers, conductors, customers** are data records, not system users.
- **Mobile-friendly dashboard** — The owner likely accesses from a phone or tablet.
- **Offline-resilient** — Tanzanian internet can be unreliable; design for graceful degradation.
- **Soft delete** — Records are never hard-deleted; use `deleted` flag + `deleted_at` timestamp for all entities.
- **Simple first** — Start with core features, expand later.

## 3. Technology Stack

### Backend
| Technology | Purpose |
|------------|---------|
| **Java 17+** | Language |
| **Spring Boot 3.x** | Framework |
| **Spring Security + JWT** | Authentication (with refresh tokens) |
| **Spring Data JPA + Hibernate** | ORM / Database access |
| **MySQL 8.x** | Production database |
| **H2** | Development/test database |
| **Maven** | Build tool |
| **Lombok** | Reduce boilerplate |
| **MapStruct** | Entity ↔ DTO mapping |
| **Swagger/OpenAPI 3** | API documentation |
| **Validation (Jakarta)** | Input validation |

### Frontend (Separate SPA)
| Technology | Purpose |
|------------|---------|
| **React 18+** or **Vue 3** | UI Framework |
| **Tailwind CSS** | Styling |
| **Axios** | HTTP client |
| **React Router / Vue Router** | Navigation |
| **Recharts / Chart.js** | Dashboard charts |

### Infrastructure
| Tool | Purpose |
|------|---------|
| **Docker + Docker Compose** | Local development environment |
| **Flyway** | Database migrations (chosen over Liquibase for simplicity) |
| **GitHub Actions** | CI/CD (optional) |

## 4. High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    FRONTEND (SPA)                       │
│              React / Vue + Tailwind CSS                 │
│                                                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐              │
│  │ Dashboard │  │Special   │  │Daladala  │  ┌────────┐ │
│  │          │  │Hire      │  │          │  │Private │ │
│  │          │  │Module    │  │Module    │  │Cars    │ │
│  └──────────┘  └──────────┘  └──────────┘  └────────┘ │
└────────────────────────┬────────────────────────────────┘
                         │  HTTP (REST API + JWT)
                         ▼
┌─────────────────────────────────────────────────────────┐
│                  SPRING BOOT BACKEND                    │
│                                                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │              Security Filter Chain               │   │
│  │    (Rate Limiting → JWT Auth → Authorization)    │   │
│  └─────────────────────────────────────────────────┘   │
│                                                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐             │
│  │Controller│  │Controller│  │Controller│  ...         │
│  │  Layer   │  │  Layer   │  │  Layer   │             │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘             │
│       ▼              ▼              ▼                   │
│  ┌─────────────────────────────────────────────────┐   │
│  │               Service Layer                      │   │
│  │     (Business Logic + Transaction Management)    │   │
│  └─────────────────────────────────────────────────┘   │
│       ▼                                                 │
│  ┌─────────────────────────────────────────────────┐   │
│  │           Repository Layer (JPA)                 │   │
│  └─────────────────────────────────────────────────┘   │
│       ▼                                                 │
│  ┌─────────────────────────────────────────────────┐   │
│  │                  MySQL Database                  │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

## 5. Project Structure (Backend)

```
mama-mpoki-car-hire/
├── src/
│   ├── main/
│   │   ├── java/com/mamampoki/carhire/
│   │   │   ├── MamaMpokiApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── RateLimitConfig.java
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── OwnerDetailsService.java
│   │   │   │   └── RateLimitingFilter.java
│   │   │   ├── auth/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── AuthRequest.java
│   │   │   │   ├── AuthResponse.java
│   │   │   │   ├── RefreshTokenRequest.java
│   │   │   │   └── ChangePasswordRequest.java
│   │   │   ├── owner/
│   │   │   │   ├── Owner.java (entity)
│   │   │   │   ├── OwnerRepository.java
│   │   │   │   ├── OwnerService.java
│   │   │   │   └── OwnerController.java
│   │   │   ├── vehicle/
│   │   │   │   ├── Vehicle.java (entity)
│   │   │   │   ├── VehicleType.java (enum)
│   │   │   │   ├── ModuleType.java (enum)
│   │   │   │   ├── VehicleStatus.java (enum)
│   │   │   │   ├── VehicleRepository.java
│   │   │   │   ├── VehicleService.java
│   │   │   │   ├── VehicleController.java
│   │   │   │   └── dto/
│   │   │   ├── driver/
│   │   │   │   ├── Driver.java (entity)
│   │   │   │   ├── DriverRepository.java
│   │   │   │   ├── DriverService.java
│   │   │   │   ├── DriverController.java
│   │   │   │   └── dto/
│   │   │   ├── conductor/
│   │   │   │   ├── Conductor.java (entity)
│   │   │   │   ├── ConductorRepository.java
│   │   │   │   ├── ConductorService.java
│   │   │   │   ├── ConductorController.java
│   │   │   │   └── dto/
│   │   │   ├── customer/
│   │   │   │   ├── Customer.java (entity)
│   │   │   │   ├── CustomerRepository.java
│   │   │   │   ├── CustomerService.java
│   │   │   │   └── CustomerController.java
│   │   │   ├── specialhire/
│   │   │   │   ├── HireBooking.java (entity)
│   │   │   │   ├── Trip.java (entity)
│   │   │   │   ├── TripExpense.java (entity)
│   │   │   │   ├── Payment.java (entity)
│   │   │   │   ├── BookingStatus.java (enum)
│   │   │   │   ├── TripStatus.java (enum)
│   │   │   │   ├── SpecialHireService.java
│   │   │   │   ├── SpecialHireController.java
│   │   │   │   └── dto/
│   │   │   ├── daladala/
│   │   │   │   ├── Route.java (entity)
│   │   │   │   ├── DailyOperation.java (entity)
│   │   │   │   ├── DailyRevenue.java (entity)
│   │   │   │   ├── DailyExpense.java (entity)
│   │   │   │   ├── DaladalaService.java
│   │   │   │   ├── DaladalaController.java
│   │   │   │   └── dto/
│   │   │   ├── privatecar/
│   │   │   │   ├── PrivateCar.java (entity — extends Vehicle)
│   │   │   │   ├── PrivateCarService.java
│   │   │   │   ├── PrivateCarController.java
│   │   │   │   └── dto/
│   │   │   ├── fuel/
│   │   │   │   ├── FuelRecord.java (entity)
│   │   │   │   ├── FuelService.java
│   │   │   │   ├── FuelController.java
│   │   │   │   └── dto/
│   │   │   ├── maintenance/
│   │   │   │   ├── MaintenanceRecord.java (entity)
│   │   │   │   ├── MaintenanceService.java
│   │   │   │   ├── MaintenanceController.java
│   │   │   │   └── dto/
│   │   │   ├── report/
│   │   │   │   ├── ReportController.java
│   │   │   │   ├── ReportService.java
│   │   │   │   └── dto/
│   │   │   ├── dashboard/
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── DashboardService.java
│   │   │   │   └── dto/
│   │   │   ├── notification/
│   │   │   │   ├── ExpiryNotificationService.java
│   │   │   │   └── dto/
│   │   │   ├── common/
│   │   │   │   ├── BaseEntity.java
│   │   │   │   ├── SoftDeletableEntity.java
│   │   │   │   ├── ApiResponse.java
│   │   │   │   ├── PaginatedResponse.java
│   │   │   │   ├── CurrencyUtils.java
│   │   │   │   └── enums/
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── ResourceNotFoundException.java
│   │   │       ├── BadRequestException.java
│   │   │       └── RateLimitExceededException.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/
│   │           ├── V1__create_owner_table.sql
│   │           ├── V2__create_vehicles_table.sql
│   │           ├── V3__create_drivers_table.sql
│   │           ├── V4__create_conductors_table.sql
│   │           └── ...
│   └── test/
│       └── java/com/mamampoki/carhire/
│           ├── MamaMpokiApplicationTests.java
│           └── ...
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── docs/
    ├── 01-system-overview.md
    ├── 02-database-schema.md
    ├── 03-api-specifications.md
    ├── 04-authentication.md
    ├── 05-module-breakdown.md
    └── 06-development-roadmap.md
```

### Base Entity Classes

```java
// BaseEntity.java — Standard fields for all entities
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

// SoftDeletableEntity.java — Adds soft delete support
@MappedSuperclass
public abstract class SoftDeletableEntity extends BaseEntity {

    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void softDelete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
```

## 6. Currency & Locale

- **Currency**: TZS (Tanzanian Shilling)
- **Formatting**: Amounts displayed with comma separators (e.g., 850,000 TZS)
- **API**: All monetary values returned as numbers (e.g., `850000.00`), not formatted strings
- **Precision**: `DECIMAL(12,2)` allows up to 99,999,999,999.99 TZS

## 7. Enum Mapping Convention

All JPA entities must use `@Enumerated(EnumType.STRING)` for MySQL ENUM columns:

```java
@Column(name = "vehicle_type", nullable = false)
@Enumerated(EnumType.STRING)
private VehicleType vehicleType;
```

This stores the enum name as a string in MySQL (e.g., `'COASTER'`) rather than an ordinal integer, which prevents data corruption if enum values are reordered.

## 8. Future Considerations (v2+)

- Multi-user roles (manager, accountant, driver app)
- SMS notifications for vehicle document expiry
- M-Pesa payment integration
- Driver mobile app for trip updates
- Fuel price tracking from BOT data
- Vehicle GPS tracking integration
