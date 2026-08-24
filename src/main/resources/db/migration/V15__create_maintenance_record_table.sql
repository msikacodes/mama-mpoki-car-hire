-- V15: Create maintenance_record table
-- Mama Mpoki Car Hire - Maintenance tracking across all vehicles

CREATE TABLE maintenance_record (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id      BIGINT NOT NULL,
    maintenance_date DATE NOT NULL,
    maintenance_type VARCHAR(20) NOT NULL COMMENT 'SERVICE, REPAIR, INSPECTION, OIL_CHANGE, TIRE_CHANGE, BRAKE, OTHER',
    description     TEXT NOT NULL,
    cost            DECIMAL(10,2) COMMENT 'Maintenance cost in TZS',
    garage_name     VARCHAR(100),
    odometer        INT,
    next_service_date DATE,
    notes           TEXT,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_maintenance_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(id),
    CONSTRAINT chk_maintenance_type CHECK (maintenance_type IN ('SERVICE', 'REPAIR', 'INSPECTION', 'OIL_CHANGE', 'TIRE_CHANGE', 'BRAKE', 'OTHER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
