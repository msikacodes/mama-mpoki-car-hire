-- V11: Create daily_operation table
-- Mama Mpoki Car Hire - Daladala daily operation tracking

CREATE TABLE daily_operation (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id      BIGINT NOT NULL,
    route_id        BIGINT NOT NULL,
    driver_id       BIGINT,
    conductor_id    BIGINT,
    operation_date  DATE NOT NULL,
    departure_time  TIME,
    return_time     TIME,
    total_passengers INT NOT NULL DEFAULT 0,
    status          VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' COMMENT 'SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED',
    notes           TEXT,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_operation_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(id),
    CONSTRAINT fk_operation_route FOREIGN KEY (route_id) REFERENCES route(id),
    CONSTRAINT fk_operation_driver FOREIGN KEY (driver_id) REFERENCES driver(id),
    CONSTRAINT fk_operation_conductor FOREIGN KEY (conductor_id) REFERENCES conductor(id),
    CONSTRAINT chk_operation_status CHECK (status IN ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_operation_passengers CHECK (total_passengers >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
