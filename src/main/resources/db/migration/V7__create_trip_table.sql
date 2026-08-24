-- V7: Create trip table
-- Mama Mpoki Car Hire - Special hire trip management

CREATE TABLE trip (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id      BIGINT NOT NULL,
    driver_id       BIGINT NOT NULL,
    vehicle_id      BIGINT NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE,
    destination     VARCHAR(200),
    actual_price    DECIMAL(12,2) COMMENT 'Actual trip price in TZS (may differ from booking)',
    odometer_start  INT,
    odometer_end    INT,
    status          VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' COMMENT 'SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED',
    notes           TEXT,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_trip_booking FOREIGN KEY (booking_id) REFERENCES hire_booking(id),
    CONSTRAINT fk_trip_driver FOREIGN KEY (driver_id) REFERENCES driver(id),
    CONSTRAINT fk_trip_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(id),
    CONSTRAINT chk_trip_status CHECK (status IN ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_trip_dates CHECK (end_date IS NULL OR end_date >= start_date),
    CONSTRAINT chk_trip_odometer CHECK (odometer_end IS NULL OR odometer_start IS NULL OR odometer_end >= odometer_start)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
