-- V16: Create private_car table
-- Mama Mpoki Car Hire - Private car extended information

CREATE TABLE private_car (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id          BIGINT NOT NULL,
    insurance_number    VARCHAR(50),
    insurance_provider  VARCHAR(100),
    insurance_expiry    DATE,
    registration_expiry DATE,
    inspection_date     DATE,
    last_service_date   DATE,
    annual_mileage      INT,
    notes               TEXT,
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at          TIMESTAMP NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_private_car_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(id),
    CONSTRAINT uk_private_car_vehicle UNIQUE (vehicle_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
