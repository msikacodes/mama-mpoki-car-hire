# MAMA MPOKI CAR HIRE — Database Schema Design

## Conventions

### Soft Delete
All entities use a soft delete strategy. Records are never hard-deleted.

```java
@Column(name = "deleted")
private boolean deleted = false;

@Column(name = "deleted_at")
private LocalDateTime deletedAt;
```

When querying, always filter: `WHERE deleted = false`

### Timestamps
All tables have `created_at` and `updated_at` columns, managed by JPA auditing.

### Enum Mapping
All enum columns use `@Enumerated(EnumType.STRING)` to store enum names as strings, not ordinal integers.

### Currency
All monetary columns use `DECIMAL(12,2)` for TZS. Values are stored as-is (e.g., 850000.00).

---

## 1. Entity Relationship Diagram (Text)

```
┌──────────────┐
│    OWNER     │
├──────────────┤
│ id (PK)      │
│ username     │
│ password     │
│ fullName     │
│ phone        │
│ email        │
│ createdAt    │
│ updatedAt    │
└──────┬───────┘
       │ 1:N (owner manages everything)
       │
       ├──►┌──────────────┐    ┌──────────────────┐
       │   │   VEHICLE    │───►│  MAINTENANCE     │
       │   ├──────────────┤    │  RECORD          │
       │   │ id (PK)      │    ├──────────────────┤
       │   │ owner_id(FK) │    │ id (PK)          │
       │   │ vehicleType  │    │ vehicle_id (FK)  │
       │   │ (ENUM)       │    │ date             │
       │   │ make         │    │ description      │
       │   │ model        │    │ cost             │
       │   │ year         │    │ type (enum)      │
       │   │ regNumber    │    └──────────────────┘
       │   │ color        │
       │   │ capacity     │         ┌──────────────┐
       │   │ fuelType     │────────►│  FUEL RECORD │
       │   │ status       │         ├──────────────┤
       │   │ moduleType   │         │ id (PK)      │
       │   │ (ENUM)       │         │ vehicle_id   │
       │   │ deleted      │         │ date         │
       │   │ deletedAt    │         │ liters       │
       │   └──────┬───────┘         │ cost/liter   │
       │          │                 │ totalCost    │
       │          │                 │ odometer     │
       │          │                 └──────────────┘
       │          │
       │          ├─── SPECIAL HIRE branch ──────┐
       │          │                              │
       │          ▼                              ▼
       │   ┌──────────────┐           ┌──────────────────┐
       │   │ HIRE BOOKING │           │      TRIP        │
       │   ├──────────────┤    1:N    ├──────────────────┤
       │   │ id (PK)      │──────────►│ id (PK)          │
       │   │ vehicle_id   │           │ booking_id (FK)  │
       │   │ customer_id  │           │ driver_id (FK)   │
       │   │ hireDate     │           │ startDate        │
       │   │ endDate      │           │ endDate          │
       │   │ destination  │           │ destination      │
       │   │ agreedPrice  │           │ actualPrice      │
       │   │ status       │           │ status           │
       │   │ deleted      │           │ odometerStart    │
       │   └──────┬───────┘           │ odometerEnd      │
       │          │                   └────────┬─────────┘
       │          │                            │ 1:N
       │          ▼                   ┌────────▼─────────┐
       │   ┌──────────────┐          │   TRIP EXPENSE   │
       │   │   CUSTOMER   │          ├──────────────────┤
       │   ├──────────────┤          │ id (PK)          │
       │   │ id (PK)      │          │ trip_id (FK)     │
       │   │ name         │          │ expenseType      │
       │   │ phone        │          │ amount           │
       │   │ idType       │          │ description      │
       │   │ deleted      │          │ date             │
       │   └──────────────┘          └──────────────────┘
       │
       │          ┌──────────────┐           ┌──────────────────┐
       │          │    ROUTE     │           │ DAILY OPERATION  │
       │          ├──────────────┤    1:N    ├──────────────────┤
       └─────────►│ id (PK)      │──────────►│ id (PK)          │
                  │ owner_id(FK) │           │ vehicle_id (FK)  │
                  │ name         │           │ route_id (FK)    │
                  │ startPoint   │           │ driver_id (FK)   │
                  │ endPoint     │           │ conductor_id(FK) │
                  │ distance     │           │ date             │
                  │ fare         │           │ departureTime    │
                  │ deleted      │           │ returnTime       │
                  └──────────────┘           │ totalPassengers  │
                                             │ status           │
                                             └────────┬─────────┘
                                                      │ 1:N
                               ┌───────────────────────┼───────────────────┐
                               ▼                       ▼                   ▼
                  ┌──────────────────┐   ┌──────────────────┐  ┌──────────────────┐
                  │  DAILY REVENUE   │   │  DAILY EXPENSE   │  │  DRIVER EARNING  │
                  ├──────────────────┤   ├──────────────────┤  ├──────────────────┤
                  │ id (PK)          │   │ id (PK)          │  │ id (PK)          │
                  │ operation_id(FK) │   │ operation_id(FK) │  │ operation_id(FK) │
                  │ source (ENUM)    │   │ expenseType(ENUM)│  │ driver_id (FK)   │
                  │ amount           │   │ amount           │  │ amount           │
                  │ description      │   │ description      │  │ date             │
                  │ date             │   │ date             │  └──────────────────┘
                  └──────────────────┘   └──────────────────┘

       │          ┌──────────────┐
       │          │  PRIVATE CAR │  (extends VEHICLE — own table)
       │          ├──────────────┤
       └─────────►│ id (PK)      │
                  │ vehicle_id   │
                  │ insuranceNo  │
                  │ insuranceExpiry│
                  │ registrationExpiry│
                  │ inspectionDate│
                  │ lastServiceDate│
                  │ notes        │
                  └──────────────┘
```

## 2. Entity Definitions

### 2.1 OWNER (Authentication)

```sql
CREATE TABLE owner (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50) UNIQUE NOT NULL,
    password        VARCHAR(255) NOT NULL,       -- BCrypt hashed
    full_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(20),
    email           VARCHAR(100),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 2.2 VEHICLE (Shared base entity)

```sql
CREATE TABLE vehicle (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id        BIGINT NOT NULL,
    vehicle_type    ENUM('COASTER', 'MINIBUS', 'DALADALA_BUS', 'PRIVATE_CAR', 'OTHER') NOT NULL,
    module_type     ENUM('SPECIAL_HIRE', 'DALADALA', 'PRIVATE') NOT NULL,
    make            VARCHAR(50),                 -- e.g., Toyota, Nissan
    model           VARCHAR(50),                 -- e.g., HiAce, Urvan
    year            INT,
    reg_number      VARCHAR(20) UNIQUE NOT NULL, -- Tanzania plate e.g., T 123 ABC
    color           VARCHAR(30),
    capacity        INT,                         -- passenger capacity
    fuel_type       ENUM('DIESEL', 'PETROL', 'CNG', 'ELECTRIC') DEFAULT 'DIESEL',
    status          ENUM('ACTIVE', 'INACTIVE', 'MAINTENANCE', 'RETIRED') DEFAULT 'ACTIVE',
    photo_url       VARCHAR(500),
    notes           TEXT,
    deleted         BOOLEAN DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES owner(id)
);
```

### 2.3 DRIVER

```sql
CREATE TABLE driver (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id        BIGINT NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(20) NOT NULL,
    license_number  VARCHAR(50),
    license_expiry  DATE,
    national_id     VARCHAR(50),
    address         VARCHAR(200),
    daily_rate      DECIMAL(10,2),               -- default daily allowance
    status          ENUM('ACTIVE', 'INACTIVE', 'ON_LEAVE') DEFAULT 'ACTIVE',
    notes           TEXT,
    deleted         BOOLEAN DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES owner(id)
);
```

### 2.4 CONDUCTOR (Daladala-specific staff)

```sql
CREATE TABLE conductor (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id        BIGINT NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(20),
    national_id     VARCHAR(50),
    address         VARCHAR(200),
    daily_rate      DECIMAL(10,2),               -- default daily earning
    status          ENUM('ACTIVE', 'INACTIVE', 'ON_LEAVE') DEFAULT 'ACTIVE',
    notes           TEXT,
    deleted         BOOLEAN DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES owner(id)
);
```

> **Why a separate Conductor entity?** In daladala operations, conductors are responsible for collecting fares and managing passengers. They are distinct from drivers and need their own performance tracking. Storing them as a text field loses this capability.

### 2.5 CUSTOMER (Special Hire)

```sql
CREATE TABLE customer (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id        BIGINT NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(20) NOT NULL,
    email           VARCHAR(100),
    address         VARCHAR(200),
    id_type         ENUM('NATIONAL_ID', 'PASSPORT', 'DRIVING_LICENSE', 'OTHER'),
    id_number       VARCHAR(50),
    notes           TEXT,
    deleted         BOOLEAN DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES owner(id)
);
```

### 2.6 HIRE BOOKING (Special Hire)

```sql
CREATE TABLE hire_booking (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id        BIGINT NOT NULL,
    vehicle_id      BIGINT NOT NULL,
    customer_id     BIGINT,
    hire_date       DATE NOT NULL,
    end_date        DATE,
    destination     VARCHAR(200),
    trip_purpose    VARCHAR(200),                -- e.g., Wedding, Corporate, Tour
    agreed_price    DECIMAL(12,2) NOT NULL,      -- TZS
    deposit_paid    DECIMAL(12,2) DEFAULT 0,     -- TZS
    status          ENUM('PENDING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    notes           TEXT,
    deleted         BOOLEAN DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES owner(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);
```

### 2.7 TRIP (Special Hire)

```sql
CREATE TABLE trip (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id      BIGINT NOT NULL,
    driver_id       BIGINT NOT NULL,
    vehicle_id      BIGINT NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE,
    destination     VARCHAR(200),
    actual_price    DECIMAL(12,2),               -- TZS (may differ from agreed_price)
    odometer_start  INT,
    odometer_end    INT,
    status          ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    notes           TEXT,
    deleted         BOOLEAN DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES hire_booking(id),
    FOREIGN KEY (driver_id) REFERENCES driver(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id)
);
```

### 2.8 TRIP EXPENSE

```sql
CREATE TABLE trip_expense (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    trip_id         BIGINT NOT NULL,
    expense_type    ENUM('FUEL', 'DRIVER_ALLOWANCE', 'TOLL', 'FOOD', 'ACCOMMODATION', 'REPAIR', 'OTHER') NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,      -- TZS
    description     VARCHAR(200),
    expense_date    DATE NOT NULL,
    deleted         BOOLEAN DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (trip_id) REFERENCES trip(id)
);
```

### 2.9 PAYMENT (Special Hire)

```sql
CREATE TABLE payment (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id      BIGINT NOT NULL,
    amount          DECIMAL(12,2) NOT NULL,      -- TZS
    payment_method  ENUM('CASH', 'MOBILE_MONEY', 'BANK_TRANSFER', 'CHEQUE', 'OTHER') NOT NULL,
    payment_date    DATE NOT NULL,
    reference_number VARCHAR(100),               -- e.g., M-Pesa confirmation code
    notes           TEXT,
    deleted         BOOLEAN DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES hire_booking(id)
);
```

### 2.10 ROUTE (Daladala)

```sql
CREATE TABLE route (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id        BIGINT NOT NULL,
    name            VARCHAR(100) NOT NULL,        -- e.g., "Dodoma Town - Ihumwa"
    start_point     VARCHAR(100) NOT NULL,
    end_point       VARCHAR(100) NOT NULL,
    distance_km     DECIMAL(6,2),
    fare_amount     DECIMAL(8,2) NOT NULL,        -- standard fare per passenger (TZS)
    status          ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    notes           TEXT,
    deleted         BOOLEAN DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES owner(id)
);
```

### 2.11 DAILY OPERATION (Daladala)

```sql
CREATE TABLE daily_operation (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id      BIGINT NOT NULL,
    route_id        BIGINT NOT NULL,
    driver_id       BIGINT,
    conductor_id    BIGINT,                      -- references conductor table
    operation_date  DATE NOT NULL,
    departure_time  TIME,
    return_time     TIME,
    total_passengers INT DEFAULT 0,
    status          ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    notes           TEXT,
    deleted         BOOLEAN DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id),
    FOREIGN KEY (route_id) REFERENCES route(id),
    FOREIGN KEY (driver_id) REFERENCES driver(id),
    FOREIGN KEY (conductor_id) REFERENCES conductor(id)
);
```

### 2.12 DAILY REVENUE (Daladala)

```sql
CREATE TABLE daily_revenue (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    operation_id    BIGINT NOT NULL,
    source          ENUM('FARE', 'CHARTER', 'ADVERTISING', 'OTHER') NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,      -- TZS
    description     VARCHAR(200),
    revenue_date    DATE NOT NULL,
    deleted         BOOLEAN DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (operation_id) REFERENCES daily_operation(id)
);
```

### 2.13 DAILY EXPENSE (Daladala)

```sql
CREATE TABLE daily_expense (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    operation_id    BIGINT NOT NULL,
    expense_type    ENUM('FUEL', 'REPAIR', 'TOLL', 'MAINTENANCE', 'INSURANCE', 'CONDUCTOR_ALLOWANCE', 'OTHER') NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,      -- TZS
    description     VARCHAR(200),
    expense_date    DATE NOT NULL,
    deleted         BOOLEAN DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (operation_id) REFERENCES daily_operation(id)
);
```

### 2.14 FUEL RECORD (Shared across all vehicles)

```sql
CREATE TABLE fuel_record (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id      BIGINT NOT NULL,
    fuel_date       DATE NOT NULL,
    liters          DECIMAL(8,2) NOT NULL,
    cost_per_liter  DECIMAL(8,2) NOT NULL,       -- TZS per liter (required)
    total_cost      DECIMAL(10,2) NOT NULL,      -- TZS (= liters × cost_per_liter)
    odometer        INT,
    station         VARCHAR(100),
    notes           TEXT,
    deleted         BOOLEAN DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id)
);
```

> **Validation rule**: `total_cost` must equal `liters × cost_per_liter`. Enforce in service layer. `cost_per_liter` is now **required** (NOT NULL) to ensure consistent calculations.

### 2.15 MAINTENANCE RECORD (Shared across all vehicles)

```sql
CREATE TABLE maintenance_record (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id      BIGINT NOT NULL,
    maintenance_date DATE NOT NULL,
    maintenance_type ENUM('SERVICE', 'REPAIR', 'INSPECTION', 'OIL_CHANGE', 'TIRE_CHANGE', 'BRAKE', 'OTHER') NOT NULL,
    description     TEXT NOT NULL,
    cost            DECIMAL(10,2),               -- TZS
    garage_name     VARCHAR(100),
    odometer        INT,
    next_service_date DATE,
    notes           TEXT,
    deleted         BOOLEAN DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id)
);
```

### 2.16 PRIVATE CAR (Extended info — own table)

```sql
CREATE TABLE private_car (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id          BIGINT UNIQUE NOT NULL,
    insurance_number    VARCHAR(50),
    insurance_provider  VARCHAR(100),
    insurance_expiry    DATE,
    registration_expiry DATE,
    inspection_date     DATE,
    last_service_date   DATE,
    annual_mileage      INT,
    notes               TEXT,
    deleted             BOOLEAN DEFAULT FALSE,
    deleted_at          TIMESTAMP NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id)
);
```

> **Why keep private_car as a separate table?** Insurance and registration details are specific to private cars and don't apply to special hire or daladala vehicles. A separate table avoids nullable columns on the vehicle table and keeps the schema clean.

---

## 3. Key Relationships Summary

| Relationship | Type | Description |
|-------------|------|-------------|
| Owner → Vehicle | 1:N | Owner manages many vehicles |
| Owner → Driver | 1:N | Owner manages many drivers |
| Owner → Conductor | 1:N | Owner manages many conductors |
| Owner → Customer | 1:N | Owner records many customers |
| Owner → Route | 1:N | Owner defines many routes |
| Vehicle → FuelRecord | 1:N | Vehicle has many fuel records |
| Vehicle → MaintenanceRecord | 1:N | Vehicle has many maintenance records |
| Vehicle → PrivateCar | 1:1 | Private car extends vehicle |
| HireBooking → Trip | 1:N | A booking can have multiple trips |
| HireBooking → Payment | 1:N | A booking has multiple payments |
| Trip → TripExpense | 1:N | A trip has multiple expenses |
| DailyOperation → DailyRevenue | 1:N | An operation has multiple revenue entries |
| DailyOperation → DailyExpense | 1:N | An operation has multiple expenses |
| Route → DailyOperation | 1:N | A route has many daily operations |
| Vehicle → DailyOperation | 1:N | A vehicle has many daily operations |
| Driver → DailyOperation | 1:N | A driver has many daily operations |
| Conductor → DailyOperation | 1:N | A conductor has many daily operations |

## 4. Indexes (Performance)

```sql
-- Vehicle queries
CREATE INDEX idx_vehicle_owner ON vehicle(owner_id);
CREATE INDEX idx_vehicle_module ON vehicle(module_type);
CREATE INDEX idx_vehicle_reg ON vehicle(reg_number);
CREATE INDEX idx_vehicle_status ON vehicle(status);
CREATE INDEX idx_vehicle_deleted ON vehicle(deleted);

-- Driver queries
CREATE INDEX idx_driver_owner ON driver(owner_id);
CREATE INDEX idx_driver_deleted ON driver(deleted);

-- Conductor queries
CREATE INDEX idx_conductor_owner ON conductor(owner_id);
CREATE INDEX idx_conductor_deleted ON conductor(deleted);

-- Customer queries
CREATE INDEX idx_customer_owner ON customer(owner_id);
CREATE INDEX idx_customer_deleted ON customer(deleted);

-- Booking queries (critical for availability checks)
CREATE INDEX idx_booking_vehicle ON hire_booking(vehicle_id);
CREATE INDEX idx_booking_customer ON hire_booking(customer_id);
CREATE INDEX idx_booking_owner ON hire_booking(owner_id);
CREATE INDEX idx_booking_status ON hire_booking(status);
CREATE INDEX idx_booking_hire_date ON hire_booking(hire_date);
CREATE INDEX idx_booking_end_date ON hire_booking(end_date);
CREATE INDEX idx_booking_deleted ON hire_booking(deleted);

-- Trip queries
CREATE INDEX idx_trip_booking ON trip(booking_id);
CREATE INDEX idx_trip_driver ON trip(driver_id);
CREATE INDEX idx_trip_vehicle ON trip(vehicle_id);
CREATE INDEX idx_trip_status ON trip(status);
CREATE INDEX idx_trip_deleted ON trip(deleted);

-- Payment queries
CREATE INDEX idx_payment_booking ON payment(booking_id);
CREATE INDEX idx_payment_deleted ON payment(deleted);

-- Daily operation queries
CREATE INDEX idx_daily_op_vehicle ON daily_operation(vehicle_id);
CREATE INDEX idx_daily_op_route ON daily_operation(route_id);
CREATE INDEX idx_daily_op_driver ON daily_operation(driver_id);
CREATE INDEX idx_daily_op_conductor ON daily_operation(conductor_id);
CREATE INDEX idx_daily_op_date ON daily_operation(operation_date);
CREATE INDEX idx_daily_op_deleted ON daily_operation(deleted);

-- Fuel queries
CREATE INDEX idx_fuel_vehicle ON fuel_record(vehicle_id);
CREATE INDEX idx_fuel_date ON fuel_record(fuel_date);
CREATE INDEX idx_fuel_deleted ON fuel_record(deleted);

-- Maintenance queries
CREATE INDEX idx_maintenance_vehicle ON maintenance_record(vehicle_id);
CREATE INDEX idx_maintenance_deleted ON maintenance_record(deleted);

-- Private car queries
CREATE INDEX idx_private_car_vehicle ON private_car(vehicle_id);
CREATE INDEX idx_private_car_deleted ON private_car(deleted);

-- Document expiry queries (for alerts)
CREATE INDEX idx_private_car_insurance_expiry ON private_car(insurance_expiry);
CREATE INDEX idx_private_car_registration_expiry ON private_car(registration_expiry);
```

## 5. JPA Entity Example

```java
@Entity
@Table(name = "vehicle")
@Data
@EqualsAndHashCode(callSuper = true)
public class Vehicle extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "vehicle_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Column(name = "module_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ModuleType moduleType;

    @Column(name = "make")
    private String make;

    @Column(name = "model")
    private String model;

    @Column(name = "year")
    private Integer year;

    @Column(name = "reg_number", unique = true, nullable = false)
    private String regNumber;

    @Column(name = "color")
    private String color;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "fuel_type")
    @Enumerated(EnumType.STRING)
    private FuelType fuelType = FuelType.DIESEL;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private VehicleStatus status = VehicleStatus.ACTIVE;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "notes")
    private String notes;
}
```

## 6. Repository with Soft Delete Support

```java
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // Custom query to exclude soft-deleted records
    @Query("SELECT v FROM Vehicle v WHERE v.deleted = false AND v.owner.id = :ownerId")
    List<Vehicle> findByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT v FROM Vehicle v WHERE v.deleted = false AND v.moduleType = :moduleType")
    List<Vehicle> findByModuleType(@Param("moduleType") ModuleType moduleType);

    // Vehicle availability check for bookings
    @Query("SELECT v FROM Vehicle v WHERE v.deleted = false AND v.status = 'ACTIVE' " +
           "AND v.moduleType = 'SPECIAL_HIRE' " +
           "AND v.id NOT IN (" +
           "  SELECT hb.vehicle.id FROM HireBooking hb " +
           "  WHERE hb.deleted = false " +
           "  AND hb.status NOT IN ('CANCELLED', 'COMPLETED') " +
           "  AND hb.hireDate <= :endDate AND hb.endDate >= :startDate" +
           ")")
    List<Vehicle> findAvailableVehiclesForDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
}
```
