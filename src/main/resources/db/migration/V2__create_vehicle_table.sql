-- V2: Create vehicle table
-- Mama Mpoki Car Hire - Shared vehicle entity

CREATE TABLE vehicle (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id        BIGINT NOT NULL,
    vehicle_type    VARCHAR(20) NOT NULL COMMENT 'COASTER, MINIBUS, DALADALA_BUS, PRIVATE_CAR, OTHER',
    module_type     VARCHAR(20) NOT NULL COMMENT 'SPECIAL_HIRE, DALADALA, PRIVATE',
    make            VARCHAR(50),
    model           VARCHAR(50),
    year            INT,
    reg_number      VARCHAR(20) NOT NULL COMMENT 'Tanzania plate e.g. T 123 ABC',
    color           VARCHAR(30),
    capacity        INT COMMENT 'Passenger capacity',
    fuel_type       VARCHAR(10) NOT NULL DEFAULT 'DIESEL' COMMENT 'DIESEL, PETROL, CNG, ELECTRIC',
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, INACTIVE, MAINTENANCE, RETIRED',
    photo_url       VARCHAR(500),
    notes           TEXT,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_vehicle_owner FOREIGN KEY (owner_id) REFERENCES owner(id),
    CONSTRAINT uk_vehicle_reg_number UNIQUE (reg_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add check constraints for enums
ALTER TABLE vehicle
    ADD CONSTRAINT chk_vehicle_type CHECK (vehicle_type IN ('COASTER', 'MINIBUS', 'DALADALA_BUS', 'PRIVATE_CAR', 'OTHER')),
    ADD CONSTRAINT chk_module_type CHECK (module_type IN ('SPECIAL_HIRE', 'DALADALA', 'PRIVATE')),
    ADD CONSTRAINT chk_fuel_type CHECK (fuel_type IN ('DIESEL', 'PETROL', 'CNG', 'ELECTRIC')),
    ADD CONSTRAINT chk_vehicle_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE', 'RETIRED'));
