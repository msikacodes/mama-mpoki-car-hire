-- V14: Create fuel_record table
-- Mama Mpoki Car Hire - Fuel tracking across all vehicles

CREATE TABLE fuel_record (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id      BIGINT NOT NULL,
    fuel_date       DATE NOT NULL,
    liters          DECIMAL(8,2) NOT NULL,
    cost_per_liter  DECIMAL(8,2) NOT NULL COMMENT 'Price per liter in TZS',
    total_cost      DECIMAL(10,2) NOT NULL COMMENT 'Total cost in TZS (= liters × cost_per_liter)',
    odometer        INT,
    station         VARCHAR(100),
    notes           TEXT,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_fuel_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(id),
    CONSTRAINT chk_fuel_liters CHECK (liters > 0),
    CONSTRAINT chk_fuel_cost_per_liter CHECK (cost_per_liter > 0),
    CONSTRAINT chk_fuel_total_cost CHECK (total_cost > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
